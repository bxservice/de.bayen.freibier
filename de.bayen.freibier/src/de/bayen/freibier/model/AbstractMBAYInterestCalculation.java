package de.bayen.freibier.model;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MDocType;
import org.compiere.model.MFactAcct;
import org.compiere.model.MJournal;
import org.compiere.model.MPeriod;
import org.compiere.model.MQuery;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PrintInfo;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportEngine;
import org.compiere.process.DocAction;
import org.compiere.process.DocActionTemplate;
import org.compiere.process.DocumentEngine;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ServerProcessCtl;

/**
 * This class helps creating your own document class. It is like an abstract
 * base class for all document model classes and it implements most
 * functionality for the {@link DocAction} interface.
 * 
 * Java does not allow multiple inheritance. The model MYYYBla must inherit from
 * X_YYY_Bla and can not inherit from this abstract class. If you want to use
 * this class for more than one usecase you can either copy it and change only
 * the "extends" class. Or we create a delegator class from this.
 * 
 * If you are interested here you might also want to have a look at the
 * {@link DocActionTemplate} class.
 * 
 * @author tbayen
 * 
 * @param <L>
 *            Class of sublines(or Void if this document has no sublines)
 */
abstract public class AbstractMBAYInterestCalculation<L> extends X_BAY_InterestCalculation implements DocAction {
	
	private static final long serialVersionUID = 1L;

	public AbstractMBAYInterestCalculation(Properties ctx,
			int BAY_InterestCalculation_ID, String trxName) {
		super(ctx, BAY_InterestCalculation_ID, trxName);
	}

	public AbstractMBAYInterestCalculation(Properties ctx, ResultSet rs,
			String trxName) {
		super(ctx, rs, trxName);
	}

	// DocAction info methods
	
	/** status change error message */
	String m_processMsg; 
	/**	Just Prepared Flag			*/
	private boolean		m_justPrepared = false;

	public String getDocumentInfo(){
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		String msgreturn = dt.getNameTrl()+" "+getDocumentNo();
		return msgreturn.toString();
	}

	public String getProcessMsg(){
		return m_processMsg;
	}

	/**
	 * sublines of this document type. If it has no subline table at all the result is null, if it is possible to have lines but there are no lines, it gives an array with length 0.
	 * 
	 * @return null or array of lines
	 */
	public L[] getLines(){
		return null;
	}
	
	abstract public String getSummary();
	
	abstract public BigDecimal getApprovalAmt();

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
		L[] lines = getLines();
		if(lines!=null && lines.length == 0){
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
	
	/**
	 * create a reversal document.
	 * 
	 * @param accrual true means the reversal has todays date, false means it has the same date as the original document.
	 * @return reversal document
	 */
	abstract public MBAYInterestCalculation reverse(boolean accrual);

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

	/**
	 * This implementation does not allow reactivation and gives back false in every case.
	 */
	@Override
	public boolean reActivateIt() {
		if (log.isLoggable(Level.INFO)) log.info("reActivateIt - " + getDocumentInfo());
		// Before reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;

		MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
		MFactAcct.deleteEx(MJournal.Table_ID, get_ID(), get_TrxName());

		//... insert more code here if appropriate

		// setPosted(false);
		setProcessed(false);
		setDocAction(DOCACTION_Complete);

		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;

		return false;
	}

}
