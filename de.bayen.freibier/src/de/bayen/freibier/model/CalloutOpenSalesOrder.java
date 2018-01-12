package de.bayen.freibier.model;

import java.util.List;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.adempiere.model.GridTabWrapper;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.I_C_Order;
import org.compiere.model.MOrder;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.zkoss.zul.Messagebox;

public class CalloutOpenSalesOrder implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
		I_C_Order order = GridTabWrapper.create(mTab, I_C_Order.class);
		
		List<MOrder> openOrders = getBPartnerOpenOrders(order);
		
		if (openOrders != null && !openOrders.isEmpty()) {
			StringBuilder ordersDocNo = new StringBuilder();
			
			for (MOrder openOrder : openOrders)
				ordersDocNo.append(openOrder.getDocumentNo() + " ");

			Messagebox.show("Der Kunde hat folgende Lieferscheine offen: " + ordersDocNo.toString(), 
					"", Messagebox.OK, Messagebox.EXCLAMATION);
		}

		return null;
	}
	
	private List<MOrder> getBPartnerOpenOrders(I_C_Order order) {
		
		String whereClause = "C_BPartner_ID=? AND C_DocTypeTarget_ID=? AND "
				+ "(DocStatus=? OR (DocStatus=? AND IsPrinted='N'))";
		
		List <MOrder> list = new Query(Env.getCtx(), MOrder.Table_Name, whereClause, null)
		.setParameters(new Object[]{order.getC_BPartner_ID(), order.getC_DocTypeTarget_ID(), MOrder.DOCSTATUS_Drafted, MOrder.DOCSTATUS_InProgress})
		.setOnlyActiveRecords(true)
		.setClient_ID(true)
		.list();

		return list;
		
	}

}
