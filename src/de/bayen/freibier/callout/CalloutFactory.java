package de.bayen.freibier.callout;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;
import org.compiere.model.MBPartner;

public class CalloutFactory implements IColumnCalloutFactory {

	@Override
	public IColumnCallout[] getColumnCallouts(String tableName,
			String columnName) {
		List<IColumnCallout> list = new ArrayList<IColumnCallout>();
		if (tableName.equals(MBPartner.Table_Name)
				&& columnName.equals(MBPartner.COLUMNNAME_Name)){
			System.out.println("FreiBier-Callout von Factory installiert.");
			list.add(new FreiBierTestCallout());
		}
		return list.toArray(new IColumnCallout[0]);
	}

}
