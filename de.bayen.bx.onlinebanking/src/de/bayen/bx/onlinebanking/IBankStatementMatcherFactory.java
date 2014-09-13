package de.bayen.bx.onlinebanking;

import org.compiere.impexp.BankStatementMatcherInterface;

/**
 * This is an OSGI service interface for BankStatementMatcher.
 * 
 * I tried to implement this without touching trunk. To do it a bit better one could do the following:
 * 
 * * move this interface into org.adempiere.base plugin
 * 
 * * create a access method in org.adempiere.base.Core that finds matcher factories through OSGi.
 * 
 * * extend MBankStatementMatcher to use the OSGi service via Core
 * 
 * * extend BankStatementMatchInfo in trunk to support charges
 * 
 * * extend the matcher Process or copy my process to trunk
 * 
 * @author tbayen
 */
public interface IBankStatementMatcherFactory {

	BankStatementMatcherInterface newBankStatementMatcherInstance(String className);

}
