package de.bayen.freibier.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.compiere.model.Query;
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

		recalculate();
		return super.beforeSave(newRecord);
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		// nachfolgende Zeilen neu berechnen
		StringBuilder whereClause = new StringBuilder();
		whereClause.append(COLUMNNAME_BAY_InterestCalculation_ID + "=? "); // #1
		whereClause.append(" AND " + COLUMNNAME_DateTrx + ">? "); // #2
		// @formatter:off
		MBAYInterestCalculationLine followingline = 
				new Query(getCtx(), Table_Name, whereClause.toString(), get_TrxName())
		        	.setParameters(getBAY_InterestCalculation_ID(), getDateTrx())
		        	.setOrderBy(COLUMNNAME_DateTrx)
		        	.first();
		// @formatter:on
		if (followingline != null) {
			// Diese Zeile wiederum ruft Ihre Nachfolger auf,
			// so das alles neu berechnet wird.
			followingline.recalculate();
			followingline.saveEx(get_TrxName());
		}

		// recalculate header
		MBAYInterestCalculation parent = ((MBAYInterestCalculation) getBAY_InterestCalculation());
		parent.recalculateSQL();
		// parent.hasToBeRecalculated();
		// parent.saveEx(get_TrxName());

		return super.afterSave(newRecord, success);
	}

	public static int calculateDays(Properties ctx, Timestamp dateTrx,
			int headerID, int myID, String trxName) {
		if (dateTrx == null) {
			return 0;
		}
		StringBuilder whereClause = new StringBuilder();
		whereClause.append(COLUMNNAME_BAY_InterestCalculation_ID + "=? "); // #1
		whereClause.append(" AND " + COLUMNNAME_DateTrx + "<? "); // #2
		whereClause.append(" AND " + COLUMNNAME_BAY_InterestCalculationLine_ID
				+ "!=? "); // #3
		// @formatter:off
		MBAYInterestCalculationLine lastline = 
				new Query(ctx, Table_Name, whereClause.toString(), trxName)
		        	.setParameters(headerID, dateTrx, myID)
		        	.setOrderBy(COLUMNNAME_DateTrx + " DESC")
		        	.first();
		// @formatter:on

		if (lastline == null)
			return 0;
		else {
			Calendar lastdate = new GregorianCalendar();
			lastdate.setTime(lastline.getDateTrx());
			Calendar linedate = new GregorianCalendar();
			linedate.setTime(dateTrx);
			int days = (linedate.get(Calendar.YEAR) - lastdate
					.get(Calendar.YEAR)) * 360;
			days += (linedate.get(Calendar.MONTH) - lastdate
					.get(Calendar.MONTH)) * 30;
			days += (linedate.get(Calendar.DAY_OF_MONTH) - lastdate
					.get(Calendar.DAY_OF_MONTH));
			days -= linedate.get(Calendar.DAY_OF_MONTH) == 31 ? 1 : 0;
			days += lastdate.get(Calendar.DAY_OF_MONTH) == 31 ? 1 : 0;
			return days;
		}
	}

	/**
	 * Running total of the lines before my own (not including my own value).
	 * 
	 * @param ctx
	 * @param dateTrx
	 * @param headerID
	 * @param myID
	 * @param trxName
	 * @return
	 */
	static public BigDecimal calculateRunningTotal(Properties ctx,
			Timestamp dateTrx, int headerID, int myID, String trxName) {
		// calculate actual running total sum
		StringBuilder whereClause = new StringBuilder();
		whereClause.append(COLUMNNAME_BAY_InterestCalculation_ID + "=? "); // #1
		whereClause.append(" AND " + COLUMNNAME_DateTrx + "<? "); // #2
		whereClause.append(" AND " + COLUMNNAME_BAY_InterestCalculationLine_ID
				+ "!=? "); // #3
		// @formatter:off
		return new Query(ctx, Table_Name, whereClause.toString(), trxName)
		        	.setParameters(headerID, dateTrx, myID)
		        	.aggregate(COLUMNNAME_Amount, Query.AGGREGATE_SUM);
		// @formatter:on
	}

	static public BigDecimal calculateInterest(BigDecimal sum, BigDecimal percent,
			int days) {
		return sum.multiply(percent).multiply(new BigDecimal(days))
				.divide(new BigDecimal(100 * 360), 2, RoundingMode.HALF_UP);
	}

	private void recalculate() {
		setDays(calculateDays(getCtx(), getDateTrx(),
				getBAY_InterestCalculation_ID(), get_ID(), get_TrxName()));
		BigDecimal sum = calculateRunningTotal(getCtx(), getDateTrx(),
				getBAY_InterestCalculation_ID(), get_ID(), get_TrxName());
		// calculate interest
		setLineTotalAmt(calculateInterest(sum, getInterestPercent(), getDays()));
	}
}
