package de.bayen.freibier.report;


import org.adempiere.base.IProcessFactory;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.process.ProcessCall;

public class ProcessFactory implements IProcessFactory {

	/**
	 * Bisher habe ich alle meine Prozesse mit einer Extension gemacht. Das ist
	 * aber inzwischen uncool geworden. Außerdem mag ich es auch lieber, meine
	 * Zuordnungen durch debugbaren Java-Code laufen zu lassen anstatt durch die
	 * Magie der OSGi-Extensions.
	 */
	@Override
	public ProcessCall newProcessInstance(String className) {
		if (className == null)
			return null;
		String[] processPackages = { getClass().getPackage().getName() };

		for (String packageName : processPackages) {
			if (className.startsWith(packageName)) {
				ClassLoader cl = getClass().getClassLoader();
				try {
					Class<?> clazz = cl.loadClass(className);
					if (ProcessCall.class.isAssignableFrom(clazz)) {
						return (ProcessCall) clazz.newInstance();
					}
				} catch (Exception e) {
					throw new AdempiereException(e);
				}
			}
		}
		return null;
	}

}
