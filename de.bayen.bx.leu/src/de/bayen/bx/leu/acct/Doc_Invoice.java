package de.bayen.bx.leu.acct;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.acct.DocLine;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.I_C_Tax_Acct;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.X_C_Tax_Acct;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;

public class Doc_Invoice extends org.compiere.acct.Doc_Invoice {

	/*
	 * This column has been created in M_Tax_Acct by the OSGi Activator using my
	 * 2Pack file.
	 */
	static final String COLUMNNAME_P_REVENUE_ACCT = "P_Revenue_Acct";

	public Doc_Invoice(MAcctSchema as, ResultSet rs, String trxName) {
		super(as, rs, trxName);
	}

	@Override
	public ArrayList<Fact> createFacts(MAcctSchema as) {
		ArrayList<Fact> facts = super.createFacts(as);
		for (Fact fact : facts) {
			FactLine[] factLines = fact.getLines();
			for (FactLine factLine : factLines) {
				DocLine docLine = factLine.getDocLine();
				if (docLine != null && docLine.getM_Product_ID() > 0
						&& docLine.getC_Tax_ID() > 0) {
					String sql = "SELECT * FROM " + I_C_Tax_Acct.Table_Name
							+ " WHERE " + I_C_Tax_Acct.COLUMNNAME_C_Tax_ID
							+ "=? " + " AND "
							+ I_C_Tax_Acct.COLUMNNAME_C_AcctSchema_ID + "=? ";
					CPreparedStatement pstm = DB.prepareStatement(sql,
							getTrxName());
					X_C_Tax_Acct taxAcct = null;
					try {
						pstm.setInt(1, docLine.getC_Tax_ID());
						pstm.setInt(2, as.get_ID());
						ResultSet rs = pstm.executeQuery();
						if (rs.next())
							taxAcct = new X_C_Tax_Acct(getCtx(), rs,
									getTrxName());
						if (rs.next())
							throw new AdempiereException(
									"this does not work with more than one tax accounting record");
					} catch (SQLException e) {
						throw new AdempiereException(e);
					} finally {
						try {
							if (pstm != null)
								pstm.close();
						} catch (SQLException e) {
							throw new AdempiereException(e);
						}
						pstm = null;
					}
					int revenueAcctCombi_ID = taxAcct
							.get_ValueAsInt(COLUMNNAME_P_REVENUE_ACCT);
					// is RevenueAccountingByTax configured?
					if (revenueAcctCombi_ID <= 0)
						continue;
					factLine.setAccount(as, new MAccount(getCtx(),
							revenueAcctCombi_ID, getTrxName()));
				}
			}
		}
		return facts;
	}

}
