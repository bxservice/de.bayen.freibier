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

import org.compiere.impexp.BankStatementMatcherInterface;

/**
 * This factory class will be provided via an OSGi service description to the
 * iDempiere core.
 * 
 * @author tbayen
 */
public class BankStatementMatcherFactory implements IBankStatementMatcherFactory {

	@Override
	public BankStatementMatcherInterface newBankStatementMatcherInstance(String className) {
		int index = className.indexOf(' ');
		if (index == -1) {
			try {
				Class<?> clazz = getClass().getClassLoader().loadClass(className);
				return (BankStatementMatcherInterface) clazz.newInstance();
			} catch (Exception ex) {}
		} else {
			String clazzName = className.substring(0, index);
			String parameter = className.substring(index + 1);
			try {
				Class<?> clazz = getClass().getClassLoader().loadClass(clazzName);
				return (BankStatementMatcherInterface) clazz.getConstructor(
						String.class).newInstance(parameter);
			} catch (Exception ex) {}
		}
		return null;
	}
}
