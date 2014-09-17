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
package de.bayen.bx.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.CLogger;

/**
 * Helper Version of SvrProcess.
 * 
 * This class makes it easier to access parameters in a Process.
 * 
 * @author tbayen
 */
public abstract class AbstractSvrProcess extends SvrProcess {

	protected CLogger log = CLogger.getCLogger(getClass());
	protected int[] tableIDs;
	
	/**
	 * Constructor that sets the list of allowed tables to call this process
	 * from.
	 * 
	 * The parameter gives all tables that are allowed to call this process from
	 * a field. You can call this constructor without a parameter to allow all
	 * tables, e.g. if this process is not to be called from a field but as a
	 * direct process call (e.g. via the menu).
	 * 
	 * @param tableIDs
	 */
	public AbstractSvrProcess(int... tableIDs) {
		this.tableIDs = tableIDs;
	}

	/**
	 * may be overridden to get more parameters. For example RecordParams.class
	 * can be listed here.
	 */
	protected Class<?>[] getParameterInterfaces() {
		return new Class<?>[] {};
	}

	// a proxy that can be used with all the given parameter interfaces
	Object parameterBean = null;

	public Object getParameterBean() {
		return parameterBean;
	}


	Map<String, Object> parameterMap = new HashMap<String, Object>();
	Map<String, Class<?>> parameterTypeMap = new HashMap<String, Class<?>>();

	private class MyInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			String methodName = method.getName();
			if (!methodName.startsWith("get"))
				throw new IllegalArgumentException("method " + methodName
						+ " called.");
			String propertyName = methodName.substring(3);
			if(PO.class.isAssignableFrom(method.getReturnType())){
				BigDecimal value=(BigDecimal) parameterMap.get(propertyName+"_ID");
				if(value==null)
					return null;
				else{
					PO po=MTable.get(getCtx(), propertyName).getPO(value.intValueExact(), get_TrxName());
					return po;
				}
			}else{
				Object value = parameterMap.get(propertyName);
				return value;
			}
		}
	}

	/**
	 * Read the given parameters into the parameter bean. Throws an Exception if
	 * an illegal parameter is set.
	 * 
	 * May be overridden if you have something to prepare. Your overloaded
	 * version should first call super.prepare() to read the parameters.
	 */
	@Override
	protected void prepare() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Class<?>[] interfaces = getParameterInterfaces();
		MyInvocationHandler ihandler = new MyInvocationHandler();
		parameterBean = Proxy.newProxyInstance(cl, interfaces, ihandler);
		for (int i = 0; i < interfaces.length; i++) {
			Method[] methods = interfaces[i].getMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				if (methodName.startsWith("get"))
					parameterTypeMap.put(methodName.substring(3),
							method.getReturnType());
			}
		}
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			Class<?> clazz = parameterTypeMap.get(name);
			if(clazz==null && name.endsWith("_ID")){
				clazz = parameterTypeMap.get(name.substring(0, name.length()-3));
			}
			if (clazz == null)
				throw new AdempiereException("Unknown Parameter: " + name);
			String fieldTo = name + "To";
			if (parameterTypeMap.get(fieldTo) == null)
				fieldTo = null;
			if (Boolean.class == clazz) {
				parameterMap.put(name, para[i].getParameterAsBoolean());
			} else if (Integer.class == clazz) {
				parameterMap.put(name, para[i].getParameterAsInt());
				if (fieldTo != null)
					parameterMap.put(fieldTo, para[i].getParameter_ToAsInt());
			} else if (BigDecimal.class == clazz) {
				parameterMap.put(name, para[i].getParameterAsBigDecimal());
				if (fieldTo != null)
					parameterMap.put(fieldTo, para[i].getParameter_To());
			} else if (String.class == clazz) {
				parameterMap.put(name, para[i].getParameterAsString());
				if (fieldTo != null)
					parameterMap.put(fieldTo, para[i].getParameter_To());
			} else if (Timestamp.class == clazz) {
				parameterMap.put(name, para[i].getParameterAsTimestamp());
				if (fieldTo != null)
					parameterMap.put(fieldTo, para[i].getParameter_To());
			} else if (Object.class.isAssignableFrom(clazz)) {
				parameterMap.put(name, para[i].getParameter());
				if (fieldTo != null)
					parameterMap.put(fieldTo, para[i].getParameter_To());
			}
		}
	}

	/**
	 * You should overload this method for your own business logic. You can call
	 * the supermethod at first to check if we are called from an allowed table.
	 * Allowed tables are set in the constructor.
	 */
	@Override
	protected String doIt() throws Exception {
		int Table_ID = getTable_ID();
		if (log.isLoggable(Level.INFO))
			log.info("Table_ID=" + Table_ID);
		boolean tableOK = false;
		if (tableIDs==null || tableIDs.length == 0)
			tableOK = true;
		else {
			for (int i = 0; i < tableIDs.length; i++) {
				if (tableIDs[i] == Table_ID) {
					tableOK = true;
					break;
				}
			}
		}
		if (!tableOK)
			throw new AdempiereSystemError("process is not for this table");
		return null;
	}
}
