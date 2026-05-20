package de.bayen.freibier.process;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAccount;
import org.compiere.model.MElementValue;
import org.compiere.model.Query;
import org.compiere.model.X_C_Charge_Acct;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;

import de.bayen.freibier.model.I_BAY_Config;
import de.bayen.freibier.model.MBAYContract;
import de.bayen.freibier.model.MBAYInterestCalculation;
import de.bayen.freibier.model.MBAYInterestCalculationLine;
import de.bayen.freibier.model.X_BAY_Config;

public class CreateInterestCalculationProcess extends SvrProcess {

	private String name = null;
	private String description = null;
	private String invoiceFrequency = null;
	private Timestamp dateFrom = null;
	private Timestamp dateTo = null;
	private String docAction = null;

	@Override
	protected void prepare() {
		for (ProcessInfoParameter para : getParameter()) {
			String name = para.getParameterName();
			switch (name) {
			case "Name":
				this.name = para.getParameterAsString();
				break;
			case "Description":
				this.description = para.getParameterAsString();
				break;
			case "InvoiceFrequency":
				this.invoiceFrequency = para.getParameterAsString();
				break;
			case "DateDoc":
				this.dateFrom = (Timestamp) para.getParameter();
				this.dateTo = (Timestamp) para.getParameter_To();
				break;
			case "DocAction":
				this.docAction = para.getParameterAsString();
				break;
			default:
				if (log.isLoggable(Level.INFO))
					log.log(Level.INFO, "Custom Parameter: " + name + "=" + para.getInfo());
				break;
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		List<MBAYContract> contracts = getContracts();

		int found = contracts.size();
		int created = 0;
		for (MBAYContract contract : contracts) {
			if (processRecord(contract))
				created++;
		}

		return "@OK@ --> " + created + " Zinsabrechnungen erstellt von " + found + "  Datensätzen";
	}

	private List<MBAYContract> getContracts() {
		final String whereClause = "BAY_Contract_ID IN ( "
				+ "SELECT t_selection_ID from T_Selection where ad_pinstance_ID = ?)";
		return new Query(getCtx(), MBAYContract.Table_Name, whereClause, get_TrxName())
				.setParameters(getAD_PInstance_ID())
				.list();
	}

	protected boolean processRecord(MBAYContract record) {

		if (invoiceFrequency != null && !invoiceFrequency.equals(record.getInvoiceFrequency())) {
			addLog("Zinsabrechnung nicht erstellt für Datensatz [" + record.getValue() + "] da die Abrechnungsfrequenz nicht übereinstimmt");
			return false;
		}
		
		int chargeID;
		if (record.isSOTrx())
			chargeID = getConfig().getChargeCustomerLoan_ID();
		else
			chargeID = getConfig().getChargeVendorLoan_ID();

		// XXX Query ist nicht gut, wenn es mehrere Account Schemas gibt
		X_C_Charge_Acct accounting = new Query(getCtx(), X_C_Charge_Acct.Table_Name,
				X_C_Charge_Acct.COLUMNNAME_C_Charge_ID + "=?", get_TrxName()).setParameters(chargeID).first();
		// TODO man muss nicht value nehmen, ID wäre effektiver
		MAccount account = MAccount.get(accounting.getCh_Expense_Acct());
		MElementValue elementValue = new MElementValue(getCtx(), account.getAccount_ID(), get_TrxName());
		String interestAccount = elementValue.getValue();
		//
		MBAYInterestCalculation ic = new MBAYInterestCalculation(getCtx(), 0, get_TrxName());
		ic.setBAY_Contract_ID(record.getBAY_Contract_ID());
		ic.setC_BPartner_ID(record.getC_BPartner_ID());
		ic.setName(name);
		ic.setDescription(description);
		ic.setDateDoc(dateTo);
		ic.setDateAcct(dateTo);
		String currencyID = getCtx().getProperty("$C_Currency_ID");
		ic.setC_Currency_ID(Integer.valueOf(currencyID));
		ic.setIsSOTrx(record.isSOTrx());
		ic.saveEx(get_TrxName());
		//
		// first line of the calculation is the running total at start date
		{
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT sum(AcctSum) FROM ( ");
			sql.append("SELECT ");
			sql.append("(Fact_Acct.AmtAcctDr - Fact_Acct.AmtAcctCr) AS AcctSum ");
			sql.append("FROM Fact_Acct ");
			sql.append("RIGHT JOIN C_ElementValue ON(C_ElementValue.C_ElementValue_ID = Fact_Acct.Account_ID) ");
			sql.append("RIGHT JOIN C_BPartner ON(C_BPartner.C_BPartner_ID = Fact_Acct.C_BPartner_ID) ");
			sql.append("LEFT JOIN BAY_Contract ON(BAY_Contract.BAY_Contract_ID = Fact_Acct.UserElement1_ID) ");
			sql.append("LEFT JOIN C_Invoice ON(Fact_Acct.AD_Table_ID = 318 AND C_Invoice.C_Invoice_ID = Fact_Acct.Record_ID) ");
			sql.append("WHERE C_ElementValue.Value=? "); // #1
			sql.append("AND BAY_Contract.BAY_Contract_ID=? "); // #2
			sql.append("AND Fact_Acct.PostingType='A' ");
			sql.append("AND Reversal_ID IS NULL ");
			sql.append("AND Fact_Acct.DateTrx<? "); // #3
			sql.append(") AS olderPostings ");
			CPreparedStatement stat = DB.prepareStatement(sql.toString(), get_TrxName());
			try {
				stat.setString(1, interestAccount);
				stat.setInt(2, record.get_ID());
				stat.setDate(3, new Date(dateFrom.getTime()));
				ResultSet rs = stat.executeQuery();
				try {
					if (rs.next()) {
						MBAYInterestCalculationLine newLine = new MBAYInterestCalculationLine(ic);
						newLine.setDateTrx(dateFrom);
						newLine.setAmount(rs.getBigDecimal(1));
						newLine.setDescription("Anfangssaldo"); // XXX translation
						newLine.setInterestPercent(record.getInterestPercent());
						newLine.saveEx(get_TrxName());
					}
				} finally {
					rs.close();
					stat.close();
				}
			} catch (Exception ex) {
				throw new AdempiereException(ex);
			}
		}

		// read all posted accounting lines between start and end date
		Timestamp lastDate = null;
		{
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			sql.append("Fact_Acct.DateTrx, ");
			sql.append("Fact_Acct.DateAcct, ");
			sql.append("Fact_Acct.AmtAcctDr, ");
			sql.append("Fact_Acct.AmtAcctCr, ");
			sql.append("BAY_Contract.Value, ");
			sql.append("Fact_Acct.Description AS fa_Description, ");
			sql.append("C_Invoice.Description AS i_Description, ");
			sql.append("C_InvoiceLine.Description AS Description ");
			sql.append("FROM Fact_Acct ");
			sql.append("RIGHT JOIN C_ElementValue ON(C_ElementValue.C_ElementValue_ID = Fact_Acct.Account_ID) ");
			sql.append("RIGHT JOIN C_BPartner ON(C_BPartner.C_BPartner_ID = Fact_Acct.C_BPartner_ID) ");
			sql.append("LEFT JOIN BAY_Contract ON(BAY_Contract.BAY_Contract_ID = Fact_Acct.UserElement1_ID) ");
			sql.append("LEFT JOIN C_Invoice ON(Fact_Acct.AD_Table_ID = 318 AND C_Invoice.C_Invoice_ID = Fact_Acct.Record_ID) ");
			sql.append("LEFT JOIN C_InvoiceLine ON(Fact_Acct.AD_Table_ID = 318 AND C_InvoiceLine.C_InvoiceLine_ID = Fact_Acct.Line_ID) ");
			sql.append("WHERE C_ElementValue.Value=? "); // #1
			sql.append("AND BAY_Contract.BAY_Contract_ID=? "); // #2
			sql.append("AND Fact_Acct.PostingType='A' ");
			sql.append("AND Reversal_ID IS NULL ");
			sql.append("AND Fact_Acct.DateTrx>=? "); // #3
			sql.append("AND Fact_Acct.DateTrx<=? "); // #4
			sql.append("ORDER BY Fact_Acct.DateTrx, Fact_Acct.Fact_Acct_ID ");
			CPreparedStatement stat = DB.prepareStatement(sql.toString(), get_TrxName());
			try {
				stat.setString(1, interestAccount);
				stat.setInt(2, record.get_ID());
				stat.setDate(3, new Date(dateFrom.getTime()));
				stat.setDate(4, new Date(dateTo.getTime()));
				ResultSet rs = stat.executeQuery();
				try {
					while (rs.next()) {
						MBAYInterestCalculationLine newLine = new MBAYInterestCalculationLine(ic);
						lastDate = rs.getTimestamp("DateTrx");
						newLine.setDateTrx(lastDate);
						newLine.setAmount(rs.getBigDecimal("AmtAcctDr")
								.subtract(rs.getBigDecimal("AmtAcctCr")));
						String description = rs.getString("Description");
						if (description == null)
							description = rs.getString("i_Description");
						if (description == null)
							description = rs.getString("fa_Description");
						newLine.setDescription(description);
						newLine.setInterestPercent(record.getInterestPercent());
						newLine.saveEx(get_TrxName());
					}
				} finally {
					rs.close();
					stat.close();
				}
			} catch (Exception ex) {
				throw new AdempiereException(ex);
			}
		}

		// last line
		if (lastDate == null || !lastDate.equals(dateTo)) {
			MBAYInterestCalculationLine newLine = new MBAYInterestCalculationLine(ic);
			newLine.setDateTrx(dateTo);
			newLine.setAmount(BigDecimal.ZERO);
			newLine.setDescription("Zinsabschluss"); // XXX translation
			newLine.setInterestPercent(record.getInterestPercent());
			newLine.saveEx(get_TrxName());
		}

		// Dokument ggf. abschliessen
		if (DocAction.ACTION_Complete.equals(docAction)) {
			ic.setDocAction(docAction);
			if (!ic.processIt(docAction)) {
				throw new AdempiereException("can not complete InterestCalculation document");
			}
			ic.saveEx();
		}

		// logging
		addLog(getProcessInfo().getAD_Process_ID(), new Timestamp(System.currentTimeMillis()), new BigDecimal(
				getProcessInfo().getAD_PInstance_ID()), "new: " + ic.getDocumentInfo(),
				MBAYInterestCalculation.Table_ID, ic.get_ID());

		return true;
	}

	private X_BAY_Config freibierConfig = null;

	private I_BAY_Config getConfig() {
		if (freibierConfig == null)
			freibierConfig = new Query(getCtx(), I_BAY_Config.Table_Name, null, get_TrxName()).first();
		return freibierConfig;
	}

}