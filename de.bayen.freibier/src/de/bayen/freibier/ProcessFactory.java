package de.bayen.freibier;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;

import de.bayen.freibier.process.CreateInterestCalculationProcess;

public class ProcessFactory implements IProcessFactory {

	/**
	 * Bisher habe ich alle meine Prozesse mit einer Extension gemacht. Das ist
	 * aber inzwischen uncool geworden. Außerdem mag ich es auch lieber, meine
	 * Zuordnungen durch debugbaren Java-Code laufen zu lassen anstatt durch die
	 * Magie der OSGi-Extensions. Also habe ich diese Klasse hier schonmal
	 * vorbereitet. Beim nächsten Prozess nehme ich die und wenn alles glatt
	 * läuft, können die vorhandenen Prozeß-Extensions dann umgestellt werden.
	 */
	@Override
	public ProcessCall newProcessInstance(String className) {
		if(CreateInterestCalculationProcess.class.getName().equals(className))
			return new CreateInterestCalculationProcess();
		return null;
	}

}
