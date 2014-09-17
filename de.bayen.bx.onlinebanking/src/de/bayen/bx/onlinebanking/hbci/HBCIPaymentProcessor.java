/******************************************************************************
 * Copyright (C) 2014 Thomas Bayen                                            *
 * Copyright (C) 2014 Jakob Bayen KG             							  *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package de.bayen.bx.onlinebanking.hbci;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.Callback;
import org.adempiere.util.IProcessUI;
import org.compiere.model.I_C_BankAccount;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_PaySelectionCheck;
import org.compiere.model.I_C_Payment;
import org.compiere.model.MBPBankAccount;
import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatementLoader;
import org.compiere.model.MOrg;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MPaySelectionLine;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.kapott.hbci.GV.AbstractSEPAGV;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.HBCIJobResult;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIStatus;
import org.kapott.hbci.structures.Konto;

import de.bayen.bx.onlinebanking.model.MBPBankAccountHelper;
import de.bayen.bx.onlinebanking.model.MBPBankAccountHelper.SepaSddScheme;
import de.bayen.bx.onlinebanking.model.MOrgHelper;
import de.bayen.bx.onlinebanking.model.X_BAY_HBCILog;
import de.bayen.bx.util.IBANUtil;

/**
 * Implementierung eines Payment-Processors, der per HBCI einen Sammler von
 * Zahlungen (Überweisungen oder Lastschriften) zur Bank sendet.
 * 
 * Diese Klasse wird aktiviert, indem sie im Bankkonto als PaymentExport-Klasse
 * angegeben wird. Ich nehme allerdings die Zugangsdaten aus dem
 * BankStatementLoader desselben Kontos, der also auch eingerichtet sein muss.
 * Ich gehe mal davon aus das jemand, der über sein Konto per elektronischer
 * Transaktionen verfügen möchte, vorher auch das Einlesen von Kontoauszügen
 * einrichten möchte. Man muss es aber nicht nutzen, wenn man nicht will.
 * 
 * @author tbayen
 */
public class HBCIPaymentProcessor {

	static private class Job {
		HBCIJob hbciJob;
		BigDecimal total;
		int count;
		public String jobName;
	}

	static final DateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	static final DecimalFormat amountFormatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
	static final boolean onlyTest = false; // only for debugging

	private Properties m_ctx;
	private String m_trxName;
	private boolean m_isDebitPayment;
	private IProcessUI m_processUI;

	private MyHBCICallback m_callback;
	private HBCIHandler hbciHandle;
	private Job allJobs[];
	private MOrg m_org;
	private boolean m_isDebit;

	//@Override
	public boolean init(Properties ctx, I_C_BankAccount C_BankAccount, boolean isDebitPayment, IProcessUI processUI,
			String trxName) {
		this.m_ctx = ctx;
		this.m_isDebitPayment = isDebitPayment;
		this.m_processUI = processUI;
		this.m_trxName = trxName;
		if(C_BankAccount == null)
			throw new AdempiereException("bank not found in HBCI processor");
		
		String whereClause = MBankAccount.COLUMNNAME_C_BankAccount_ID + "=? " + " AND isActive='Y'";
		Query query = new Query(ctx, MBankStatementLoader.Table_Name, whereClause, trxName);
		query.setParameters(C_BankAccount.getC_BankAccount_ID());
		MBankStatementLoader loader = query.firstOnly();

		m_callback = new MyHBCICallback(loader, processUI);
		HBCIPassport passport = HBCIUtil.createPassport(loader, m_callback);

		{
			/*
			 * Ich habe festgestellt, das es eine gute Idee ist, die BPD
			 * regelmäßig neu abzufragen. Deshalb mache ich das jetzt einfach
			 * immer. Wer da Zeit sparen möchte, kann das weglassen. Falls es
			 * dann irgendwann Probleme gibt, müsste die BPD anders aktualisiert
			 * werden - z.B. entweder automatisch bei Problemen oder mit einem
			 * eigenen Job.
			 */
			passport.clearBPD();
			hbciHandle = new HBCIHandler(HBCIUtils.getParam("client.passport.hbciversion.default"), passport);
			passport.getBPD();
			// Properties bpd = passport.getBPD();
			// System.out.println(bpd.toString());
		}

		Konto meinKonto = new Konto("DE", C_BankAccount.getC_Bank().getRoutingNo(), C_BankAccount.getAccountNo());
		meinKonto.bic = C_BankAccount.getC_Bank().getSwiftCode();
		meinKonto.iban = C_BankAccount.getIBAN().replace(" ", "");
		if(!IBANUtil.isIBAN(meinKonto.iban))
			throw new AdempiereException("your own IBAN is not guilty");
		m_org = (MOrg) MTable.get(ctx, MOrg.Table_ID).getPO(Env.getAD_Org_ID(ctx), trxName);
		meinKonto.name = m_org.getName();

		if (isDebitPayment) {
			/*
			 * Ich kann vorher nicht einfach abschätzen, welche Arten von
			 * Lastschriften ich später bekomme (B2B, CORE, COR1). Man könnte
			 * diese vorher durch den Benutzer vorsortieren lassen. Aber
			 * einerseits interessiert den Benutzer das womöglich gar nicht und
			 * andererseits gibt es auch noch eine Unterteilung nach Erst- und
			 * Folgelastschriften (FRST und RCUR), die sowieso geteilt werden
			 * müssen. Also übernehme ich die Aufteilung ganz hier. Ich erzeuge
			 * also erst mal alle möglichen Arten von Jobs und selektiere
			 * unmittelbar vor dem Versand, welche ich wirklich benutzt habe.
			 * 
			 * Eigentlich könnte man übrigens meiner Meinung nach in der Theorie
			 * diese mehreren Dateien in einem einzigen HBCI-Job mit einer
			 * einzigen Authorisierung zur Bank schicken. Das geht aber wohl mit
			 * HBCI4Java so nicht.
			 */
			allJobs = new Job[SepaSddScheme.values().length * 2];
			/*
			 * jeweils ein Eintrag für Erst- (FRST) und einer für
			 * Folgelastschriften (RCUR):
			 */
			allJobs[SepaSddScheme.CORE.ordinal() * 2] = initJob("MultiLastSEPA", meinKonto, isDebitPayment, true);
			allJobs[SepaSddScheme.CORE.ordinal() * 2 + 1] = initJob("MultiLastSEPA", meinKonto, isDebitPayment, false);
			allJobs[SepaSddScheme.COR1.ordinal() * 2] = initJob("MultiLastCOR1SEPA", meinKonto, isDebitPayment, true);
			allJobs[SepaSddScheme.COR1.ordinal() * 2 + 1] = initJob("MultiLastCOR1SEPA", meinKonto, isDebitPayment,
					false);
			allJobs[SepaSddScheme.B2B.ordinal() * 2] = initJob("MultiLastB2BSEPA", meinKonto, isDebitPayment, true);
			allJobs[SepaSddScheme.B2B.ordinal() * 2 + 1] = initJob("MultiLastB2BSEPA", meinKonto, isDebitPayment,
					false);
		} else {
			// Überweisungen
			allJobs = new Job[1];
			allJobs[0] = initJob("MultiUebSEPA", meinKonto, isDebitPayment, false);
		}

		return true;
	}

	private Job initJob(String jobName, Konto konto, boolean isDebit, boolean isFirst) {
		m_isDebit = isDebit;
		Job job = new Job();
		HBCIJob hbciJob = hbciHandle.newJob(jobName);
		hbciJob.setParam("src", konto);
		hbciJob.setParam("src.name", konto.name);
		// hbciJob.setParam("src.country", "DE");
		// hbciJob.setParam("src.blz", myAccount.getC_Bank().getRoutingNo());
		// hbciJob.setParam("src.number", myAccount.getAccountNo());
		// hbciJob.setParam("src.subnumer", "");
		// job.setParam("sepaid", sepaid); // wird sonst automatisch gesetzt
		if (isDebit) {
			if (isFirst)
				hbciJob.setParam("sequencetype", "FRST");
			else
				hbciJob.setParam("sequencetype", "RCUR");
			// Frist abhängig von Banktagen berechnen
			GregorianCalendar cal = new GregorianCalendar();
			int frist = 2;
			if (jobName.contains("CORE") && isFirst)
				frist = 5;
			{
				// Frist gilt immer nur bis 15:30 (be ider Volksbank)
				GregorianCalendar heute = new GregorianCalendar();
				if (heute.get(Calendar.HOUR_OF_DAY) >= 16
						|| (heute.get(Calendar.HOUR_OF_DAY) == 15 
						&& heute.get(Calendar.MINUTE) > 30)
					){
					frist++;
				}
			}
			
			while (frist > 0) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
				int wochentag = cal.get(GregorianCalendar.DAY_OF_WEEK);
				if (wochentag == Calendar.SUNDAY)
					continue;
				if (wochentag == Calendar.SATURDAY)
					continue;
				// hier ggf. andere Bankfeiertage einfügen
				frist--;
			}
			String dateString = isoDateFormatter.format(cal.getTime());
			hbciJob.setParam("targetdate", dateString);
		} else {
			// GregorianCalendar cal = new GregorianCalendar();
			// String dateString = isoDateFormatter.format(cal.getTime());
			// hbciJob.setParam("targetdate", dateString);
		}

		job.hbciJob = hbciJob;
		job.count = 0;
		job.total = BigDecimal.ZERO;
		job.jobName = jobName;
		return job;
	}

	//@Override
	public boolean addCheck(I_C_PaySelectionCheck check) {
		// Verwendungszweck ermitteln
		String whereClause = MPaySelectionLine.COLUMNNAME_C_PaySelectionCheck_ID + "=? ";
		Query query = new Query(m_ctx, MPaySelectionLine.Table_Name, whereClause, m_trxName);
		query.setParameters(check.getC_PaySelectionCheck_ID());
		List<MPaySelectionLine> list = query.list();
		SimpleDateFormat shortDateFormatter = new SimpleDateFormat("dd.MM.yy");
		StringBuilder usage = new StringBuilder();
		StringBuilder e2eid = new StringBuilder();
		for (MPaySelectionLine line : list) {
			I_C_Invoice invoice = line.getC_Invoice();
			if (invoice != null) {
				if (usage.length() > 0) {
					usage.append(", ");
					e2eid.append("/");
				}
				usage.append(invoice.getDocumentNo()).append(" ");
				usage.append(shortDateFormatter.format(invoice.getDateInvoiced()));
				usage.append(Util.isEmpty(invoice.getDescription()) ? "" : " " + invoice.getDescription());
				e2eid.append(invoice.getDocumentNo());
			}
		}
		if (usage.length() == 0) {
			usage.append("Zahlung ").append(check.getC_Payment().getDocumentNo());
			e2eid.append("Check " + check.getDocumentNo());
		}
		// Transaktion speichern
		MBPBankAccount bpAccount = (MBPBankAccount) check.getC_BP_BankAccount();
		if(check.getPayAmt().compareTo(BigDecimal.ZERO) <= 0)
			throw new AdempiereException("checks/direct transactions are not allowed with an amount smaller than or equal zero");
		return addTransaction(bpAccount, usage.toString(), e2eid.toString(), check.getPayAmt());
	}
	
	public void done(){
		HBCIUtils.done();
	}

	//@Override
	public boolean addPayment(I_C_Payment payment) {
		/*
		 * Konto ermitteln
		 * 
		 * (payment.getC_BP_BankAccount() ist übrigens normalerweise nicht
		 * gesetzt. Es ist in 2012 von Elaine Tan erst eingeführt worden und
		 * wird nur von deren Code btgl. PaymentTransaction benutzt.)
		 * 
		 * Wen das stört, das hier von mehreren Konten eines Geschäftspartners
		 * keins ausgewählt werden kann, kann dieses Feld ja vielleicht im
		 * Fenster aktivieren und benutzen. Vorher sollte man aber sehen, was
		 * Elaine damit wirklich macht.
		 */
		int bpartnerID = payment.getC_BPartner_ID();
		String whereClause = MBPBankAccount.COLUMNNAME_C_BPartner_ID + "=? AND " + MBPBankAccount.Table_Name
				+ ".IsActive='Y' ";
		Query query = new Query(m_ctx, MBPBankAccount.Table_Name, whereClause, m_trxName);
		query.setParameters(bpartnerID);
		MBPBankAccount bpAccount = query.first();
		if (bpAccount == null)
			throw new AdempiereException("no known account for Business Partner " + payment.getC_BPartner().toString());
		BigDecimal payAmt = payment.getPayAmt();
		boolean isDebit = false; // Lastschrift oder Überweisung?
		if (payment.getC_DocType().isSOTrx())
			isDebit = true;
		if (BigDecimal.ZERO.compareTo(payAmt) > 0) {
			isDebit = !isDebit;
			payAmt = payAmt.negate();
		}
		if (isDebit != m_isDebit)
			throw new AdempiereException("Zahlung " + payAmt
					+ " entspricht nicht der Zahlungsart des Sammlers (Lastschrift/Überweisung).");
		return addTransaction(bpAccount, payment.getDescription(), payment.getDocumentNo(), payAmt);
	}

	//@Override
	public boolean addTransaction(MBPBankAccount bpAccount, String usage, String end2endId, BigDecimal amount) {
		// GV-Typen unterscheiden und richtigen Job aus der Tabelle suchen
		SepaSddScheme lsArt = null;
		int jobIndex = 0; // reicht für Überweisung
		if (m_isDebitPayment) {
			String lsString = bpAccount.get_ValueAsString(MBPBankAccountHelper.COLUMNNAME_SEPASDDSCHEME);
			if (lsString == "")
				throw new AdempiereException("Bank Account without a SEPA Mandate Type set: "+bpAccount.getA_Name());
			lsArt = SepaSddScheme.valueOf(lsString);
			jobIndex = lsArt.ordinal() * 2;

			if (Boolean.TRUE.equals(bpAccount.get_ValueAsBoolean(MBPBankAccountHelper.COLUMNNAME_ISTRANSFERRED))) {
				jobIndex++;
			} else {
				System.out.println("Erstbenutzung des Mandats: " + bpAccount.toString());
				if (!onlyTest) {
					bpAccount.set_ValueNoCheck(MBPBankAccountHelper.COLUMNNAME_ISTRANSFERRED, Boolean.TRUE);
					bpAccount.saveEx(m_trxName);
				}
			}
		}
		Job job = allJobs[jobIndex];

		// HBCI-Parameter füllen
		String name = bpAccount.getA_Name();
		if (Util.isEmpty(name, true))
			name = bpAccount.getC_BPartner().getName();
		job.hbciJob.setParam("dst.name", job.count, name);
		job.hbciJob.setParam("dst.bic", job.count, bpAccount.getC_Bank().getSwiftCode());
		String iban = ((PO) bpAccount).get_ValueAsString(MBPBankAccountHelper.COLUMNNAME_IBAN);
		if (Util.isEmpty(iban, true))
			throw new AdempiereException("Keine IBAN vorhanden: " + bpAccount.toString());
		else
			iban = iban.replace(" ", "");
		if (IBANUtil.checkIBAN(iban).length() > 0)
			throw new AdempiereException("IBAN falsch: (" + IBANUtil.checkIBAN(iban) + ")" + bpAccount.toString());
		job.hbciJob.setParam("dst.iban", job.count, iban);
		job.hbciJob.setParam("btg.value", job.count, amountFormatter.format(amount));
		job.hbciJob.setParam("btg.curr", job.count, "EUR");
		if (usage == null || usage.length() > 140)
			usage = usage.substring(0, 140);
		job.hbciJob.setParam("usage", job.count, usage);
		if (end2endId == null || end2endId.length() > 35)
			end2endId = end2endId.substring(0, 35);
		job.hbciJob.setParam("endtoendid", job.count, end2endId);
		if (m_isDebitPayment) {
			String mandateID = ((PO) bpAccount).get_ValueAsString(MBPBankAccountHelper.COLUMNNAME_MNDTID);
			if(mandateID==null || mandateID.length()==0)
				throw new AdempiereException("Mandats-ID ist leer: "+bpAccount.toString());
			job.hbciJob.setParam("mandateid", job.count, mandateID);
			try {
				String dateString = isoDateFormatter.format(((PO) bpAccount).get_Value(MBPBankAccountHelper.COLUMNNAME_DATEDOC));
				job.hbciJob.setParam("manddateofsig", job.count, dateString);
			} catch (IllegalArgumentException e) {
				throw new AdempiereException("date format error: " + bpAccount.toString(), e);
			}
			job.hbciJob.setParam("amendmandindic", job.count, Boolean.toString(false));
			MOrgInfo orgInfo = MOrgInfo.get(m_ctx, Env.getAD_Org_ID(m_ctx), m_trxName);
			job.hbciJob.setParam("creditorid", job.count,
					orgInfo.get_ValueAsString(MOrgHelper.COLUMNNAME_AD_ORG_CREDITORIDENTIFIER));
		}
		job.count++;
		job.total = job.total.add(amount);
		return true;
	}

	//@Override
	public String isOK() {
		try {
			for (int i = 0; i < allJobs.length; i++) {
				Job job = allJobs[i];
				if (job.count == 0)
					continue;
				job.hbciJob.setParam("Total.value", amountFormatter.format(job.total));
				job.hbciJob.setParam("Total.curr", "EUR");
				((AbstractSEPAGV) job.hbciJob).verifyConstraints();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return null;
	}

	private Boolean stop;


	public String process() {
		return process(null, null);
	}
	
	//@Override
	public String process(Integer paySelectionID, Integer batchID) {
		// nochmal checken und den Benutzer fragen
		BigDecimal grandTotal = BigDecimal.ZERO;
		int grandCount = 0;
		for (int i = 0; i < allJobs.length; i++) {
			grandTotal = grandTotal.add(allJobs[i].total);
			grandCount += allJobs[i].count;
		}
		if (grandTotal.equals(BigDecimal.ZERO))
			throw new AdempiereException("Zahlungssumme ist 0.");
		if (m_processUI != null) {
			stop = null;
			DecimalFormat format = new DecimalFormat("#,##0.00");
			m_processUI.ask("Gesamtsumme " + format.format(grandTotal) + " € in " + grandCount + " Belegen als "
					+ (m_isDebitPayment ? "Lastschrift" : "Überweisung") + " versenden?", new Callback<Boolean>() {
				public void onCallback(Boolean result) {
						stop = !result;
				}
			});
			int timeout=2*60*5;  // 2 Minuten
			while(stop==null){  // wait for user answer
				try {
					Thread.sleep(200);
					if(timeout-- < 0)
						throw new AdempiereException("timeout");
				} catch (InterruptedException e) {}
			}
			if (stop)
				return "manueller Abbruch";
		}
		

		// Jetzt alle Teile einzeln versenden
		String allErrors = "";
		try {
			for (int i = 0; i < allJobs.length; i++) {
				Job job = allJobs[i];
				if (job.count == 0)
					continue;
				job.hbciJob.setParam("Total.value", job.total.toString());
				job.hbciJob.setParam("Total.curr", "EUR");
				((AbstractSEPAGV) job.hbciJob).verifyConstraints();

				X_BAY_HBCILog protocolRecord = new X_BAY_HBCILog(m_ctx, 0, m_trxName);

				Properties params = ((AbstractSEPAGV) job.hbciJob).getLowlevelParams();
				String painXML = params.getProperty(job.hbciJob.getName() + ".sepapain");
				painXML = painXML.substring(1);

				
				protocolRecord.setAD_Org_ID(0);
				protocolRecord.setBinaryData(painXML.getBytes());
				protocolRecord.setCounter(job.count);
				protocolRecord.setTotalAmt(job.total);
				protocolRecord.setName(job.jobName + " " + (i % 2 == 0 ? "FRST" : "RCUR"));
				protocolRecord.setC_PaySelection_ID(paySelectionID == null ? 0 : paySelectionID);
				protocolRecord.setC_PaymentBatch_ID(batchID == null ? 0 : batchID);
				
				// letzte Stelle zum Abbruch, bevor es zur Bank geht
				stop=false;
				if (allJobs.length>1 && m_processUI != null) {
					/*
					 * Wenn ich die ÜBertragung aufteile (wegen verschiedener
					 * SEPA-Typen), frage ich die hier nochmal einzeln ab. Das
					 * hilft vor allem, wenn was in einer Datei schiefgeht, weil
					 * ich die Dateien dann nicht alle neu versenden muss,
					 * sondern die bereits versendeten ausblenden kann.
					 */
					stop = null;
					m_processUI.ask("Einzeldatei über " + job.total.toString() + " versenden?", new Callback<Boolean>() {
						public void onCallback(Boolean result) {
								stop = !result;
						}
					});
					int timeout=2*60*5;  // 2 Minuten
					while(stop==null){  // wait for user answer
						try {
							Thread.sleep(200);
							if(timeout-- < 0)
								throw new AdempiereException("timeout");
						} catch (InterruptedException e) {}
					}
				}
				
				if ((stop == false) && !onlyTest) {

					job.hbciJob.addToQueue();

					// alle Jobs in der Job-Warteschlange ausführen
					// HBCIExecStatus ret = hbciHandle.execute();
					hbciHandle.execute();

					protocolRecord.setProcessed(true);
					HBCIJobResult result = job.hbciJob.getJobResult();
					String statusString=result.getJobStatus().getErrorString();
					if(statusString.length()>200)
						statusString=statusString.substring(0, 200);
					protocolRecord.setStatus(statusString);
					// Was ist das für ein Objekt?
					if (result.isOK()) {
						protocolRecord.setIsError(false);
						protocolRecord.setProtocol(m_callback.getLog());
					} else {
						protocolRecord.setIsError(true);
						String errorMessage = "ERROR: ";
						HBCIStatus status = result.getJobStatus();
						if (!status.isOK())
							errorMessage += status.getErrorString();
						status = result.getGlobStatus();
						if (!status.isOK())
							errorMessage += status.getErrorString();
						protocolRecord.setProtocol(m_callback.getLog() + errorMessage);
						allErrors += errorMessage;
					}
				}
				protocolRecord.saveEx(m_trxName);
				m_callback.cleanLog();
			}
			if (allErrors.length() == 0)
				return null;
			return allErrors;
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

}
