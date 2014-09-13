/******************************************************************************
 * Copyright (C) 2014 Thomas Bayen                                            *
 * Copyright (C) 2014 Jakob Bayen KG             							  *
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
package de.bayen.bx.onlinebanking.model;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.adempiere.model.GridTabWrapper;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.I_C_BP_BankAccount;
import org.compiere.model.MBPBankAccount;
import org.compiere.model.MBank;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.Util;

import de.bayen.bx.util.IBANUtil;

public class CalloutIBAN implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
		String iban = (String) mField.getValue();
		if(Util.isEmpty(iban,true))
			return "";  // No IBAN is also ok. That case has to be checked somewhere else.
		String error = IBANUtil.checkIBAN(iban);
		if (error.length() > 0)
			return error;
		iban = IBANUtil.formatIBAN(iban);
		mTab.setValue(mField, iban);
		
		// some special rules for the Business Partner Bank Account Table
		if (MBPBankAccount.Table_Name.equals(mTab.getTableName())) {
			// we are in the BPartner Account tab
			I_C_BP_BankAccount bpBankAccount = GridTabWrapper.create(mTab, I_C_BP_BankAccount.class);
			initializeMBPBankAccountFromIBAN(ctx, bpBankAccount, iban);
		}
		return "";
	}

	/**
	 * This method is not used for the callout but it provides the same
	 * functionality if you have a PO object and want to enter a IBAN value
	 * programatically.
	 * 
	 * @param ctx
	 * @param bpBankAccount
	 * @param iban
	 * @return error message or ""
	 */
	public static String setIBAN(Properties ctx, PO bpBankAccount, String iban){
		if(Util.isEmpty(iban,true))
			return "";  // No IBAN is also ok. That case has to be checked somewhere else.
		String error = IBANUtil.checkIBAN(iban);
		if (error.length() > 0)
			return error;
		iban = IBANUtil.formatIBAN(iban);
		bpBankAccount.set_ValueOfColumn(MBPBankAccountHelper.COLUMNNAME_IBAN, iban);
		
		// some special rules for the Business Partner Bank Account Table
		if (MBPBankAccount.Table_Name.equals(bpBankAccount.get_TableName())) {
			// we are in the BPartner Account tab
			initializeMBPBankAccountFromIBAN(ctx, (I_C_BP_BankAccount) bpBankAccount, iban);
		}
		return "";
	}

	public static void initializeMBPBankAccountFromIBAN(Properties ctx,
			I_C_BP_BankAccount bpBankAccount, String iban) {
		// if we have a routingNo we can fill routingNo and bank
		String routingNo = IBANUtil.extractRoutingNoFromIBAN(iban);
		if (routingNo != null) {
			if (bpBankAccount.getRoutingNo() == null) {
				bpBankAccount.setRoutingNo(routingNo);
			}
			if (bpBankAccount.getC_Bank_ID() < 1) {
				String whereClause = MBank.COLUMNNAME_RoutingNo + "=?";
				Query query = new Query(ctx, MBank.Table_Name, whereClause,
						null);
				query.setParameters(routingNo);
				int bankID = query.firstIdOnly();
				if (bankID > 0)
					bpBankAccount.setC_Bank_ID(bankID);
			}
		}
		// if we have accountNo we can fill that
		String accountNo = IBANUtil.extractAccountNoFromIBAN(iban);
		if (accountNo != null) {
			if (bpBankAccount.getAccountNo() == null)
				bpBankAccount.setAccountNo(accountNo);
		}
	}

}
