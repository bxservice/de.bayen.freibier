package de.bayen.freibier.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MInvoiceLine;
import org.compiere.model.Query;
import org.compiere.util.DB;

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
	protected MBAYInterestCalculationLine[] getLines(String whereClause) {
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

	/**
	 * This method does the same as {@link #recalculate()} but it does not use
	 * the persistence layer. This allows to recalculate the header from the
	 * afterSave() method of the line record. If we use the persistence layer
	 * then the header object changes and the gui does not allow to enter a new
	 * line before a (manual) refresh on the header object is made.
	 * 
	 * If you switch to the header tab the value is still refreshed (in swing).
	 * I do not know how but this works. :-)
	 * 
	 * I choosed this way after looking how JJ did it in InvoiceLine so I think
	 * this is the right way.
	 */
	public void recalculateSQL() {
		// Update Invoice Header
		// TotalLines, InterestAmt, TotalAmt
		// @formatter:off
		String sql = "UPDATE " + MBAYInterestCalculation.Table_Name + " ic "
				//
				+ " SET " + COLUMNNAME_TotalLines + "=" 
				+ "(SELECT COALESCE(SUM(line." + MBAYInterestCalculationLine.COLUMNNAME_Amount + "),0) "
				+ " FROM " + MBAYInterestCalculationLine.Table_Name
				+ " line WHERE(ic."+MBAYInterestCalculation.COLUMNNAME_BAY_InterestCalculation_ID 
				+ "=line."+MBAYInterestCalculation.COLUMNNAME_BAY_InterestCalculation_ID+ ")) "
				//
				+ " , " + COLUMNNAME_InterestAmt + "=" 
				+ "(SELECT COALESCE(SUM(line." + MBAYInterestCalculationLine.COLUMNNAME_LineTotalAmt + "),0) "
				+ " FROM " + MBAYInterestCalculationLine.Table_Name
				+ " line WHERE(ic."+MBAYInterestCalculation.COLUMNNAME_BAY_InterestCalculation_ID 
				+ "=line."+MBAYInterestCalculation.COLUMNNAME_BAY_InterestCalculation_ID+ ")) "
				//
				+ " , " + COLUMNNAME_TotalAmt + "=" 
				+ "(SELECT COALESCE(SUM(line." + MBAYInterestCalculationLine.COLUMNNAME_Amount + "),0) + "
				+ " COALESCE(SUM(line." + MBAYInterestCalculationLine.COLUMNNAME_LineTotalAmt + "),0) "
				+ " FROM " + MBAYInterestCalculationLine.Table_Name
				+ " line WHERE(ic."+MBAYInterestCalculation.COLUMNNAME_BAY_InterestCalculation_ID 
				+ "=line."+MBAYInterestCalculation.COLUMNNAME_BAY_InterestCalculation_ID+ ")) "
				//
				+ "WHERE " + MBAYInterestCalculation.COLUMNNAME_BAY_InterestCalculation_ID + "=?";
		// @formatter:on
		int no = DB.executeUpdateEx(sql, new Object[] { get_ID() },
				get_TrxName());
		if (no != 1)
			log.warning("(1) #" + no);
	}

}
