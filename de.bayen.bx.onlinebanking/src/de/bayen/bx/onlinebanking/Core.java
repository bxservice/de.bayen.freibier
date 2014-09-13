package de.bayen.bx.onlinebanking;

import java.util.List;
import java.util.logging.Level;

import org.adempiere.base.Service;
import org.compiere.impexp.BankStatementMatcherInterface;
import org.compiere.util.CLogger;

/**
 * Class with static methods to access OSGi services. There is a similar class
 * org.adempiere.base.Core. Here I have methods that could belong to this class
 * but I did not want to change trunk at this moment.
 * 
 * @author tbayen
 */
public class Core {

	private final static CLogger s_log = CLogger.getCLogger(Core.class);

	public static BankStatementMatcherInterface getBankStatementMatcher(String className){
		if (className == null || className.length() == 0) {
			s_log.log(Level.SEVERE, "No class name for OSGi service locator");
			return null;
		}

		BankStatementMatcherInterface myService = null;
		
		List<IBankStatementMatcherFactory> factoryList = 
				Service.locator().list(IBankStatementMatcherFactory.class).getServices();
		if (factoryList != null) {
			for(IBankStatementMatcherFactory factory : factoryList) {
				myService = factory.newBankStatementMatcherInstance(className);
				if (myService != null)
					break;
			}
		}
		
		if (myService == null) {
			s_log.log(Level.SEVERE, "Not found in service/extension registry");
			return null;
		}
		
		return myService;
	}

}
