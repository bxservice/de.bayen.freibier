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

import org.compiere.impexp.BankStatementMatchInfo;

/**
 * This class is like {@link BankStatementMatchInfo} but more powerful. It is
 * used by the advanced BankStatementMatcher2 interface. This class is derived
 * from the old Interface so the new Matchers can be used with the old process
 * if you need to (but are not as powerful).
 * 
 * @author tbayen
 */
public class BankStatementMatchInfo2 extends BankStatementMatchInfo {

	private int C_Charge_ID = 0;

	@Override
	public boolean isMatched() {
		return super.isMatched() || C_Charge_ID != 0;
	}

	public int getC_Charge_ID() {
		return C_Charge_ID;
	}

	public void setC_Charge_ID(int C_Charge_ID) {
		this.C_Charge_ID = C_Charge_ID;
	}
}
