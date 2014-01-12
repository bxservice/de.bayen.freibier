package de.bayen.freibier.model;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MPeriod;
import org.compiere.model.MQuery;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PrintInfo;
import org.compiere.model.Query;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportEngine;
import org.compiere.process.DocAction;
import org.compiere.process.DocumentEngine;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ServerProcessCtl;
import org.compiere.util.DB;
import org.compiere.util.Msg;
import org.compiere.util.Util;

public class MBAYInterestCalculation extends X_BAY_InterestCalculation implements DocAction {

	private static final long serialVersionUID = 1L;

	/** status change error message */
	String m_processMsg; 
	/**	Just Prepared Flag			*/
	private boolean		m_justPrepared = false;

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
	public MBAYInterestCalculationLine[] getLines(String whereClause) {
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
	
	// DocAction
	//**********

	// getter methods
	
	public String getSummary(){
		// 1004711: InterestAmt = 123.00 (#1) - name
		StringBuilder sb = new StringBuilder();
		sb.append(getDocumentNo());
		sb.append(": ").
			append(Msg.translate(getCtx(),"InterestAmt")).append("=").append(getInterestAmt())
			.append(" (#").append(getLines(null).length).append(")");
		if(!Util.isEmpty(getName()))
			sb.append(" - ").append(getName());
		return sb.toString();
	}

	public String getDocumentInfo(){
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		String msgreturn = dt.getNameTrl()+" "+getDocumentNo();
		return msgreturn.toString();
	}

	public String getProcessMsg(){
		return m_processMsg;
	}
	
	public BigDecimal getApprovalAmt(){
		return getInterestAmt();
	}
	
	// createPDF

	/**************************************************************************
	 * 	Create PDF
	 *	@return File or null
	 */
	public File createPDF (){
		try{
			StringBuilder msgfile = new StringBuilder().append(get_TableName()).append(get_ID()).append("_");
			File temp = File.createTempFile(msgfile.toString(), ".pdf");
			return createPDF (temp);
		}catch (Exception e){
			log.severe("Could not create PDF - " + e.getMessage());
		}
		return null;
	}

	/**
	 * 	Create PDF file
	 *	@param file output file
	 *	@return file if success
	 */
	public File createPDF (File file)
	{
		MPrintFormat format = MPrintFormat.get(getCtx(), 0, get_Table_ID());
		MQuery query=new MQuery(get_Table_ID());
		query.addRestriction(get_KeyColumns()[0],MQuery.EQUAL,get_ID());
		PrintInfo info = new PrintInfo(getDocumentNo(),get_Table_ID(),0);
		ReportEngine re = new ReportEngine(getCtx(), format, query, info, get_TrxName());

		if(format.getJasperProcess_ID() > 0){
			// JasperReports Print Format
			ProcessInfo pi = new ProcessInfo ("", format.getJasperProcess_ID());
			pi.setRecord_ID(get_ID());
			pi.setIsBatch(true);
			ServerProcessCtl.process(pi, null);
			return pi.getPDFReport();
		}else{
			// Standard Print Format (Non-Jasper)
			return re.getPDF(file);
		}
	}

	// Document Action methods
	
	/**
	 * 	Process document
	 * 
	 *	@param processAction document action
	 *	@return true if performed
	 */
	public boolean processIt (String processAction)
	{
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (processAction, getDocAction());
	}

	/**
	 * 	Unlock Document.
	 * 	@return true if success
	 */
	@Override
	public boolean unlockIt() {
		if (log.isLoggable(Level.INFO)) log.info("unlockIt - " + getDocumentInfo());
		setProcessing(false);
		return true;
	}

	/**
	 * 	Invalidate Document
	 * 	@return true if success
	 */
	@Override
	public boolean invalidateIt() {
		if (log.isLoggable(Level.INFO)) log.info("invalidateIt - " + getDocumentInfo());
		setDocAction(DOCACTION_Prepare);
		return true;
	}

	/**
	 *	Prepare Document
	 * 	@return new status (In Progress or Invalid)
	 */
	@Override
	public String prepareIt() {
		if (log.isLoggable(Level.INFO)) log.info("prepareIt - " + getDocumentInfo());
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		
		MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());

		// this document has lines
		Object[] lines = getLines(null);
		if (lines.length == 0){
			m_processMsg = "@NoLines@";
			return DocAction.STATUS_Invalid;
		}
		
		//... insert more code here if appropriate

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Add up Amounts
		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}

	@Override
	public boolean approveIt() {
		if (log.isLoggable(Level.INFO)) log.info("approveIt - " + getDocumentInfo());
		setIsApproved(true);
		return true;
	}

	@Override
	public boolean rejectIt() {
		if (log.isLoggable(Level.INFO)) log.info("rejectIt - " + getDocumentInfo());
		setIsApproved(false);
		return true;
	}

	@Override
	public String completeIt() {
		if (log.isLoggable(Level.INFO)) log.info("completeIt - " + getDocumentInfo());

		//	Re-Check
		if (!m_justPrepared)
		{
			String status = prepareIt();
			if (!DocAction.STATUS_InProgress.equals(status))
				return status;
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//... insert more code here if appropriate
		
		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}

		// Set the definite document number after completed (if needed)
		//setDefiniteDocumentNo();
		m_processMsg=getDocumentInfo()+" - ok";
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}

	@Override
	public boolean voidIt() {
		if (log.isLoggable(Level.INFO)) log.info("voidIt - " + getDocumentInfo());
		
		if (DOCSTATUS_Closed.equals(getDocStatus())
				|| DOCSTATUS_Reversed.equals(getDocStatus())
				|| DOCSTATUS_Voided.equals(getDocStatus())){
			m_processMsg = "Document Closed: " + getDocStatus();
			setDocAction(DOCACTION_None);
			return false;
		}

		if (DOCSTATUS_Drafted.equals(getDocStatus())
				|| DOCSTATUS_Invalid.equals(getDocStatus())
				|| DOCSTATUS_InProgress.equals(getDocStatus())
				|| DOCSTATUS_Approved.equals(getDocStatus())
				|| DOCSTATUS_NotApproved.equals(getDocStatus()) ){
				// Before Void
				m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
				if (m_processMsg != null)
					return false;

				//... insert more code here if appropriate

		}

		// After Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);
		return true;
	}

	@Override
	public boolean closeIt() {
		if (log.isLoggable(Level.INFO)) log.info("closeIt - " + getDocumentInfo());
		// Before Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_CLOSE);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);

		// After Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_CLOSE);
		if (m_processMsg != null)
			return false;
		return true;
	}

	@Override
	public boolean reverseCorrectIt() {
		if (log.isLoggable(Level.INFO)) log.info("reverseCorrectIt - " + getDocumentInfo());
		// Before reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		DocAction reversal = reverse(false);
		if (reversal == null)
			return false;

		// After reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		m_processMsg = reversal.getDocumentNo();
		
		return true;
	}
	
	public MBAYInterestCalculation reverse(boolean accrual){
		// TODO implementieren
		throw new AdempiereException("not implemented");

		//... insert more code here if appropriate

		// setProcessed(true);
		// setReversal_ID(reversal.getC_Invoice_ID());
		// setDocStatus(DOCSTATUS_Reversed); // may come from void
		// setDocAction(DOCACTION_None);
		// return reverse;
	}

	@Override
	public boolean reverseAccrualIt() {
		if (log.isLoggable(Level.INFO)) log.info("reverseAccrualIt - " + getDocumentInfo());
		// Before reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		DocAction reversal = reverse(true);
		if (reversal == null)
			return false;
		
		// After reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		m_processMsg = reversal.getDocumentNo();
		return true;
	}

	@Override
	public boolean reActivateIt() {
		if (log.isLoggable(Level.INFO)) log.info("reActivateIt - " + getDocumentInfo());
		// Before reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;

		//... insert more code here if appropriate

		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;

		return false;
	}

}
