package de.bayen.freibier.process;

import org.compiere.process.SvrProcess;

import de.bayen.freibier.model.MBAYInterestCalculation;

public class BAYInterestCalculationRefreshProcess extends SvrProcess {

	@Override
	protected void prepare() {
	}

	@Override
	protected String doIt() throws Exception {
		MBAYInterestCalculation record = new MBAYInterestCalculation(getCtx(), getRecord_ID(), get_TrxName());
		record.recalculateEverything();
		record.saveEx(get_TrxName());
		return null;
	}

}
