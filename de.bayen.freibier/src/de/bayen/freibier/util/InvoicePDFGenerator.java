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

import org.compiere.model.MInvoice;
import org.compiere.model.MMailText;
import org.compiere.model.MUser;
import org.compiere.model.MUserMail;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.EMail;
import org.compiere.util.Env;
import org.compiere.util.Util;

public class InvoicePDFGenerator {

	private MInvoice invoice;
	private File pdfFile;

	private String trxName;

	public InvoicePDFGenerator(MInvoice invoice) {
		this.invoice = invoice;
		this.trxName = invoice.get_TrxName();
	}

	public void initializeOrRetrievePDFFile() {
		pdfFile = ArchiveHelper.getInvoicePrintoutFromArchive(invoice, trxName);

		if (pdfFile == null) {
			generateAndArchivePDF();
		}
	}

	private void generateAndArchivePDF() {
		if (ZugFerdHelper.useClientZugFerd(invoice.getAD_Org_ID())) {
			pdfFile = ZugFerdHelper.createZugFerdPDF(invoice);
		}
		
		ArchiveHelper.archivePDFPrintout(invoice, pdfFile, trxName);
	}
	
	public boolean sendInvoicePerMail() {
		MMailText mText = MailHelper.getMMailTextRecord(invoice);

		String emailAddress = getUserToEmailAddress();
		EMail email = MailHelper.getEMail(mText, emailAddress, invoice);
		email.addAttachment(pdfFile);

		boolean success = MailHelper.sendEmail(email);
		MUserMail um = new MUserMail(mText, Env.getAD_User_ID(Env.getCtx()), email);
		um.saveEx();
		
		invoice.set_ValueOfColumn("BAY_EMailSent", success);
		invoice.setIsPrinted(true);
		invoice.saveEx();

		return success;
	}
	
	protected String getUserToEmailAddress() {
		MUser userTo = MUser.get(invoice.getAD_User_ID());
		String emailAddress = userTo.getEMail();
		if (Util.isEmpty(emailAddress))
			throw new AdempiereUserError (" @RequestActionEMailError@ User has an Invalid EMail: " + emailAddress);

		return emailAddress;
	}

	public File getPDF() {
		return pdfFile;
	}
}
