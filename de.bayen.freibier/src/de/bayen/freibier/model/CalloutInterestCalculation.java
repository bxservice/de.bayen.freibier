package de.bayen.freibier.model;

import java.sql.Timestamp;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.adempiere.model.GridTabWrapper;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;

public class CalloutInterestCalculation implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab,
			GridField mField, Object value, Object oldValue) {
		I_BAY_InterestCalculation record = GridTabWrapper.create(mTab,
				I_BAY_InterestCalculation.class);

		if (mField.getColumnName().equals(
				MBAYInterestCalculation.COLUMNNAME_DateDoc)) {
			if (oldValue != null && oldValue.equals(record.getDateAcct()))
				record.setDateAcct((Timestamp) value);

		} else if (mField.getColumnName().equals(
				MBAYInterestCalculation.COLUMNNAME_BAY_Contract_ID)) {
			MBAYContract contract = new MBAYContract(ctx,
					record.getBAY_Contract_ID(), null);
			record.setC_BPartner_ID(contract.getC_BPartner_ID());
		}
		
		return "";
	}

}
