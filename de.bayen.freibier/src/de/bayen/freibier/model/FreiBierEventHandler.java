package de.bayen.freibier.model;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.adempiere.base.event.LoginEventData;
import org.compiere.model.MDocType;
import org.compiere.model.MOrder;
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUser;
import org.compiere.process.DocActionEventData;
import org.compiere.process.DocumentEngine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.osgi.service.event.Event;

public class FreiBierEventHandler extends AbstractEventHandler {

	@Override
	protected void doHandleEvent(Event event) {
		String type = event.getTopic();

		if (IEventTopics.AFTER_LOGIN.equals(type)) {
			LoginEventData eventData = getEventData(event);
			if (eventData.getAD_Client_ID() == 1000000) {
				MRole role = MRole.get(Env.getCtx(), eventData.getAD_Role_ID());
				if (eventData.getAD_Org_ID() == 0) {
					if (!role.getName().toLowerCase().contains("admin") || role.isManual()) {
						addErrorMessage(event, "Keine Organisation ausgewählt");
					}
				}
				if (role.getName().toLowerCase().contains("admin")
					|| (role.getDescription() != null && role.getDescription().toLowerCase().contains("admin"))) {
					int userId = Env.getContextAsInt(Env.getCtx(), Env.AD_USER_ID);
					MUser user = MUser.get(userId);
					if (!Env.getCtx().containsKey("#AD_Org_ID_ChangeRole")
						&& ! user.isSupportUser()) {
						addErrorMessage(event, "You cannot login to admin roles");
					}
				}
			}
		}

		if (IEventTopics.DOCACTION.equals(type)) {
			DocActionEventData eventData = getEventData(event);
			/*
			 * gforge [#2814] allow DocActions to be selected in a more flexible way
             * For all sales order documents where the doctype uses the numbering sequence "Lieferscheinnummern".
             * * Drafted documents are not allowed to be completed. But they can be prepared or voided. The default is to prepare.
             * * Prepared documents cannot be completed unless IsPrinted='Y'
             * * For prepared but unprinted documents the default is prepare, after printing the default is complete.
			 */
			if (eventData.po instanceof MOrder) {
				MOrder order = (MOrder) eventData.po;
				MDocType dt = MDocType.get(order.getCtx(), order.getC_DocTypeTarget_ID());

				String seqLieferschein = MSysConfig.getValue("BAY_LIEFERSCHEIN_SEQUENCE_NAME", "Lieferscheinnummern", order.getAD_Client_ID());
				int seqLieferscheinId = DB.getSQLValue(order.get_TrxName(), "SELECT AD_Sequence_ID FROM AD_Sequence WHERE Name=? AND AD_Client_ID=?", seqLieferschein, order.getAD_Client_ID());
				if (seqLieferscheinId < 0)
					throw new RuntimeException("Could not find sequence for Lieferscheinnummern (Sysconfig BAY_LIEFERSCHEIN_SEQUENCE_NAME)");

				if (   order.isSOTrx()
					&& (dt.getDocNoSequence_ID() == seqLieferscheinId || dt.getDefiniteSequence_ID() == seqLieferscheinId)) {
					// when the order is NOT completed or closed
					String docStatus = order.getDocStatus();
					if (   DocumentEngine.STATUS_Drafted.equals(docStatus)
						|| DocumentEngine.STATUS_Approved.equals(docStatus)
						|| DocumentEngine.STATUS_NotApproved.equals(docStatus)
						|| DocumentEngine.STATUS_Invalid.equals(docStatus)
						|| DocumentEngine.STATUS_InProgress.equals(docStatus)) {
						if (! order.isPrinted()) {
							if (eventData.options.contains(DocumentEngine.ACTION_Complete)) {
								eventData.options.remove(DocumentEngine.ACTION_Complete);
								eventData.indexObj.decrementAndGet();
							}
							eventData.docAction.set(0, DocumentEngine.ACTION_Prepare);
						} else {
							eventData.docAction.set(0, DocumentEngine.ACTION_Complete);
						}
					}
				}
			}
		} else if (getPO(event) instanceof MOrder
				&& (type.equals(IEventTopics.PO_BEFORE_NEW) || type.equals(IEventTopics.PO_BEFORE_CHANGE))) {
			
			if (WarehouseHelper.isUpdateWarehouse()) {
				MOrder order = (MOrder) getPO(event);
				int BAY_Route_ID = order.get_ValueAsInt("BAY_Route_ID");
				int warehouseID = WarehouseHelper.getWarehouseID(BAY_Route_ID);
				order.setM_Warehouse_ID(warehouseID);				
			}
		}

	}

	@Override
	protected void initialize() {
		registerEvent(IEventTopics.AFTER_LOGIN);
		registerTableEvent(IEventTopics.DOCACTION, MOrder.Table_Name);
		
		registerTableEvent(IEventTopics.PO_BEFORE_CHANGE, MOrder.Table_Name);
		registerTableEvent(IEventTopics.PO_BEFORE_NEW, MOrder.Table_Name);
	}
	
}
