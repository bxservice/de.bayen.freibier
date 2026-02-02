/**********************************************************************
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
 **********************************************************************/
package de.bayen.freibier.model;

import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;

public class InvoiceHelper {
	
	public static void copyMailValuesFromOrder(MInvoice invoice) {
		
		if (invoice.getC_Order_ID() > 0 && invoice.isSOTrx()) {
			MOrder order = new MOrder(invoice.getCtx(), invoice.getC_Order_ID(), invoice.get_TrxName());
			if (order.get_ValueAsBoolean("BAY_IsEDI"))
				invoice.set_ValueOfColumn("BAY_SendMail", false);
			else
				invoice.set_ValueOfColumn("BAY_SendMail", order.get_ValueAsBoolean("BAY_SendMail"));
		}
	}

}
