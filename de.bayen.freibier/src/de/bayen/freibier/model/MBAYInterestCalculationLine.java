package de.bayen.freibier.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MBAYInterestCalculationLine extends X_BAY_InterestCalculationLine {

	private static final long serialVersionUID = 1L;

	public MBAYInterestCalculationLine(Properties ctx,
			int BAY_InterestCalculationLine_ID, String trxName) {
		super(ctx, BAY_InterestCalculationLine_ID, trxName);
	}

	public MBAYInterestCalculationLine(Properties ctx, ResultSet rs,
			String trxName) {
		super(ctx, rs, trxName);
	}
}
