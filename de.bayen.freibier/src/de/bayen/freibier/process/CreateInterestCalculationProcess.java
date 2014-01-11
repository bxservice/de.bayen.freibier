package de.bayen.freibier.process;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;

import de.bayen.freibier.model.MBAYContract;
import de.bayen.freibier.model.MBAYInterestCalculation;
import de.bayen.freibier.model.MBAYInterestCalculationLine;
import de.bayen.freibier.util.AbstractRecordProcessor;

public class CreateInterestCalculationProcess extends
		AbstractRecordProcessor<MBAYContract> {

	public CreateInterestCalculationProcess() {
		super(MBAYContract.Table_ID);
	}

	static public interface Params {
		public String getName();

		public Timestamp getDateDoc();

		public Timestamp getDateDocTo();
	}

	@Override
	protected Class<?>[] getParameterInterfaces() {
		return new Class<?>[] { RecordParams.class, Params.class };
	}

	@Override
	protected String processRecord(MBAYContract record) {
		final String INTEREST_ACCOUNT = "1360"; // XXX configure somewhere
		Params params = (Params) getParameterBean();
		MBAYInterestCalculation ic = new MBAYInterestCalculation(getCtx(), 0,
				get_TrxName());
		ic.setBAY_Contract_ID(record.getBAY_Contract_ID());
		ic.setC_BPartner_ID(record.getC_BPartner_ID());
		ic.setName(params.getName());
		ic.setDateDoc(params.getDateDocTo());
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
			sql.append("AND Fact_Acct.DateTrx<? "); // #3
			sql.append(") AS olderPostings ");
			CPreparedStatement stat = DB.prepareStatement(sql.toString(),
					get_TrxName());
			try {
				stat.setString(1, INTEREST_ACCOUNT);
				stat.setInt(2, record.get_ID());
				stat.setDate(3, new Date(params.getDateDoc().getTime()));
				ResultSet rs = stat.executeQuery();
				try {
					if (rs.next()) {
						MBAYInterestCalculationLine newLine = new MBAYInterestCalculationLine(
								ic);
						newLine.setDateTrx(params.getDateDoc());
						newLine.setAmount(rs.getBigDecimal(1));
						newLine.setDescription("Anfangssaldo"); // XXX
																// translation
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
			sql.append("C_Invoice.Description AS Description ");
			sql.append("FROM Fact_Acct ");
			sql.append("RIGHT JOIN C_ElementValue ON(C_ElementValue.C_ElementValue_ID = Fact_Acct.Account_ID) ");
			sql.append("RIGHT JOIN C_BPartner ON(C_BPartner.C_BPartner_ID = Fact_Acct.C_BPartner_ID) ");
			sql.append("LEFT JOIN BAY_Contract ON(BAY_Contract.BAY_Contract_ID = Fact_Acct.UserElement1_ID) ");
			sql.append("LEFT JOIN C_Invoice ON(Fact_Acct.AD_Table_ID = 318 AND C_Invoice.C_Invoice_ID = Fact_Acct.Record_ID) ");
			sql.append("WHERE C_ElementValue.Value=? "); // #1
			sql.append("AND BAY_Contract.BAY_Contract_ID=? "); // #2
			sql.append("AND Fact_Acct.PostingType='A' ");
			sql.append("AND Fact_Acct.DateTrx>=? "); // #3
			sql.append("AND Fact_Acct.DateTrx<=? "); // #4
			sql.append("ORDER BY Fact_Acct.DateTrx ");
			CPreparedStatement stat = DB.prepareStatement(sql.toString(),
					get_TrxName());
			try {
				stat.setString(1, "1360");
				stat.setInt(2, record.get_ID());
				stat.setDate(3, new Date(params.getDateDoc().getTime()));
				stat.setDate(4, new Date(params.getDateDocTo().getTime()));
				ResultSet rs = stat.executeQuery();
				try {
					while (rs.next()) {
						MBAYInterestCalculationLine newLine = new MBAYInterestCalculationLine(
								ic);
						lastDate = rs.getTimestamp("DateTrx");
						newLine.setDateTrx(lastDate);
						newLine.setAmount(rs.getBigDecimal("AmtAcctDr")
								.subtract(rs.getBigDecimal("AmtAcctCr")));
						String description = rs.getString("Description");
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
		if (!lastDate.equals(params.getDateDocTo())) {
			MBAYInterestCalculationLine newLine = new MBAYInterestCalculationLine(
					ic);
			newLine.setDateTrx(params.getDateDocTo());
			newLine.setAmount(BigDecimal.ZERO);
			newLine.setDescription("Zinsabschluss");  // XXX translation
			newLine.setInterestPercent(record.getInterestPercent());
			newLine.saveEx(get_TrxName());
		}
		
		// logging
		addLog(getProcessInfo().getAD_Process_ID(),
				new Timestamp(System.currentTimeMillis()), new BigDecimal(
						getProcessInfo().getAD_PInstance_ID()), "new: ",
				MBAYInterestCalculation.Table_ID, ic.get_ID());
		return null;
	}

}
