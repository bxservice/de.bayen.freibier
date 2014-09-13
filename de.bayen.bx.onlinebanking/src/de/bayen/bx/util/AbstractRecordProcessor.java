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

import java.sql.ResultSet;
import java.sql.Statement;

import org.compiere.model.MTable;
import org.compiere.model.PO;
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
public abstract class AbstractRecordProcessor<T extends PO> extends AbstractSvrProcess {

	public AbstractRecordProcessor(int... tableIDs) {
		super(tableIDs);
	}

	/**
	 * may be overridden to get more parameters. RecordParams.class should be
	 * listed here.
	 */
	protected Class<?>[] getParameterInterfaces() {
		return new Class<?>[] { RecordParams.class };
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

	@SuppressWarnings("unchecked")
	@Override
	protected String doIt() throws Exception {
		super.doIt();
		int Table_ID = getTable_ID();
		int Record_ID = getRecord_ID();
		RecordParams bean = (RecordParams) getParameterBean();

		MTable table = new MTable(getCtx(), Table_ID, null);
		if (bean.getAllRecords()==Boolean.TRUE) {

			String sql = bean.getTabInfoSQL();
			/*
			 * Spalte P_String in AD_PInstance_Para muss verlängert werden, z.B.
			 * auf 5000 Zeichen.
			 */
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
					addLog(lineResult);
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
