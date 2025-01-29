/***********************************************************************
 * This file is part of iDempiere ERP Open Source                      *
 * http://www.idempiere.org                                            *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - BX Service                                           *
 **********************************************************************/
package de.bayen.freibier.util;

import java.io.File;
import java.io.IOException;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MBankAccount;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MSysConfig;

import auler.gmbh.zugferdxinvoice.process.ZugFerdGenerator;

public class ZugFerdHelper {

	public static void convertPDFtoZugFerd(MOrder order, File pdfFile) {
		MInvoice invoice = MInvoice.get(order.getC_Invoice_ID());
		ZugFerdGenerator zugFerdGenerator = new ZugFerdGenerator(invoice);
		setBankDetails(zugFerdGenerator, order);
		try {
			zugFerdGenerator.generateZugFerdXML(pdfFile);
		} catch (IOException e) {
			throw new AdempiereException(e.getLocalizedMessage());
		}
	}
	
	public static boolean useClientZugFerd(int AD_Org_ID) {
		return true;
	}

	private static void setBankDetails(ZugFerdGenerator zugFerdGenerator, MOrder order) {
		int bankAccountID = getDefaultBankAccount(order); //TODO:Change for SysConfig??
		if (bankAccountID <= 0) {
			throw new AdempiereException("Default bank account not configured. Contact the administrator");
		}

		MBankAccount bankAccount = MBankAccount.get(bankAccountID);
		zugFerdGenerator.setBank(bankAccount.getC_Bank_ID());
		zugFerdGenerator.setBankAccount(bankAccountID);
		if (!zugFerdGenerator.isValidBankDetail())
			throw new AdempiereException("Invalid default bank account. Contact the administrator");
	}

	private static int getDefaultBankAccount(MOrder order) {
		int bankAccountID = MSysConfig.getIntValue("ZUGFERD_DEFAULTBANKACCOUNT_ID", 0, order.getAD_Client_ID());
		if (bankAccountID <= 0) {
			throw new AdempiereException("Default bank account not configured. Contact theadministrator");
		}
		
		return bankAccountID;
	}
}

