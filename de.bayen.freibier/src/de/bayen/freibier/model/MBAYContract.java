package de.bayen.freibier.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MBAYContract extends X_BAY_Contract {

	private static final long serialVersionUID = 1L;

	public MBAYContract(Properties ctx, int BAY_Contract_ID, String trxName) {
		super(ctx, BAY_Contract_ID, trxName);
	}

	public MBAYContract(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

}
