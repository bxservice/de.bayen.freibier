package de.bayen.freibier.bank;

import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.impexp.BankStatementMatchInfo;
import org.compiere.impexp.BankStatementMatcherInterface;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAcctSchemaElement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MTable;
import org.compiere.model.X_C_AcctSchema_Element;
import org.compiere.model.X_I_BankStatement;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.Env;

public class BankStatementContainerUnfolding extends SvrProcess {

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		//
	}

	@Override
	protected String doIt() throws Exception {
		int Table_ID = getTable_ID();
		int Record_ID = getRecord_ID();
		if (log.isLoggable(Level.INFO))
			log.info("Table_ID=" + Table_ID + ", Record_ID=" + Record_ID);
		if (Table_ID != X_I_BankStatement.Table_ID)
			throw new AdempiereSystemError("??");

		/*
		 * Ich erkenne Sammler daran, das meine Volksbank bei diesen als
		 * Zahlungsgegenstelle mein eigenes Konto angibt. Der Hibiscus-Loader
		 * schreibt in diesem Fall die BLZ und KtoNr zusammen in ein
		 * entsprechendes Feld. Das kann ich hier erkennen. Außerdem haben
		 * Sammler bei der Volksbank den Transaktionstyp 079, bei der Sparkasse ist das aber leider nicht so einfach.
		 */
		X_I_BankStatement line = new X_I_BankStatement(getCtx(), Record_ID, get_TrxName());
		String bankAccountNo = line.getBankAccountNo();
		if (line.getBankAccountNo() == null || line.getRoutingNo() == null
				|| line.getEftPayeeAccount() == null)
			return null;
		String ktoid = line.getRoutingNo() + line.getBankAccountNo();
		if (!line.getEftPayeeAccount().endsWith(ktoid))
			return null;
		if (line.getEftTrxType() == null)
			return null;
		//if (!line.getEftTrxType().startsWith("079: Sammel"))
		//	return null;

		// Das ist ein Sammler - jetzt gehts los
		
		
		// TODO Auto-generated method stub
		return null;
	}
}
