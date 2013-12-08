package de.bayen.freibier;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;
import org.compiere.model.MProduct;

import de.bayen.freibier.model.CalloutProduct;

public class CalloutFactory implements IColumnCalloutFactory {

	@Override
	public IColumnCallout[] getColumnCallouts(String tableName,
			String columnName) {
		List<IColumnCallout> list = new ArrayList<IColumnCallout>();
		//
		if(tableName.equals(MProduct.Table_Name)){
			if(columnName.equals(MProduct.COLUMNNAME_M_Product_Category_ID)){
				list.add(new CalloutProduct());
			}
		}
		return list.toArray(new IColumnCallout[0]);
	}

}
