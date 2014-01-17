package de.bayen.freibier;

import java.sql.ResultSet;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

import de.bayen.freibier.model.MBAYContract;
import de.bayen.freibier.model.MBAYInterestCalculation;
import de.bayen.freibier.model.MBAYInterestCalculationLine;
import de.bayen.freibier.model.MBAYStatistikperiode;
import de.bayen.freibier.model.MBAYTradingUnit;

public class ModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
		if (MBAYStatistikperiode.Table_Name.equals(tableName))
			return MBAYStatistikperiode.class;
		if (MBAYTradingUnit.Table_Name.equals(tableName))
			return MBAYTradingUnit.class;
		if (MBAYContract.Table_Name.equals(tableName))
			return MBAYContract.class;
		if (MBAYInterestCalculation.Table_Name.equals(tableName))
			return MBAYInterestCalculation.class;
		if (MBAYInterestCalculationLine.Table_Name.equals(tableName))
			return MBAYInterestCalculationLine.class;
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		if (MBAYStatistikperiode.Table_Name.equals(tableName))
			return new MBAYStatistikperiode(Env.getCtx(), Record_ID, trxName);
		if (MBAYTradingUnit.Table_Name.equals(tableName))
			return new MBAYTradingUnit(Env.getCtx(), Record_ID, trxName);
		if (MBAYContract.Table_Name.equals(tableName))
			return new MBAYContract(Env.getCtx(), Record_ID, trxName);
		if (MBAYInterestCalculation.Table_Name.equals(tableName))
			return new MBAYInterestCalculation(Env.getCtx(), Record_ID, trxName);
		if (MBAYInterestCalculationLine.Table_Name.equals(tableName))
			return new MBAYInterestCalculationLine(Env.getCtx(), Record_ID,
					trxName);
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		if (MBAYStatistikperiode.Table_Name.equals(tableName))
			return new MBAYStatistikperiode(Env.getCtx(), rs, trxName);
		if (MBAYTradingUnit.Table_Name.equals(tableName))
			return new MBAYTradingUnit(Env.getCtx(), rs, trxName);
		if (MBAYContract.Table_Name.equals(tableName))
			return new MBAYContract(Env.getCtx(), rs, trxName);
		if (MBAYInterestCalculation.Table_Name.equals(tableName))
			return new MBAYInterestCalculation(Env.getCtx(), rs, trxName);
		if (MBAYInterestCalculationLine.Table_Name.equals(tableName))
			return new MBAYInterestCalculationLine(Env.getCtx(), rs, trxName);
		return null;
	}

}
