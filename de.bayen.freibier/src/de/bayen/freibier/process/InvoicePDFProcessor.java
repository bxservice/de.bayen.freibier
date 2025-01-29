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
package de.bayen.freibier.process;

import java.util.logging.Level;

import org.compiere.model.MOrder;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CLogger;

import de.bayen.freibier.util.InvoicePDFGenerator;


/**
 * The InvoicePdfProcessor class is responsible for generating invoice PDFs from the associated Order.
 * It determines whether the generated PDF should be downloaded by the user
 * or sent via email. Additionally, it can modify the PDF to make it
 * ZugFerd compliant if necessary.
 *
 * <p>This class encapsulates the logic for:
 * <ul>
 *   <li>Generating the invoice PDF from the provided data.</li>
 *   <li>Deciding the delivery method (download, email, chat).</li>
 *   <li>Optionally making it ZugFerd compatible</li>
 * </ul>
 *
 * @author Diego Ruiz
 * @version 1.0
 */
public class InvoicePDFProcessor extends SvrProcess {

	protected static final CLogger log = CLogger.getCLogger(InvoicePDFProcessor.class);

	private int p_C_Order_ID = 0;
	private boolean p_sendEmail = false;

	@Override
	protected void prepare() {
		for (ProcessInfoParameter para : getParameter()) {
			String name = para.getParameterName();
			switch (name) {
			case "C_Order_ID": 
				p_C_Order_ID = para.getParameterAsInt();
				break;
			case "EMailPDF": 
				p_sendEmail = para.getParameterAsBoolean();
				break;
			default:
				if (log.isLoggable(Level.INFO))
					log.log(Level.INFO, "Custom Parameter: " + name + "=" + para.getInfo());
				break;
			}
		}	
	}

	@Override
	protected String doIt() throws Exception {

		MOrder order = new MOrder(getCtx(), p_C_Order_ID, get_TrxName());
		if (!order.isComplete()) {
			log.warning("Drucken nicht möglich: Bestellung nicht fertiggestellt");
			return null;
		}

		InvoicePDFGenerator pdfGenerator = new InvoicePDFGenerator(order, get_TrxName());
		pdfGenerator.initializeOrRetrievePDFFile();

		boolean success = true;
		if (isSendMail(order)) {
			success = pdfGenerator.sendInvoicePerMail();
		} else if (processUI != null) {
			processUI.download(pdfGenerator.getPDF());
		}

		return success ? "Invoice Generated" : "@Error@";
	}
	
	private boolean isSendMail(MOrder order) {
		return p_sendEmail;
	}
}
