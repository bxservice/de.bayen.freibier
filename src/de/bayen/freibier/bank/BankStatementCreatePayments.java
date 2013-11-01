package de.bayen.freibier.bank;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAcctSchemaElement;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.model.X_C_AcctSchema_Element;
import org.compiere.model.X_I_BankStatement;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 * Create Payments from Bank Statement Info
 * 
 * Bisher weiss ich noch nicht so genau, was dieser Prozess am Ende alles machen soll. 
 * So wie es aussieht, gibt es einen Payment-Erzeuger und einen Matcher. Beide arbeiten 
 * jedoch nur auf einem einzigen Datensatz und nicht auf der ganzen Tabelle. (Ich glaube, 
 * das Problem kommt bestimmt noch öfter.)
 * 
 * Ziel ist also, zuerst Payments von allen (?) Datensätzen zu erzeugen. Dabei soll im Gegensatz
 * zu de.compiere.process.BankStatementPayment der Wert für UserElement1 (Vertrag) kopiert werden.
 * 
 * @author tbayen
 */
public class BankStatementCreatePayments extends SvrProcess {

	private Properties m_ctx;
	private String m_userElement1ColumnName = null;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		//
		m_ctx = Env.getCtx();
		// User Defined References
		MAcctSchema[] ass = MAcctSchema.getClientAcctSchema(m_ctx, getAD_Client_ID());
		if (ass.length != 1)
			throw new AdempiereException("Not implemented for more than one Accounting Schema.");
		MAcctSchemaElement ud1 = ass[0]
				.getAcctSchemaElement(X_C_AcctSchema_Element.ELEMENTTYPE_UserElement1);
		if (ud1 != null)
			m_userElement1ColumnName = ud1.getDisplayColumnName();
	}

	@Override
	protected String doIt() throws Exception {
		int Table_ID = getTable_ID();
		if (log.isLoggable(Level.INFO)) log.info ("Table_ID=" + Table_ID);
		
		if (Table_ID == X_I_BankStatement.Table_ID)
			return createPaymentAccountingElementsImport();
		else if (Table_ID == MBankStatement.Table_ID)
			return createPaymentAccountingElements(new MBankStatement(getCtx(), getRecord_ID(), get_TrxName()));
		throw new AdempiereSystemError("??");
	}

	private String createPaymentAccountingElementsImport() {
		StringBuilder sql = new StringBuilder("SELECT * FROM I_BankStatement")
				.append(" WHERE I_IsImported='N'");

		CPreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();

			while (rs.next()) {
				X_I_BankStatement imp = new X_I_BankStatement(m_ctx, rs, get_TrxName());
				createPayment(imp);
				commitEx();
			}
		} catch (Exception ex) {
			log.log(Level.SEVERE, sql.toString(), ex);
		} finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		return "OK";
	}

	private String createPaymentAccountingElements(MBankStatement mBankStatement) {
		MBankStatementLine[] lines = mBankStatement.getLines(false);
		try {
			for (MBankStatementLine mBankStatementLine : lines) {
				addLog(createPayment(mBankStatementLine));
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "can not create payment", e);
		}
		return "OK";
	}

	/**
	 * copied from org.compiere.process.BankStatementPayment
	 * 
	 * 	Create Payment for Import
	 *	@param ibs import bank statement
	 *	@return Message
	 *  @throws Exception if not successful
	 */
	private String createPayment (X_I_BankStatement ibs) throws Exception
	{
		if (ibs == null || ibs.getC_Payment_ID() != 0)
			return "--";
		if (log.isLoggable(Level.FINE)) log.fine(ibs.toString());
		if (ibs.getC_Invoice_ID() == 0 && ibs.getC_BPartner_ID() == 0)
			throw new AdempiereUserError ("@NotFound@ @C_Invoice_ID@ / @C_BPartner_ID@");
		if (ibs.getC_BankAccount_ID() == 0)
			throw new AdempiereUserError ("@NotFound@ @C_BankAccount_ID@");
		//
		Timestamp valuta = ibs.getValutaDate();
		if(valuta==null)
			valuta=ibs.getStatementLineDate();
		if(valuta==null)
			valuta=ibs.getStatementDate();
		MPayment payment = createPayment (ibs.getC_Invoice_ID(), ibs.getC_BPartner_ID(),
			ibs.getC_Currency_ID(), ibs.getStmtAmt(), ibs.getTrxAmt(), 
			ibs.getC_BankAccount_ID(), valuta, 
			ibs.getDateAcct(), ibs.getDescription(), ibs.getAD_Org_ID());
		if (payment == null)
			throw new AdempiereSystemError("Could not create Payment");
		
		ibs.setC_Payment_ID(payment.getC_Payment_ID());
		ibs.setC_Currency_ID (payment.getC_Currency_ID());
		ibs.setTrxAmt(payment.getPayAmt(true));
		ibs.saveEx();
		//
		StringBuilder retString = new StringBuilder("@C_Payment_ID@ = ").append(payment
				.getDocumentNo());
		if (payment.getOverUnderAmt().signum() != 0)
			retString.append(" - @OverUnderAmt@=").append(payment.getOverUnderAmt());
		addLog(getProcessInfo().getAD_Process_ID(), new Timestamp(System.currentTimeMillis()),
				new BigDecimal(getProcessInfo().getAD_PInstance_ID()), retString.toString(),
				MPayment.Table_ID, payment.get_ID());
		return retString.toString();
	}	//	createPayment - Import

	/**
	 * 	Create Payment for BankStatement
	 *	@param bsl bank statement Line
	 *	@return Message
	 *  @throws Exception if not successful
	 */
	private String createPayment (MBankStatementLine bsl) throws Exception
	{
		if (bsl == null || bsl.getC_Payment_ID() != 0)
			return "--";
		if (log.isLoggable(Level.FINE)) log.fine(bsl.toString());
		if (bsl.getC_Invoice_ID() == 0 && bsl.getC_BPartner_ID() == 0)
			throw new AdempiereUserError ("@NotFound@ @C_Invoice_ID@ / @C_BPartner_ID@");
		//
		MBankStatement bs = new MBankStatement (getCtx(), bsl.getC_BankStatement_ID(), get_TrxName());
		//
		MPayment payment = createPayment (bsl.getC_Invoice_ID(), bsl.getC_BPartner_ID(),
			bsl.getC_Currency_ID(), bsl.getStmtAmt(), bsl.getTrxAmt(), 
			bs.getC_BankAccount_ID(), bsl.getStatementLineDate(), bsl.getDateAcct(),
			bsl.getDescription(), bsl.getAD_Org_ID());
		if (payment == null)
			throw new AdempiereSystemError("Could not create Payment");
		//	update statement
		bsl.setPayment(payment);
		bsl.saveEx();
		//
		StringBuilder retString = new StringBuilder("@C_Payment_ID@ = ").append(payment.getDocumentNo());
		if (payment.getOverUnderAmt().signum() != 0)
			retString.append(" - @OverUnderAmt@=").append(payment.getOverUnderAmt());
		return retString.toString();
	}	//	createPayment

	/**
	 * copied from org.compiere.process.BankStatementPayment
	 * 
	 * I changed it so the UserElement1 value is taken from Invoice and 
	 * written to Payment (FreiBier uses this for Contracts)
	 * 
	 * 	Create actual Payment
	 *	@param C_Invoice_ID invoice
	 *	@param C_BPartner_ID partner ignored when invoice exists
	 *	@param C_Currency_ID currency
	 *	@param StmtAmt statement amount
	 *	@param TrxAmt transaction amt
	 *	@param C_BankAccount_ID bank account
	 *	@param DateTrx transaction date
	 *	@param DateAcct	accounting date
	 *	@param Description description
	 *	@param AD_Org_ID org
	 *	@return payment
	 */
	private MPayment createPayment (int C_Invoice_ID, int C_BPartner_ID, 
		int C_Currency_ID, BigDecimal StmtAmt, BigDecimal TrxAmt,
		int C_BankAccount_ID, Timestamp DateTrx, Timestamp DateAcct, 
		String Description, int AD_Org_ID)
	{
		//	Trx Amount = Payment overwrites Statement Amount if defined
		BigDecimal PayAmt = TrxAmt;
		if (PayAmt == null || Env.ZERO.compareTo(PayAmt) == 0)
			PayAmt = StmtAmt;
		if (C_Invoice_ID == 0
			&& (PayAmt == null || Env.ZERO.compareTo(PayAmt) == 0))
			throw new IllegalStateException ("@PayAmt@ = 0");
		if (PayAmt == null)
			PayAmt = Env.ZERO;
		//
		MPayment payment = new MPayment (getCtx(), 0, get_TrxName());
		payment.setAD_Org_ID(AD_Org_ID);
		payment.setC_BankAccount_ID(C_BankAccount_ID);
		payment.setTenderType(MPayment.TENDERTYPE_Check);
		if (DateTrx != null)
			payment.setDateTrx(DateTrx);
		else if (DateAcct != null)
			payment.setDateTrx(DateAcct);
		if (DateAcct != null)
			payment.setDateAcct(DateAcct);
		else
			payment.setDateAcct(payment.getDateTrx());
		payment.setDescription(Description);
		//
		if (C_Invoice_ID != 0)
		{
			MInvoice invoice = new MInvoice (getCtx(), C_Invoice_ID, null);
			payment.setC_DocType_ID(invoice.isSOTrx());		//	Receipt
			payment.setC_Invoice_ID(invoice.getC_Invoice_ID());
			payment.setC_BPartner_ID (invoice.getC_BPartner_ID());
			// description is copied - tbayen
			payment.setDescription(invoice.getDescription());
			// UserElement1/Contract - tbayen
			if (m_userElement1ColumnName != null) {
				int colI = invoice.get_ColumnIndex(m_userElement1ColumnName);
				if (colI != -1) { // Invoice uses UserElement1 field
					int colP = payment.get_ColumnIndex(m_userElement1ColumnName);
					if(colP != -1) {  // Payment uses UserElement1 field too
						Object val = invoice.get_Value(m_userElement1ColumnName);
						payment.set_ValueOfColumn(m_userElement1ColumnName, val);
					}
				}
			}
			//
			if (PayAmt.signum() != 0)	//	explicit Amount
			{
				payment.setC_Currency_ID(C_Currency_ID);
				if (invoice.isSOTrx())
					payment.setPayAmt(PayAmt);
				else	//	payment is likely to be negative
					payment.setPayAmt(PayAmt.negate());
				payment.setOverUnderAmt(invoice.getGrandTotal(true).subtract(payment.getPayAmt()));
			}
			else	// set Pay Amout from Invoice
			{
				payment.setC_Currency_ID(invoice.getC_Currency_ID());
				payment.setPayAmt(invoice.getGrandTotal(true));
			}
		}
		else if (C_BPartner_ID != 0)
		{
			payment.setC_BPartner_ID(C_BPartner_ID);
			payment.setC_Currency_ID(C_Currency_ID);
			if (PayAmt.signum() < 0)	//	Payment
			{
				payment.setPayAmt(PayAmt.abs());
				payment.setC_DocType_ID(false);
			}
			else	//	Receipt
			{
				payment.setPayAmt(PayAmt);
				payment.setC_DocType_ID(true);
			}
		}
		else
			return null;
		payment.saveEx();
		//
		if (!payment.processIt(MPayment.DOCACTION_Complete)) {
			log.warning("Payment Process Failed: " + payment.getDocumentNo() + " " + payment.getProcessMsg());
			throw new IllegalStateException("Payment Process Failed: " + payment.getDocumentNo() + " " + payment.getProcessMsg());
			
		}
		payment.saveEx();
		return payment;		
	}	//	createPayment

}
