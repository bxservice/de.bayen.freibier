package de.bayen.bx.leu.acct;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.base.IDocFactory;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.acct.Doc;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MInvoice;
import org.compiere.model.MTable;
import org.compiere.util.CLogger;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class DocFactory implements IDocFactory {

	protected static CLogger s_log = CLogger.getCLogger(DocFactory.class);

	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, int Record_ID,
			String trxName) {
		Properties ctx = Env.getCtx();
		String tableName = MTable.getTableName(ctx, AD_Table_ID);
		String sql = "SELECT * FROM " + tableName + " WHERE " + tableName
				+ "_ID=? AND Processed='Y'";
		CPreparedStatement stat = DB.prepareStatement(sql, trxName);
		ResultSet rs = null;
		try {
			stat.setInt(1, Record_ID);
			rs = stat.executeQuery();
			if (rs.next()) {
				return getDocument(as, AD_Table_ID, rs, trxName);
			}
			s_log.severe("Not Found: " + tableName + "_ID=" + Record_ID);
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, sql, e);
			throw new AdempiereException(e);
		} finally {
			DB.close(rs);
			rs = null;
			stat = null;
		}
		return null;
	}

	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, ResultSet rs,
			String trxName) {
		String tableName = MTable.getTableName(Env.getCtx(), AD_Table_ID);
		if (MInvoice.Table_Name.equals(tableName))
			return new Doc_Invoice(as, rs, trxName);
		return null;
	}

}
