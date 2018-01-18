package de.bayen.freibier.model;

import java.util.List;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.adempiere.model.GridTabWrapper;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.DocumentLink;
import org.adempiere.webui.component.Messagebox;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.I_C_Order;
import org.compiere.model.MOrder;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import de.bayen.freibier.ui.BAYMessageBox;

public class CalloutOpenSalesOrder implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
		
		if (value == null)
			return null;
		
		I_C_Order order = GridTabWrapper.create(mTab, I_C_Order.class);
		
		List<MOrder> openOrders = getBPartnerOpenOrders(order);
		
		if (openOrders != null && !openOrders.isEmpty()) {
			BAYMessageBox box = new BAYMessageBox();
			box.setParentWindow(ADWindow.get(WindowNo));
			box.addLabelComponent("Der Kunde hat folgende Lieferscheine offen: ");

			for (MOrder openOrder : openOrders) {
				DocumentLink recordLink = new DocumentLink(openOrder.getDocumentNo(), 
						MOrder.Table_ID, openOrder.getC_Order_ID(), new EventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								if (event.getTarget() instanceof DocumentLink) {
									DocumentLink documentLink = (DocumentLink)event.getTarget();
									if (documentLink.getAdTableId() > 0 && documentLink.getAdTableId() > 0)
										AEnv.zoom(documentLink.getAdTableId(), documentLink.getRecordId());
								}
								if (openOrders.size() == 1) {
									box.detach();
								}
							}
					
				});
				box.addLabelComponent(recordLink);
			}
		
			box.show("", Messagebox.OK, Messagebox.EXCLAMATION);
		}

		return null;
	}
	
	private List<MOrder> getBPartnerOpenOrders(I_C_Order order) {
		
		String whereClause = "C_BPartner_ID=? AND C_DocTypeTarget_ID=? AND C_Order_ID!=? AND "
				+ "(DocStatus IN (?,?,?) OR (DocStatus=? AND IsPrinted='N'))";
		
		List <MOrder> list = new Query(Env.getCtx(), MOrder.Table_Name, whereClause, null)
		.setParameters(new Object[]{order.getC_BPartner_ID(), 
									order.getC_DocTypeTarget_ID(), 
									order.getC_Order_ID(), 
									MOrder.DOCSTATUS_Drafted,
									MOrder.DOCSTATUS_Invalid,
									MOrder.DOCSTATUS_NotApproved,
									MOrder.DOCSTATUS_InProgress})
		.setOnlyActiveRecords(true)
		.setClient_ID(true)
		.list();

		return list;
		
	}

}
