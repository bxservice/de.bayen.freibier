package de.bayen.bx.onlinebanking.model;

import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;

@Deprecated
public class OnlineBankingModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(OnlineBankingModelValidator.class);
	private int	m_AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		//client = null for global validator
		if (client != null) {	
			m_AD_Client_ID = client.getAD_Client_ID();
			if (log.isLoggable(Level.INFO)) log.info(client.toString());
		}
		else  {
			if (log.isLoggable(Level.INFO)) log.info("Initializing global validator: "+this.toString());
		}
		
		//	We want to be informed when C_Order is created/changed
		//engine.addModelChange(MOrder.Table_Name, this);
		//	We want to validate Order before preparing 
		//engine.addDocValidate(MOrder.Table_Name, this);
	}	//	initialize

	@Override
	public int getAD_Client_ID() {
		return m_AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception {
		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {
		return null;
	}

}
