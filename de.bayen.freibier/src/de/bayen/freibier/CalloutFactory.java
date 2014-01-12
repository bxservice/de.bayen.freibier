package de.bayen.freibier;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;
import org.compiere.model.MInvoiceBatchLine;
import org.compiere.model.MProduct;

import de.bayen.freibier.model.CalloutInterestCalculation;
import de.bayen.freibier.model.CalloutInterestCalculationLine;
import de.bayen.freibier.model.CalloutInvoiceBatchLine;
import de.bayen.freibier.model.CalloutProduct;
import de.bayen.freibier.model.I_BAY_Contract;
import de.bayen.freibier.model.I_BAY_InterestCalculation;
import de.bayen.freibier.model.MBAYInterestCalculation;
import de.bayen.freibier.model.MBAYInterestCalculationLine;

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
		//
		if(tableName.equals(MInvoiceBatchLine.Table_Name)){
			if(columnName.equals(I_BAY_Contract.COLUMNNAME_BAY_Contract_ID)){
				list.add(new CalloutInvoiceBatchLine());
			}
		}
		//
		if(tableName.equals(MBAYInterestCalculation.Table_Name)){
			if(columnName.equals(I_BAY_InterestCalculation.COLUMNNAME_DateDoc)){
				list.add(new CalloutInterestCalculation());
			}
		}
		//
		if(tableName.equals(MBAYInterestCalculationLine.Table_Name)){
			list.add(new CalloutInterestCalculationLine());
		}
		//
		return list.toArray(new IColumnCallout[0]);
	}

}
