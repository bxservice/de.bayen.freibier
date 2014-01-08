package de.bayen.freibier.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MInvoiceLine;
import org.compiere.model.Query;

public class MBAYInterestCalculation extends X_BAY_InterestCalculation {

	private static final long serialVersionUID = 1L;

	public MBAYInterestCalculation(Properties ctx,
			int BAY_InterestCalculation_ID, String trxName) {
		super(ctx, BAY_InterestCalculation_ID, trxName);
	}

	public MBAYInterestCalculation(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Get Lines
	 * 
	 * @param whereClause
	 *            starting with AND
	 * @return lines
	 */
	private MBAYInterestCalculationLine[] getLines(String whereClause) {
		String whereClauseFinal = COLUMNNAME_BAY_InterestCalculation_ID + "=? ";
		if (whereClause != null)
			whereClauseFinal += whereClause;
		List<MInvoiceLine> list = new Query(getCtx(),
				I_BAY_InterestCalculationLine.Table_Name, whereClauseFinal,
				get_TrxName()).setParameters(get_ID())
				.setOrderBy(I_BAY_InterestCalculationLine.COLUMNNAME_Line)
				.list();
		return list.toArray(new MBAYInterestCalculationLine[list.size()]);
	} // getLines

	public void recalculate() {
		MBAYInterestCalculationLine[] lines = getLines(null);
		Calendar date = null;
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal interestSum = BigDecimal.ZERO;
		for (MBAYInterestCalculationLine line : lines) {
			Calendar linedate = new GregorianCalendar();
			linedate.setTime(line.getDateTrx());
			if (date != null) {
				int days = (linedate.get(Calendar.YEAR) - date
						.get(Calendar.YEAR)) * 360;
				days += (linedate.get(Calendar.MONTH) - date
						.get(Calendar.MONTH)) * 30;
				days += (linedate.get(Calendar.DAY_OF_MONTH) - date
						.get(Calendar.DAY_OF_MONTH));
				days -= linedate.get(Calendar.DAY_OF_MONTH) == 31 ? 1 : 0;
				days += date.get(Calendar.DAY_OF_MONTH) == 31 ? 1 : 0;
				line.setDays(days);
			} else {
				line.setDays(0);
			}
			date = linedate;
			// sum*(percent/100)*(days/360)
			BigDecimal interest = sum.multiply(line.getInterestPercent())
					.multiply(new BigDecimal(line.getDays()))
					.divide(new BigDecimal(100 * 360), 2, RoundingMode.HALF_UP);
			line.setLineTotalAmt(interest);
			interestSum = interestSum.add(interest);
			sum = sum.add(line.getAmount());
			line.saveEx();
		}
		setInterestAmt(interestSum);
		setTotalLines(sum);
		setTotalAmt(sum.add(interestSum));
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		recalculate();
		return super.beforeSave(newRecord);
	}
}
