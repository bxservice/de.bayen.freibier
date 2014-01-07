package de.bayen.freibier.model;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.adempiere.model.GridTabWrapper;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.I_C_InvoiceBatchLine;

public class CalloutInvoiceBatchLine extends CalloutEngine implements
		IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab,
			GridField mField, Object value, Object oldValue) {
		if (value != null) {
			I_BAY_Contract contract = new X_BAY_Contract(ctx, (Integer) value,
					null);
			I_C_InvoiceBatchLine record = GridTabWrapper.create(mTab,
					I_C_InvoiceBatchLine.class);
			//
			if (contract.getC_BPartner_ID() > 0) {
				record.setC_BPartner_ID(contract.getC_BPartner_ID());
			}
		}
		return null;
	}
}
