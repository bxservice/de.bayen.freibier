package de.bayen.freibier.model;

import org.compiere.model.I_M_Product;
import org.compiere.model.MClient;
import org.compiere.model.MProduct;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.X_M_Product;

/**
 * Hier kommen Hilfsmethoden hinein, die ich nicht in Product hineinschreibe, weil ich den
 * Quellcode des Adempiere-Kerns nicht ändern will. 
 * 
 * @author tbayen
 */
public class ProductValidator implements ModelValidator{

	private int m_AD_Client_ID;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		// This line must come before registering the model changes
		//client = null for global validator
		if (client != null) {	
			m_AD_Client_ID = client.getAD_Client_ID();
		}

		engine.addModelChange(X_M_Product.Table_Name, this);
	}

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
		if(po.get_TableName().equals("M_Product") && type==TYPE_BEFORE_CHANGE){
			refreshProduct(po);
		}else if(po.get_TableName().equals("M_Product") && type==TYPE_BEFORE_NEW){
			refreshProduct(po);
		}
		return null;
	}

	private void refreshProduct(PO po) {
		MProduct product=(MProduct)po;
		MBAYTradingUnit tu = getTradingUnit(product);
		if(tu==null){
			if(product.getName()==null)
				product.setName(product.getDescription());
		}else{
			tu.initProduct(product);
		}
	}

	@Override
	public String docValidate(PO po, int timing) {
		return null;
	}

	/**
	 * statische Hilfsmethode; ersetzt den getter in {@link MProduct}.
	 * 
	 * @param product
	 * @return
	 */
	public static MBAYTradingUnit getTradingUnit(I_M_Product product){
		Integer id=(Integer) ((PO)product).get_Value("BAY_TradingUnit_ID");
		if(id==null || id==0)
			return null;
		return new MBAYTradingUnit(((PO)product).getCtx(), id, ((PO)product).get_TrxName());
	}
}
