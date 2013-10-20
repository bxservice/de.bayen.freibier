package de.bayen.freibier.bank;

import org.compiere.impexp.BankStatementMatchInfo;
import org.compiere.impexp.BankStatementMatcherInterface;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.X_I_BankStatement;

/**
 * Automatic bank statement matcher. This class finds a matching document for a given bank statement line.
 * 
 * @author tbayen
 */
public class AutomaticBankStatementMatcher implements BankStatementMatcherInterface {

	@Override
	public BankStatementMatchInfo findMatch(MBankStatementLine bsl) {
		System.out.println("BankStatementMatcher");
		return null;
	}

	@Override
	public BankStatementMatchInfo findMatch(X_I_BankStatement ibs) {
		System.out.println("BankStatementMatcher");
		return null;
	}

}
