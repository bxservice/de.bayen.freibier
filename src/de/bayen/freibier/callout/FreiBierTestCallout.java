package de.bayen.freibier.callout;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;

public class FreiBierTestCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab,
			GridField mField, Object value, Object oldValue) {
		System.out.println("FreiBier Callout: "+WindowNo+" - "+mTab+" - "+mField);
		return null;
	}

}
