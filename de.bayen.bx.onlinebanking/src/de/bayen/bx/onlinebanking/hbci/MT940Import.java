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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_C_BankAccount;
import org.compiere.model.MBankStatementLoader;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.util.Util;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRKUms.BTag;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.structures.Konto;

import de.bayen.bx.onlinebanking.model.I_BAY_MT940;
import de.bayen.bx.onlinebanking.model.X_BAY_MT940;
import de.bayen.bx.util.AbstractSvrProcess;

/**
 * Prozeß, der Kontoauszugsdaten per HBCI von der Bank importiert und als
 * Rohdaten in die Tabelle BAY_MT940 schreibt.
 * 
 * @author tbayen
 */
// TODO jemand sollte über Multithreading nachdenken (im zk-Server)
public class MT940Import extends AbstractSvrProcess {

	static public interface ImportParameter {
		public MBankStatementLoader getC_BankStatementLoader();

		public Date getDateTrx();

		public Date getDateTrxTo();
	}

	private MBankStatementLoader m_loader;
	private MyHBCICallback m_callback;

	@Override
	protected Class<?>[] getParameterInterfaces() {
		return new Class<?>[] { ImportParameter.class };
	}

	@Override
	protected String doIt() throws Exception {
		Properties ctx = getCtx();
		ImportParameter bean = (ImportParameter) getParameterBean();
		Date startDate = bean.getDateTrx();
		if (startDate == null)
			startDate = new Date(); // sonst heute
		Date endDate = bean.getDateTrxTo();
		if (endDate == null)
			endDate = new Date(); // sonst heute
		m_loader = bean.getC_BankStatementLoader();
		I_C_BankAccount bankAccount = m_loader.getC_BankAccount();

		return loadBankStatementData(ctx, startDate, endDate, bankAccount);
	}

	private String loadBankStatementData(Properties ctx, Date startDate,
			Date endDate, I_C_BankAccount bankAccount) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		//
		m_callback = new MyHBCICallback(m_loader, processUI);
		try {
			HBCIPassport passport = HBCIUtil.createPassport(m_loader,
					m_callback);

			HBCIHandler hbciHandle = new HBCIHandler(
					HBCIUtils.getParam("client.passport.hbciversion.default"),
					passport);
			String accountNo = bankAccount.getAccountNo();
			Konto myaccount = passport.getAccount(accountNo);
			// wenn der obige Aufruf nicht funktioniert, muss die abzufragende
			// Kontoverbindung manuell gesetzt werden:
			// Konto myaccount=new Konto("DE","86055592","1234567890");

			// Job zur Abholung der Kontoauszüge erzeugen
			HBCIJob auszug = hbciHandle.newJob("KUmsAll");
			auszug.setParam("my", myaccount);
			auszug.setParam("startdate", dateFormatter.format(startDate));
			auszug.setParam("enddate", dateFormatter.format(endDate));
			auszug.addToQueue();

			// alle Jobs in der Job-Warteschlange ausführen
			HBCIExecStatus ret = hbciHandle.execute();

			GVRKUms result = (GVRKUms) auszug.getJobResult();

			// wenn der Job "Kontoauszüge abholen" erfolgreich ausgeführt wurde
			if (result.isOK()) {
				MTable table = MTable.get(ctx, I_BAY_MT940.Table_ID);
				for (BTag buchungsTag : result.getDataPerDay()) {
					for (GVRKUms.UmsLine zeile : buchungsTag.lines) {
						// prepare some values
						String vwz = "";
						String eref = null, kref = null, mref = null, cred = null;
						for (String vwzZeile : zeile.usage) {
							if (vwzZeile.startsWith("EREF+"))
								eref = vwzZeile.substring(5);
							else if (vwzZeile.startsWith("KREF+"))
								kref = vwzZeile.substring(5);
							else if (vwzZeile.startsWith("MREF+"))
								mref = vwzZeile.substring(5);
							else if (vwzZeile.startsWith("CRED+"))
								cred = vwzZeile.substring(5);
							else if (vwzZeile.startsWith("SVWZ+"))
								vwz += vwzZeile.substring(5);
							else if (Pattern.matches("", vwzZeile)) {
								log.severe("unknown SEPA vwz: " + vwzZeile);
								vwz += vwzZeile.substring(5);
							} else
								vwz += vwzZeile;
						}
						if (Util.isEmpty(vwz, true))
							vwz = null;

						String bPName = null;
						if (zeile.other != null) {
							if (!Util.isEmpty(zeile.other.name, true)) {
								bPName = zeile.other.name;
								bPName += (zeile.other.name2 == null ? ""
										: zeile.other.name2);
							} else if (!Util.isEmpty(zeile.other.name2, true))
								bPName = zeile.other.name2;
						} else
							bPName = null;
						//
						String otherAccountNo = null;
						if (zeile.other != null) {
							if (Util.isEmpty(zeile.other.iban, true))
								otherAccountNo = Util.isEmpty(
										zeile.other.number, true) ? null
										: zeile.other.number;
							else
								otherAccountNo = zeile.other.iban;
						}

						// @formatter:off
						String whereClause = "("
								+ I_BAY_MT940.COLUMNNAME_BPAccountNo
								+ " IS NOT DISTINCT FROM ?) AND ("
								+ I_BAY_MT940.COLUMNNAME_BPName
								+ " IS NOT DISTINCT FROM ?) AND ("
								+ I_BAY_MT940.COLUMNNAME_GVCode
								+ " IS NOT DISTINCT FROM ?) AND ("
								+ I_BAY_MT940.COLUMNNAME_Primanota
								+ " IS NOT DISTINCT FROM ?) AND ("
								+ I_BAY_MT940.COLUMNNAME_IsReversal
								+ " IS NOT DISTINCT FROM ?) AND ("
								+ I_BAY_MT940.COLUMNNAME_StatementDate
								+ " IS NOT DISTINCT FROM ?) AND ("
								+ I_BAY_MT940.COLUMNNAME_Amount
								+ " IS NOT DISTINCT FROM ?) AND ("
								+ I_BAY_MT940.COLUMNNAME_ValutaDate
								+ " IS NOT DISTINCT FROM ?) AND ("
								+ I_BAY_MT940.COLUMNNAME_VWZ
								+ " IS NOT DISTINCT FROM ?) ";
						Query query = new Query(ctx, table, whereClause,
								get_TrxName()).setParameters(otherAccountNo,
								bPName, zeile.gvcode, zeile.primanota,
								zeile.isStorno ? "Y" : "N", new Timestamp(
										zeile.bdate.getTime()), zeile.value
										.getBigDecimalValue(), new Timestamp(
										zeile.valuta.getTime()), vwz);
						// System.out.println(query.getSQL());
						int availableLine = query.firstIdOnly();

						// @formatter:on
						if (availableLine > 0) {
							String doubleMessage = "doubled line not re-read: "
									+ (zeile.other == null ? 
											(zeile.text == null ? "" : zeile.text)
											: zeile.other.name);
							addBufferLog(
									0, null, null, doubleMessage,
									X_BAY_MT940.Table_ID, availableLine);
							log.info(doubleMessage);
							continue;
						}

						// first some checks
						// if (buchungsTag.starttype != 'F' ||
						// buchungsTag.endtype
						// != 'F')
						// log.warning("unerwarteter Buchungtyp: " +
						// buchungsTag.start.toString() + " ("
						// + buchungsTag.starttype + ")" + "---" +
						// buchungsTag.end.toString() + " ("
						// + buchungsTag.endtype + ")");

						/*
						 * Den Check auf das Startdate des Auszugs habe ich hier
						 * weggelassen. Dennoch dokumentiere ich das Verhalten,
						 * falls jemand später was damit anfangen möchte:
						 * 
						 * Die Volksbank gibt mir für jeden Tag einen
						 * Tagesauszug. In diesem Buchungstag ist das Startdatum
						 * und das Enddatum identisch. Das Buchungsdatum aller
						 * Zeilen hierin ist ebenfalls identisch.
						 * 
						 * Die Sparkasse gibt als Startdatum des Buchungstages
						 * das Datum des letzten Buchungstages (also das
						 * Ende-Datum des letzten Auszugs). Das bedeutet, das
						 * ich für den Auszug vom 2.1.13, in dem lauter
						 * Einzel-Buchungen mit Buchungstag 2.1.13 enthalten
						 * waren, ein Enddatum 2.1.13 hatte, aber ein Startdatum
						 * 30.1.13.
						 */
						// if (!buchungsTag.start.timestamp.equals(zeile.bdate))
						// System.out.println("unexpected start dates: " +
						// buchungsTag.start.timestamp + " and "
						// + zeile.bdate + " in Line " + zeile.toString());
						if (!buchungsTag.end.timestamp.equals(zeile.bdate))
							throw new AdempiereException(
									"unexpected end dates: "
											+ buchungsTag.end.timestamp
											+ " and " + zeile.bdate
											+ " in Line " + zeile.toString());
						//
						X_BAY_MT940 record = new X_BAY_MT940(ctx, 0,
								get_TrxName());
						record.setC_BankAccount_ID(bankAccount
								.getC_BankAccount_ID());
						record.setAdditional(zeile.additional);
						record.setAddKey(zeile.addkey);
						record.setAmtSource(zeile.orig_value != null ? zeile.orig_value
								.getBigDecimalValue() : null);
						record.setBPAccountNo(otherAccountNo);
						record.setBPName(bPName);
						record.setBPRoutingNo(zeile.other == null ? null
								: (Util.isEmpty(zeile.other.bic) ? zeile.other.blz
										: zeile.other.bic));
						record.setChargeAmt(zeile.charge_value != null ? zeile.charge_value
								.getBigDecimalValue() : null);
						record.setCRED(cred);
						record.setEREF(eref);
						record.setKREF(kref);
						record.setMREF(mref);
						record.setStartAmt(buchungsTag.start.value != null ? buchungsTag.start.value
								.getBigDecimalValue() : null);
						record.setEndAmt(buchungsTag.end.value != null ? buchungsTag.end.value
								.getBigDecimalValue() : null);
						record.setGVCode(zeile.gvcode);
						record.setInstRef(zeile.instref);
						record.setIsReversal(zeile.isStorno);
						record.setIsSepa(zeile.isSepa);
						record.setPrimanota(zeile.primanota);
						record.setReference(zeile.customerref);
						record.setStatementDate(new Timestamp(
								buchungsTag.end.timestamp.getTime()));
						record.setGVText(zeile.text);
						record.setTotalAmt(zeile.saldo.value
								.getBigDecimalValue());
						record.setValutaDate(new Timestamp(zeile.valuta
								.getTime()));
						record.setVWZ(vwz);
						record.setAmount(zeile.value.getBigDecimalValue());
						record.saveEx();

						addBufferLog(
								0, null, null,
								(record.getBPName() == null ? "" : record.getBPName())
								+ (record.getVWZ() == null ? "" : " " + record.getVWZ())
								+ " " + record.getAmount().toString(),
								X_BAY_MT940.Table_ID, record.get_ID());
					}
				}

			} else {
				// Fehlermeldungen ausgeben
				System.out.println("Job-Error");
				System.out.println(result.getJobStatus().getErrorString());
				System.out.println("Global Error");
				System.out.println(ret.getErrorString());
			}
			addLog(result.getJobStatus().toString());
			return result.getJobStatus().toString();
		} finally {
			try {
				HBCIUtils.done();
				// HBCIUtils.doneThread();
			} catch (Exception ex) {
			}
		}
	}

	public MBankStatementLoader getBankStatementLoader() {
		return m_loader;
	}

}
