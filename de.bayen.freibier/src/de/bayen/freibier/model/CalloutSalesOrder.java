package de.bayen.freibier.model;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.adempiere.model.GridTabWrapper;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.I_C_Order;
import org.compiere.model.MBPartner;
import org.compiere.model.Query;
import org.compiere.model.X_C_BP_Relation;
import org.compiere.util.Env;

public class CalloutSalesOrder extends CalloutEngine implements IColumnCallout {
	
	private static final String DELIVERY_CONSTRAINT_COLUMNNAME = "BAY_DeliveryConstraint_ID";

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
		
		if (value == null)
			return null;

		I_C_Order order = GridTabWrapper.create(mTab, I_C_Order.class);
		MBPartner invoicePartner = getBillBPartner(order.getC_BPartner_ID());
		if (invoicePartner != null) {
			mTab.setValue(I_C_Order.COLUMNNAME_Bill_BPartner_ID, invoicePartner.getC_BPartner_ID());
		}
		
		int deliveryConstraint_ID = getDeliveryConstraint(ctx, order.getC_BPartner_ID());
		if (deliveryConstraint_ID > 0)
			mTab.setValue(DELIVERY_CONSTRAINT_COLUMNNAME, deliveryConstraint_ID);

		return "";
	}
	
	private MBPartner getBillBPartner(int C_BPartner_ID) {
		String whereClause = X_C_BP_Relation.COLUMNNAME_C_BPartner_ID + " = ?";
		X_C_BP_Relation bpRelation = new Query(Env.getCtx(), X_C_BP_Relation.Table_Name, whereClause, null)
				.setParameters(C_BPartner_ID)
				.setOnlyActiveRecords(true)
				.first();
		
		if (bpRelation != null) {
			return MBPartner.get(Env.getCtx(), bpRelation.getC_BPartnerRelation_ID());
		} 
		
		return null;
	}
	
	private int getDeliveryConstraint(Properties ctx, int C_BPartner_ID) {
		MBPartner businessPartner = MBPartner.get(ctx, C_BPartner_ID);
		int deliveryConstraintID = businessPartner.get_ValueAsInt(DELIVERY_CONSTRAINT_COLUMNNAME);
		return deliveryConstraintID;
	}
}
