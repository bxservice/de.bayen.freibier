/******************************************************************************
 * Copyright (C) 2013 Thomas Bayen                                            *
 * Copyright (C) 2013 Jakob Bayen KG             							  *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package de.bayen.bx.onlinebanking;

import java.math.BigDecimal;
import java.util.List;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.impexp.BankStatementMatchInfo;
import org.compiere.impexp.BankStatementMatcherInterface;
import org.compiere.model.MBPBankAccount;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_C_Payment;
import org.compiere.model.X_I_BankStatement;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Util;

import de.bayen.bx.util.IBANUtil;

/**
 * Automatic bank statement matcher. This class finds a matching document for a
 * given bank statement line. The factory works in a way that you do not write
 * the name of this class into the BankStatementMatcher window but a commandline
 * which begins with a command word.
 * 
 * Commands:
 * 
 * accountno
 * 
 * The classic matcher. It matches by AccountNo of the BPartner. When found it
 * tries to find a matching payment or invoice.
 * 
 * stop <bpartnervalue>
 * 
 * Looks for this bpartner and makes that the calling process does immediately
 * stop looking for other matchers. This bpartner will not be matched by any
 * other matcher that comes after this one (beware of the numerical order of
 * your matchers).
 * 
 * @author tbayen
 */
public class AutomaticBankStatementMatcher implements BankStatementMatcherInterface {

	protected CLogger log = CLogger.getCLogger(getClass());
	static protected CLogger logs = CLogger.getCLogger(AutomaticBankStatementMatcher.class);

	protected String command;
	protected String parameter;

	static public final String COMMAND_STOP = "stop";
	static public final String COMMAND_ACCOUNTNO = "accountno";

	/**
	 * The constructor can be called with a <code>null</code> or "" argument
	 * what means we have the standard "accountno" behaviour. If not we can give
	 * a command string in the format "<command> <parameter>" (with one space
	 * between them).
	 * 
	 * @param parameter
	 */
	public AutomaticBankStatementMatcher(String parameter) {
		int space = parameter == null ? -1 : parameter.indexOf(' ');
		if (space == -1) {
			this.command = COMMAND_ACCOUNTNO;
			this.parameter = null;
		} else {
			this.command = parameter.substring(0, space);
			this.parameter = parameter.substring(space + 1);
		}
	}

	public AutomaticBankStatementMatcher() {
		this(null);
	}

	@Override
	public BankStatementMatchInfo findMatch(MBankStatementLine record) {
		if (COMMAND_ACCOUNTNO.equals(command))
			return matchAccountNo(record);
		if (COMMAND_STOP.equals(command))
			return matchStop(record);
		else
			throw new AdempiereException("??");
	}

	@Override
	public BankStatementMatchInfo findMatch(X_I_BankStatement record) {
		if (COMMAND_ACCOUNTNO.equals(command))
			return matchAccountNo(record);
		if (COMMAND_STOP.equals(command))
			return matchStop(record);
		else
			throw new AdempiereException("??");
	}

	/*
	 * Die doppelten Methoden für I_BankStatement und MBankstatementLine sind
	 * einfach kopiert. Lediglich die Titelzeile mit den Parametern ist
	 * unterschiedlich.
	 */

	private BankStatementMatchInfo matchStop(MBankStatementLine record) {
		int foundBPartner_ID = findC_BPartner_ID(record);
		if (foundBPartner_ID < 1)
			return null;
		MBPartner c_BPartner = new MBPartner(Env.getCtx(), foundBPartner_ID, null);
		if (c_BPartner.getValue().equals(parameter)) {
			/*
			 * I create an anonymous subclass that pretends to match but does
			 * not. So the calling Process will stop looking for other matchers.
			 */
			return new BankStatementMatchInfo() {
				@Override
				public boolean isMatched() {
					return true;
				}
			};
		}
		return null;
	}

	protected BankStatementMatchInfo matchStop(X_I_BankStatement record) {
		int foundBPartner_ID = findC_BPartner_ID(record);
		if (foundBPartner_ID < 1)
			return null;
		MBPartner c_BPartner = new MBPartner(Env.getCtx(), foundBPartner_ID, null);
		if (c_BPartner.getValue().equals(parameter)) {
			/*
			 * I create an anonymous subclass that pretends to match but does
			 * not. So the calling Process will stop looking for other matchers.
			 */
			return new BankStatementMatchInfo() {
				@Override
				public boolean isMatched() {
					return true;
				}
			};
		}
		return null;
	}

	/**
	 * package visible
	 * 
	 * @param record
	 * @return
	 */
	static BankStatementMatchInfo matchAccountNo(X_I_BankStatement record) {
		BankStatementMatchInfo2 info = new BankStatementMatchInfo2();

		int foundBPartner_ID = findC_BPartner_ID(record);
		if (foundBPartner_ID < 1)
			return null;
		info.setC_BPartner_ID(foundBPartner_ID);

		// can we find an payment for this BPartner?
		X_C_Payment foundPayment = (X_C_Payment) record.getC_Payment();
		BigDecimal stmtAmt = record.getStmtAmt();
		return matchAccountNo(info, foundBPartner_ID, foundPayment, stmtAmt);
	}

	static BankStatementMatchInfo matchAccountNo(MBankStatementLine record) {
		BankStatementMatchInfo2 info = new BankStatementMatchInfo2();

		int foundBPartner_ID = findC_BPartner_ID(record);
		if (foundBPartner_ID < 1)
			return null;
		info.setC_BPartner_ID(foundBPartner_ID);

		// can we find an payment for this BPartner?
		X_C_Payment foundPayment = (X_C_Payment) record.getC_Payment();
		BigDecimal stmtAmt = record.getStmtAmt();
		return matchAccountNo(info, foundBPartner_ID, foundPayment, stmtAmt);
	}

	private static BankStatementMatchInfo matchAccountNo(BankStatementMatchInfo2 info, int foundBPartner_ID,
			X_C_Payment foundPayment, BigDecimal stmtAmt) {
		if (foundPayment == null || foundPayment.get_ID() == 0) {
			StringBuilder whereClause = new StringBuilder();
			whereClause.append("C_BPartner_ID = ?");
			whereClause.append(" AND IsReconciled = 'N'");
			whereClause.append(" AND DocStatus = 'CO'");
			whereClause.append(" AND PayAmt * (CASE WHEN C_DocType.isSOTrx = 'N' THEN -1 ELSE 1 END) = ? ");
			// whereClause.append(" AND (DateTrx = ? OR DateAcct = ?)");
			// @formatter:off
			Query paymentQuery = new Query(Env.getCtx(), MPayment.Table_Name, whereClause.toString(), null)
				.addJoinClause(" LEFT JOIN C_DocType USING(C_DocType_ID) ")
				.setParameters(
					foundBPartner_ID,
					stmtAmt
					//, record.getEftValutaDate(), record.getEftStatementLineDate()
				);
			// @formatter:on
			List<PO> payments = paymentQuery.list();
			if (payments.size() == 1) {
				foundPayment = (X_C_Payment) payments.get(0);
				info.setC_Payment_ID(foundPayment.get_ID());
			} else
				foundPayment = null;
		}

		// what about the invoice? Does the payment know?
		int foundInvoice_ID = -1;
		if (foundPayment != null) {
			foundInvoice_ID = foundPayment.getC_Invoice_ID();
		}

		// can we find an invoice for this BPartner?
		if (foundInvoice_ID < 1) {
			StringBuilder whereClause = new StringBuilder();
			whereClause.append("C_BPartner_ID = ?");
			whereClause.append(" AND IsPaid = 'N'");
			whereClause.append(" AND DocStatus = 'CO'");
			whereClause.append(" AND GrandTotal * (CASE WHEN C_DocType.isSOTrx = 'N' THEN -1 ELSE 1 END) = ? ");
			whereClause.append(" AND IsInDispute = 'N'");
			// @formatter:off
			List<PO> invoices = new Query(Env.getCtx(), MInvoice.Table_Name, whereClause.toString(), null)
				.addJoinClause(" LEFT JOIN C_DocType USING(C_DocType_ID) ")
				.setParameters(
						info.getC_BPartner_ID(),
						stmtAmt
				)
				.setOrderBy("DateInvoiced")  // the oldest first
				.list();
			// @formatter:on
			if (invoices.size() > 0) {
				info.setC_Invoice_ID(invoices.get(0).get_ID());
			}
		}
		return info;
	}

	/**
	 * package visible
	 * 
	 * @param record
	 * @return
	 */
	static int findC_BPartner_ID(X_I_BankStatement record) {
		int foundBPartner_ID = record.getC_BPartner_ID();
		if (foundBPartner_ID > 0)
			return foundBPartner_ID;
		// "EftPayeeAccount" can be either an IBAN or
		// routingNo+"/"+accountNo
		String account = record.getEftPayeeAccount();
		int bpID = findC_BPartner_ID(account);
		return bpID > 0 ? bpID : foundBPartner_ID;
	}

	static int findC_BPartner_ID(MBankStatementLine record) {
		int foundBPartner_ID = record.getC_BPartner_ID();
		if (foundBPartner_ID > 0)
			return foundBPartner_ID;
		// "EftPayeeAccount" can be either an IBAN or
		// routingNo+"/"+accountNo
		String account = record.getEftPayeeAccount();
		int bpID = findC_BPartner_ID(account);
		return bpID > 0 ? bpID : foundBPartner_ID;
	}

	/**
	 * search a bpartner for a given account number. The account number can have
	 * the format "routingno/accountno" or it can be an IBAN.
	 * 
	 * @param account
	 * @return
	 */
	private static int findC_BPartner_ID(String account) {
		if (Util.isEmpty(account, true))
			return 0; // no data to keep
		// parse the account info we got from the statement importer
		String routingNo, accountNo, iban;
		int index = account.indexOf('/');
		if (index != -1) {
			routingNo = account.substring(0, index);
			accountNo = account.substring(index + 1, account.length());
			iban = null;
		} else {
			// must be IBAN
			routingNo = IBANUtil.extractRoutingNoFromIBAN(account);
			accountNo = null;
			iban = account; // we use IBAN as account no
		}
		// @formatter:off
		List<PO> list = new Query(Env.getCtx(),MBPBankAccount.Table_Name, 
			new StringBuilder()
			.append("( REPLACE(IBAN,' ','') = ?")
			.append(" OR (COALESCE(C_BP_BankAccount.RoutingNo, C_Bank.RoutingNo) = ? ")
			.append("     AND TRIM(LEADING '0' FROM AccountNo) = TRIM(LEADING '0' FROM ?)) ")
			.append(") AND IsMatcher = 'Y'")
			.toString()
		, null)
		.addJoinClause(" LEFT JOIN C_Bank USING(C_Bank_ID) ")
		.setClient_ID()
		.setOnlyActiveRecords(true)
		.setParameters(iban, routingNo, accountNo)
		.list();

		// @formatter:on
		MBPBankAccount bankAccount = null;
		if (list.size() == 1)
			bankAccount = (MBPBankAccount) list.get(0);
		else if (list.size() > 1) {
			logs.severe("Double account matching: " + account);
			return 0;
		}
		if (bankAccount != null)
			return bankAccount.getC_BPartner_ID();
		return  0;
	}

}
