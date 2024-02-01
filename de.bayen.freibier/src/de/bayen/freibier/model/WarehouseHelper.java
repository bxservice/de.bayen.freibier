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

import org.compiere.util.DB;

public class WarehouseHelper {
	
	final private static String ABHOLMARKT_ROUTE_UU = "cf63f734-1d6c-4cdf-bbf0-4a640648622a"; 
	final private static String ABHOLMARKT_WAREHOUSE_UU = "ed734710-3e60-48c3-82d2-b1afa7b30fa1"; 
	final private static String STANDARD_WAREHOUSE_UU = "01f5367d-0dec-4a8c-b15c-404a3417ab69"; 
	
	public static int getWarehouseID(int BAY_Route_ID) {
		
		String BAY_Route_UU = getBAYRouteUUID(BAY_Route_ID);
		
		String warehouseUUID = ABHOLMARKT_ROUTE_UU.equals(BAY_Route_UU) ? 
				ABHOLMARKT_WAREHOUSE_UU : STANDARD_WAREHOUSE_UU;
		
		String sql = "SELECT M_Warehouse_ID FROM M_Warehouse WHERE M_Warehouse_UU = ?";
		return DB.getSQLValue(null, sql, warehouseUUID);

	}
	
	private static String getBAYRouteUUID(int BAY_Route_ID) {
		String sql = "SELECT BAY_Route_UU FROM BAY_Route WHERE BAY_Route_ID = ?";
		return DB.getSQLValueString(null, sql, BAY_Route_ID);
	}

}
