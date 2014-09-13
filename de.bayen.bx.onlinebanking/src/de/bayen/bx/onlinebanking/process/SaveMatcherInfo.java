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
package de.bayen.bx.onlinebanking.process;

import java.util.logging.Level;

import org.compiere.model.MBPBankAccount;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_I_BankStatement;
import org.compiere.util.Util;

import de.bayen.bx.onlinebanking.model.CalloutIBAN;
import de.bayen.bx.util.AbstractRecordProcessor;
import de.bayen.bx.util.IBANUtil;

/**
 * This process saves information that can be used by the matcher. To use it you
 * have to set a business partner at a bank statement line and then call this
 * process.
 * 
 * @author tbayen
 */
public class SaveMatcherInfo extends AbstractRecordProcessor<PO> {

	public SaveMatcherInfo() {
		super(X_I_BankStatement.Table_ID, MBankStatementLine.Table_ID, MBankStatement.Table_ID);
	}

	protected String processRecord(PO record) {
		if (record == null)
			return "--";
		if (log.isLoggable(Level.FINE))
			log.fine("" + record);

		if (record instanceof X_I_BankStatement)
			return processRecordX_I_BankStatement((X_I_BankStatement) record);
		if (record instanceof MBankStatementLine)
			return processRecordMBankStatementLine((MBankStatementLine) record);
		if (record instanceof MBankStatement) {
			MBankStatementLine[] lines = ((MBankStatement) record).getLines(false);
			for (MBankStatementLine mBankStatementLine : lines) {
				addLog(processRecordMBankStatementLine(mBankStatementLine));
			}
		}
		return "";
	}

	/*
	 * Ich hatte das zuerst nur einmal für die Importtabelle implementiert und
	 * dann die zweite Methode für die BankStatementLine kopiert.
	 */

	private String processRecordMBankStatementLine(MBankStatementLine record) {
		int C_BPartner_ID = record.getC_BPartner_ID();
		String eftPayee = record.getEftPayee();
		String account = record.getEftPayeeAccount();
		return saveMatcherInfo(C_BPartner_ID, eftPayee, account);
	}

	private String processRecordX_I_BankStatement(X_I_BankStatement record) {
		int C_BPartner_ID = record.getC_BPartner_ID();
		String eftPayee = record.getEftPayee();
		String account = record.getEftPayeeAccount();
		return saveMatcherInfo(C_BPartner_ID, eftPayee, account);
	}

	private String saveMatcherInfo(int C_BPartner_ID, String eftPayee, String account) {
		if (Util.isEmpty(account, true))
			return null; // no data to keep
		account=account.replace(" ", "");
		// parse the account info we got from the statement importer
		String routingNo, accountNo, iban;
		// "EftPAyeeAccount" can be either an IBAN or routingNo+"/"+accountNo
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
		if (C_BPartner_ID != 0) {
			MBPBankAccount[] bankAccounts = MBPBankAccount.getOfBPartner(getCtx(), C_BPartner_ID);
			boolean exists = false;
			for (MBPBankAccount ba : bankAccounts) {
				if (accountNo != null && routingNo != null && accountNo.equals(ba.getAccountNo())
						&& routingNo.equals(ba.getRoutingNo())) {
					exists = true;
					break;
				}
				if (iban != null && iban.equals(ba.get_ValueAsString("IBAN").replace(" ",""))) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				MBPBankAccount newBankAccount = new MBPBankAccount(getCtx(), 0, get_TrxName());
				newBankAccount.setC_BPartner_ID(C_BPartner_ID);
				newBankAccount.setA_Name(eftPayee);
				newBankAccount.setIsACH(true);
				newBankAccount.setRoutingNo(routingNo);
				if (!Util.isEmpty(routingNo)) {
					int bankID = new Query(getCtx(), "C_Bank", "RoutingNo=?", get_TrxName()).setClient_ID()
							.setParameters(routingNo).firstIdOnly();
					if (bankID != -1)
						newBankAccount.setC_Bank_ID(bankID);
				}
				newBankAccount.setAccountNo(accountNo);
				iban = IBANUtil.formatIBAN(iban);
				newBankAccount.set_ValueOfColumn("IBAN", iban);
				CalloutIBAN.initializeMBPBankAccountFromIBAN(getCtx(), newBankAccount, iban);
				newBankAccount.setBankAccountType(MBPBankAccount.BANKACCOUNTTYPE_Checking);
				newBankAccount.setBPBankAcctUse(MBPBankAccount.BPBANKACCTUSE_None);
				newBankAccount.saveEx();
				return "gespeichert: " + eftPayee;
			} else {
				return "existiert: " + eftPayee;
			}
		} else {
			return "kein Geschäftspartner: " + eftPayee;
		}
	}

}
