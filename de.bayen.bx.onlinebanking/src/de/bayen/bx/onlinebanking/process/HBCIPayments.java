package de.bayen.bx.onlinebanking.process;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPaySelectionCheck;
import org.compiere.model.MPaymentBatch;

import de.bayen.bx.onlinebanking.hbci.HBCIPaymentProcessor;
import de.bayen.bx.util.AbstractSvrProcess;

public class HBCIPayments extends AbstractSvrProcess {

	public interface Parameter{
		public Integer getC_PaySelection_ID();
		public String getPaymentRule();
	}
	
	protected Class<?>[] getParameterInterfaces() {
		return new Class<?>[] { Parameter.class };
	}
	
	public HBCIPayments() {
		super();
	}

	@Override
	protected String doIt() throws Exception {
		Parameter param=(Parameter) getParameterBean();
		Integer paySelectionID = param.getC_PaySelection_ID();
		String paymentRule = param.getPaymentRule();
		if(paySelectionID==null || paySelectionID < 1)
			return "No PaySelection";
		//MPaySelection ps = new MPaySelection(getCtx(), paySelectionID, get_TrxName());
//		MTable tableCheck = MTable.get(getCtx(), MPaySelectionCheck.Table_ID);
//		String whereClause = MPaySelection.COLUMNNAME_C_PaySelection_ID + "=?";
//		Query query = tableCheck.createQuery(whereClause, get_TrxName());
//		query.setParameters(paySelectionID);
//		List<MPaySelectionCheck> checks = query.list();

		MPaySelection paySelection=new MPaySelection(getCtx(), paySelectionID, get_TrxName());
		MPaySelectionCheck[] checks = MPaySelectionCheck.get(paySelectionID, paymentRule, get_TrxName());
		MPaymentBatch batch = MPaymentBatch.getForPaySelection (getCtx(), paySelectionID, get_TrxName());
		HBCIPaymentProcessor processor = new HBCIPaymentProcessor();
		// deposit? (german Überweisung)
		boolean deposit = "D".equals(paymentRule);
		// TODO in der alten Version wurde hier bei negativen checks die Logik umgedreht
		// Das fand ich häßlich. Man sollte prüfen, ob es so weit überhaupt erst kommen darf.
//		boolean turnaround=false;
//		if(m_checks!=null && m_checks.length>0 && m_checks[0].getPayAmt().compareTo(BigDecimal.ZERO)<0){
//			turnaround=true;
//			deposit=!deposit;
//		}
		try{
			boolean ok = processor.init(getCtx(), paySelection.getC_BankAccount(), deposit, processUI, get_TrxName());
			if (!ok)
				throw new AdempiereException("Kann Zahlungsprozessor nicht initialisieren.");
			for (MPaySelectionCheck check : checks) {
				if (!processor.addCheck(check))
					throw new AdempiereException("kann Zahlungsbeleg nicht versenden (" + check.get_ID() + ")");
			}
			
			batch.saveEx(get_TrxName());
			String erg=processor.process(paySelectionID, batch.get_ID());
			
			if(erg!=null){
				return erg;
			}else{
				MPaySelectionCheck.confirmPrint (checks, batch);
				return "";
			}
		}finally{
			processor.done();
		}
	}
}
