package de.bayen.bx.onlinebanking.hbci;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MProcess;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.ServerProcessCtl;
import org.compiere.util.Env;

import de.bayen.bx.util.AbstractSvrProcess;

public class FullBankStatementImport extends AbstractSvrProcess {

	/**	Client to be imported to			*/
	private int				m_AD_Client_ID = 0;

	/** Organization to be imported to			*/
	private int				m_AD_Org_ID = 0;

	/** Ban Statement Loader				*/
	private int m_C_BankStmtLoader_ID = 0;

	/** File to be imported					*/
	private String fileName = "";

	/** File to be imported					*/
	private Timestamp trxDateFrom;

	/** File to be imported					*/
	private Timestamp trxDateTo;

	/** Current context					*/
	private Properties m_ctx;

	@Override
	protected void prepare() {
		log.info("");
		m_ctx = Env.getCtx();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("C_BankStatementLoader_ID"))
				m_C_BankStmtLoader_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if (name.equals("FileName"))
				fileName = (String)para[i].getParameter();
			else if (name.equals("DateTrx")) {
				trxDateFrom = (Timestamp)para[i].getParameter();
				trxDateTo = (Timestamp)para[i].getParameter_To();
			}
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		m_AD_Client_ID = Env.getAD_Client_ID(m_ctx);
		if (log.isLoggable(Level.INFO)) log.info("AD_Client_ID=" + m_AD_Client_ID);
		m_AD_Org_ID = Env.getAD_Org_ID(m_ctx);
		if (log.isLoggable(Level.INFO)){
			log.info("AD_Org_ID=" + m_AD_Org_ID);
			log.info("C_BankStatementLoader_ID=" + m_C_BankStmtLoader_ID);
		}

	}

	@Override
	protected String doIt() throws Exception {

		ProcessInfo pi = new ProcessInfo("", MProcess.getProcess_ID("BAY MT940Import", get_TrxName()));

		ArrayList<ProcessInfoParameter> processParams = new ArrayList<ProcessInfoParameter>();
		ProcessInfoParameter pip;
		pip = new ProcessInfoParameter("C_BankStatementLoader_ID", new BigDecimal(m_C_BankStmtLoader_ID), null, null, null);
		processParams.add(pip);
		pip = new ProcessInfoParameter("DateTrx", trxDateFrom, trxDateTo, null, null);
		processParams.add(pip);
		pi.setParameter(processParams.toArray(new ProcessInfoParameter[]{}));

		ServerProcessCtl.process(pi, null);
		
		String msg = pi.getLogInfo(true) + pi.getSummary();
		
		//Load Bank Statement
		pi = new ProcessInfo("", 247);

		processParams = new ArrayList<ProcessInfoParameter>();
		pip = new ProcessInfoParameter("C_BankStatementLoader_ID", new BigDecimal(m_C_BankStmtLoader_ID), null, null, null);
		processParams.add(pip);
		pip = new ProcessInfoParameter("FileName", fileName, null, null, null);
		processParams.add(pip);
		pi.setParameter(processParams.toArray(new ProcessInfoParameter[]{}));

		ServerProcessCtl.process(pi, null);
		
		msg = msg + " " + pi.getLogInfo(true) + pi.getSummary();
		
		//Create All Payments
		pi = new ProcessInfo("", MProcess.getProcess_ID("C_BankStatement_CreateAllPayments", get_TrxName()));
		ServerProcessCtl.process(pi, null);

		msg = msg + " " + pi.getLogInfo(true) + pi.getSummary();

		return msg;
	}

}
