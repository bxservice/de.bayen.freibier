package de.bayen.freibier.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.util.DB;

public class MBAYInterestCalculationLine extends X_BAY_InterestCalculationLine {

	private static final long serialVersionUID = 1L;

	public MBAYInterestCalculationLine(Properties ctx,
			int BAY_InterestCalculationLine_ID, String trxName) {
		super(ctx, BAY_InterestCalculationLine_ID, trxName);
	}

	public MBAYInterestCalculationLine(Properties ctx, ResultSet rs,
			String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Get Line No
		if (getLine() == 0) {
			String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM " + Table_Name
					+ " WHERE " + COLUMNNAME_BAY_InterestCalculation_ID + "=?";
			int ii = DB.getSQLValue(get_TrxName(), sql,
					getBAY_InterestCalculation_ID());
			setLine(ii);
		}

		return super.beforeSave(newRecord);
	}

}
