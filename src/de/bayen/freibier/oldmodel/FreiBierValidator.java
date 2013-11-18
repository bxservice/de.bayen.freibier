//package de.bayen.freibier.model;
//
//import org.compiere.model.MClient;
//import org.compiere.model.ModelValidationEngine;
//import org.compiere.model.ModelValidator;
//import org.compiere.model.PO;
//
///**
// * Meta-VAlidator, der alle anderen einbindet.
// * 
// * @author tbayen
// */
//public class FreiBierValidator implements ModelValidator {
//
//	private int m_AD_Client_ID;
//
//	/**
//	 * Ich aktiviere alle anderen Validators von hier aus.
//	 */
//	@Override
//	public void initialize(ModelValidationEngine engine, MClient client) {
//		// This line must come before registering the model changes
//		//client = null for global validator
//		if (client != null) {	
//			m_AD_Client_ID = client.getAD_Client_ID();
//		}
//
//		ProductValidator productValidator = new ProductValidator();
//		productValidator.initialize(engine, client);
//	}
//
//	@Override
//	public int getAD_Client_ID() {
//		return m_AD_Client_ID;
//	}
//
//	@Override
//	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
//		return null;
//	}
//
//	@Override
//	public String modelChange(PO po, int type) throws Exception {
//		return null;
//	}
//
//	@Override
//	public String docValidate(PO po, int timing) {
//		return null;
//	}
//}
