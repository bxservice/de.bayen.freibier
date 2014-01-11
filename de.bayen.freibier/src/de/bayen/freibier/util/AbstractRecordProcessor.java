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
package de.bayen.freibier.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Statement;
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
import org.compiere.util.DB;
import org.compiere.util.Util;

/**
 * Helper Version of SvrProcess.
 * 
 * This class makes it easier to access parameters and it makes it easy to write
 * a method that processes just one single record as easy as the whole window
 * full of records. You can choose this with the parameter AllRecords. You may
 * decide to make this with different defaults and/or user editable or not from
 * case to case.
 * 
 * If you want to process all records you have to set a parameter TabInfoSQL.
 * You do this by using "@0|_TabInfo_SQL@" as the default logic (and "Y=N" as
 * the display logic).
 * 
 * @param T
 *            class of the record we deal with
 * @author tbayen
 */
public abstract class AbstractRecordProcessor<T extends PO> extends SvrProcess {

	protected CLogger log = CLogger.getCLogger(getClass());
	private int[] tableIDs;

	public AbstractRecordProcessor(int... tableIDs) {
		// TODO einen Parameter vorschreiben
		if (tableIDs.length == 0)
			throw new AdempiereException(
					"overloaded class does not give a table id");
		this.tableIDs = tableIDs;
	}

	/**
	 * may be overridden to get more parameters. RecordParams.class should be
	 * listed here.
	 */
	protected Class<?>[] getParameterInterfaces() {
		return new Class<?>[] { RecordParams.class };
	}

	// a proxy that can be used with all the given parameter interfaces
	Object parameterBean = null;

	public Object getParameterBean() {
		return parameterBean;
	}

	static public interface RecordParams {
		/**
		 * Process not only the active but all shown records. Can be set by
		 * parameter "AllRecords".
		 */
		public Boolean getAllRecords();

		/**
		 * If you want to use the AllRecords facility you have to set this
		 * parameter. You can do it best with a parameter "TabInfoSQL" of type
		 * String with a standard logic of "@_TabInfo_SQL@" and a display logic
		 * of "N". You should be sure that in table AD_PInstance_Para the column
		 * P_String is long enough (I prefer 5000 characters).
		 */
		public String getTabInfoSQL();
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
			Object value = parameterMap.get(propertyName);
			return value;
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
			} else if (Object.class == clazz) {
				parameterMap.put(name, para[i].getParameter());
				if (fieldTo != null)
					parameterMap.put(fieldTo, para[i].getParameter_To());
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String doIt() throws Exception {
		int Table_ID = getTable_ID();
		int Record_ID = getRecord_ID();
		if (log.isLoggable(Level.INFO))
			log.info("Table_ID=" + Table_ID + ", Record_ID=" + Record_ID);
		RecordParams bean = (RecordParams) getParameterBean();
		boolean tableOK = false;
		for (int i = 0; i < tableIDs.length; i++) {
			if (tableIDs[i] == Table_ID) {
				tableOK = true;
				break;
			}
		}
		if (!tableOK)
			throw new AdempiereSystemError("process is not for this table");
		MTable table = new MTable(getCtx(), Table_ID, null);
		if (bean.getAllRecords()) {
			String sql = bean.getTabInfoSQL();
			// Spalte P_String in AD_PInstance_Para muss verlängert werden,
			// z.B. auf 5000 Zeichen
			Statement stmt = DB.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			String result = "";
			int count = 0;
			while (rs.next()) {
				count++;
				PO record = table.getPO(rs, get_TrxName());
				log.info(count + " - " + record.toString());
				String lineResult = processRecord((T) record);
				if (!Util.isEmpty(lineResult))
					result += lineResult + "\n";
			}
			return result;
		} else {
			PO record = table.getPO(Record_ID, get_TrxName());
			return processRecord((T) record);
		}
	}

	/**
	 * Process one single record of the table.
	 * 
	 * @param record
	 * @return error message
	 */
	abstract protected String processRecord(T record);
}
