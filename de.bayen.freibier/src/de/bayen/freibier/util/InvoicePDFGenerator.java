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

import org.compiere.model.MMailText;
import org.compiere.model.MOrder;
import org.compiere.model.MUser;
import org.compiere.model.MUserMail;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.EMail;
import org.compiere.util.Env;
import org.compiere.util.Util;

public class InvoicePDFGenerator {

	private MOrder order;
	private File pdfFile;

	private String trxName;

	public InvoicePDFGenerator(MOrder order, String trxName) {
		this.order = order;
		this.trxName = trxName;
	}

	public void initializeOrRetrievePDFFile() {
		pdfFile = ArchiveHelper.getOrderPrintoutFromArchive(order, trxName);

		if (pdfFile == null) {
			generateAndArchivePDF();
		}
	}

	private void generateAndArchivePDF() {
		pdfFile = PrintoutHelper.generatePDFPrintout(order);
		if (ZugFerdHelper.useClientZugFerd(order.getAD_Org_ID()))
			ZugFerdHelper.convertPDFtoZugFerd(order, pdfFile);
		
		ArchiveHelper.archivePDFPrintout(order, pdfFile, trxName);
	}
	
	public boolean sendInvoicePerMail() {
		MMailText mText = MailHelper.getMMailTextRecord(order);

		String emailAddress = getUserToEmailAddress();
		EMail email = MailHelper.getEMail(mText, emailAddress, order);
		email.addAttachment(pdfFile);

		boolean success = MailHelper.sendEmail(email);
		MUserMail um = new MUserMail(mText, Env.getAD_User_ID(Env.getCtx()), email);
		um.saveEx();

		return success;
	}
	
	protected String getUserToEmailAddress() {
		MUser userTo = MUser.get(order.getBill_User_ID());
		String emailAddress = userTo.getEMail();
		if (Util.isEmpty(emailAddress))
			throw new AdempiereUserError (" @RequestActionEMailError@ Bill User has an Invalid EMail: " + emailAddress);

		return emailAddress;
	}

	public File getPDF() {
		return pdfFile;
	}
}
