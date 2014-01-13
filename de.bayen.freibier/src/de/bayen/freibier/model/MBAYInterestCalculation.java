package de.bayen.freibier.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MPeriod;
import org.compiere.model.MSysConfig;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

public class MBAYInterestCalculation extends AbstractMBAYInterestCalculation<MBAYInterestCalculationLine> implements
		DocAction {

	private static final long serialVersionUID = 1L;

	public MBAYInterestCalculation(Properties ctx, int BAY_InterestCalculation_ID, String trxName) {
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
	public MBAYInterestCalculationLine[] getLines() {
		String whereClause = COLUMNNAME_BAY_InterestCalculation_ID + "=? ";
		List<MInvoiceLine> list = new Query(getCtx(), I_BAY_InterestCalculationLine.Table_Name, whereClause,
				get_TrxName()).setParameters(get_ID()).setOrderBy(I_BAY_InterestCalculationLine.COLUMNNAME_Line).list();
		return list.toArray(new MBAYInterestCalculationLine[list.size()]);
	}

	/**
	 * This method allows to recalculate the header from the afterSave() method
	 * of the line record. If we use the persistence layer then the header
	 * object changes and the gui does not allow to enter a new line before a
	 * (manual) refresh on the header object is made.
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
		int no = DB.executeUpdateEx(sql, new Object[] { get_ID() }, get_TrxName());
		if (no != 1)
			log.warning("(1) #" + no);
	}

	// DocAction
	// **********

	// getter methods

	@Override
	public String getSummary() {
		// 1004711: InterestAmt = 123.00 (#1) - name
		StringBuilder sb = new StringBuilder();
		sb.append(getDocumentNo());
		sb.append(": ").append(Msg.translate(getCtx(), "InterestAmt")).append("=").append(getInterestAmt())
				.append(" (#").append(getLines().length).append(")");
		if (!Util.isEmpty(getName()))
			sb.append(" - ").append(getName());
		return sb.toString();
	}

	@Override
	public BigDecimal getApprovalAmt() {
		return getInterestAmt();
	}

	public MBAYInterestCalculation reverse(boolean accrual) {
		Timestamp reversalDate = accrual ? Env.getContextAsDate(getCtx(), "#Date") : getDateAcct();
		if (reversalDate == null) {
			reversalDate = new Timestamp(System.currentTimeMillis());
		}
		Timestamp reversalDateDoc = accrual ? reversalDate : getDateDoc();
		
		MPeriod.testPeriodOpen(getCtx(), reversalDate, getC_DocType_ID(), getAD_Org_ID());

		MBAYInterestCalculation reversal = null;
		if (MSysConfig.getBooleanValue(MSysConfig.Invoice_ReverseUseNewNumber, true, getAD_Client_ID()))
			reversal = copyFrom(this, reversalDateDoc, reversalDate, getC_DocType_ID(), get_TrxName(), null);
		else
			reversal = copyFrom(this, reversalDateDoc, reversalDate, getC_DocType_ID(), get_TrxName(), getDocumentNo()+"^");
		if (reversal == null){
			m_processMsg = "Could not create Reversal Document";
			return null;
		}
		MBAYInterestCalculationLine[] lines = getLines();
		for (MBAYInterestCalculationLine line : lines) {
			line.setAmount(line.getAmount().negate());
			if (!line.save(get_TrxName())){
				m_processMsg = "Could not correct Reversal Line";
				return null;
			}
		}
		reversal.setName("{-> "+getName()+"}");
		reversal.setReversal_ID(get_ID());
		reversal.saveEx(get_TrxName());
		if (!reversal.processIt(DocAction.ACTION_Complete)){
			m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
			return null;
		}
		reversal.closeIt();
		reversal.setProcessing (false);
		reversal.setDocStatus(DOCSTATUS_Reversed);
		reversal.setDocAction(DOCACTION_None);
		reversal.saveEx(get_TrxName());
		//
		String desc= Util.isEmpty(getDescription())?"":getDescription()+" ";
		desc+="("+reversal.getDocumentNo()+"<-)";
		setDescription(desc);
		setProcessed(true);
		setReversal_ID(reversal.get_ID());
		setDocStatus(DOCSTATUS_Reversed);	//	may come from void
		setDocAction(DOCACTION_None);
		return reversal;
	}

	public static MBAYInterestCalculation copyFrom(MBAYInterestCalculation from, Timestamp dateDoc, Timestamp dateAcct,
			int C_DocTypeTarget_ID, String trxName, String DocumentNo) {
		MBAYInterestCalculation to = new MBAYInterestCalculation(from.getCtx(), 0, trxName);
		PO.copyValues(from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
		to.set_ValueNoCheck(COLUMNNAME_DocumentNo, DocumentNo);
		to.setDocStatus(DOCSTATUS_Drafted); // Draft
		to.setDocAction(DOCACTION_Complete);
		//
		to.setC_DocType_ID(C_DocTypeTarget_ID);
		//
		to.setDateDoc(dateDoc);
		to.setDateAcct(dateAcct);
		to.setIsApproved(false);
		// Amounts are updated by trigger when adding lines
		to.setTotalLines(Env.ZERO);
		to.setInterestAmt(Env.ZERO);
		to.setTotalAmt(Env.ZERO);
		//
		to.setPosted(false);
		to.setProcessed(false);
		// [ 1633721 ] Reverse Documents- Processing=Y
		to.setProcessing(false);
		//
		to.saveEx(trxName);

		// Lines
		if (to.copyLinesFrom(from) == 0)
			throw new IllegalStateException("Could not create Lines");

		return to;
	}

	public int copyLinesFrom(MBAYInterestCalculation otherDocument) {
		if (isProcessed() || isPosted() || otherDocument == null)
			return 0;
		MBAYInterestCalculationLine[] fromLines = otherDocument.getLines();
		int count = 0;
		for (int i = 0; i < fromLines.length; i++) {
			MBAYInterestCalculationLine line = new MBAYInterestCalculationLine(getCtx(), 0, get_TrxName());
			MBAYInterestCalculationLine fromLine = fromLines[i];
			PO.copyValues(fromLine, line, getAD_Client_ID(), getAD_Org_ID());
			line.setBAY_InterestCalculation_ID(get_ID());
			line.set_ValueNoCheck(MBAYInterestCalculationLine.COLUMNNAME_BAY_InterestCalculationLine_ID, I_ZERO); // new
			if (line.save(get_TrxName()))
				count++;
		}
		if (fromLines.length != count) {
			String msg = "Line difference - From=" + fromLines.length + " <> Saved=" + count;
			log.log(Level.SEVERE, msg);
			throw new AdempiereException(msg);
		}
		return count;
	}

}
