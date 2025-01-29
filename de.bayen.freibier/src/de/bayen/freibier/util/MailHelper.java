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

import org.compiere.model.MBPartner;
import org.compiere.model.MClient;
import org.compiere.model.MMailText;
import org.compiere.model.MOrder;
import org.compiere.model.MSysConfig;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.CLogger;
import org.compiere.util.EMail;

public class MailHelper {

	protected static final CLogger log = CLogger.getCLogger(MailHelper.class);
	private static final String INVOICE_MAIL_TEMPLATE_VALUE = "INVOICE_MAIL_TEMPLATE";
	
	public static MMailText getMMailTextRecord(MOrder order) {
		int R_MailText_ID = MSysConfig.getIntValue(INVOICE_MAIL_TEMPLATE_VALUE, 0, order.getAD_Client_ID());
		if (R_MailText_ID <= 0)
			throw new AdempiereUserError("@Error@: No mail template defined");

		MMailText mText = new MMailText(order.getCtx(), R_MailText_ID, order.get_TrxName());
		mText.setBPartner(MBPartner.get(order.getCtx(), order.getBill_BPartner_ID()));
		mText.setPO(order);
		return mText;
	}

	public static EMail getEMail(MMailText mText, String emailAddress, MOrder order) {
		MClient client = MClient.get(order.getCtx());
		String subject = mText.getMailHeader();

		EMail email = client.createEMail(emailAddress, subject, null);
		if (!email.isValid()) 
			throw new AdempiereUserError (" @RequestActionEMailError@ Invalid EMail: " + emailAddress);

		String message = mText.getMailText(true);
		if (mText.isHtml())
			email.setMessageHTML(subject, message);
		else {
			email.setSubject(subject);
			email.setMessageText(message);
		}
		
		return email;
	}
	
	public static boolean sendEmail(EMail email) {
		String msg = email.send();
		boolean success = msg.equals(EMail.SENT_OK);
		if (!success)
			log.severe(msg);

		return success;
	}

}
