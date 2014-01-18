package de.bayen.freibier.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.adempiere.model.GridTabWrapper;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;

public class CalloutInterestCalculationLine implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
		I_BAY_InterestCalculationLine record = GridTabWrapper.create(mTab, I_BAY_InterestCalculationLine.class);

		setDefaultPercentage(ctx, record, mTab);

		if (MBAYInterestCalculationLine.COLUMNNAME_DateTrx.equals(mField.getColumnName())) {
			MBAYInterestCalculationLine.recalculate(ctx, record);
		}
		if (MBAYInterestCalculationLine.COLUMNNAME_Amount.equals(mField.getColumnName())) {
			MBAYInterestCalculationLine.recalculate(ctx, record);
		}
		if (MBAYInterestCalculationLine.COLUMNNAME_InterestPercent.equals(mField.getColumnName())) {
			MBAYInterestCalculationLine.recalculate(ctx, record);
		}
		return null;
	}

	private void setDefaultPercentage(Properties ctx, I_BAY_InterestCalculationLine record, GridTab mTab) {
		if (record.getInterestPercent() != null && !record.getInterestPercent().equals(BigDecimal.ZERO))
			return;
		if (record.getInterestPercent().equals(BigDecimal.ZERO) && mTab.getRowCount() > 1)
			return;
		I_BAY_Contract contract = new X_BAY_Contract(ctx, record.getBAY_InterestCalculation().getBAY_Contract_ID(),
				null);
		if (contract == null || contract.getInterestPercent() == null)
			record.setInterestPercent(BigDecimal.ZERO);
		else
			record.setInterestPercent(contract.getInterestPercent());
	}

}
