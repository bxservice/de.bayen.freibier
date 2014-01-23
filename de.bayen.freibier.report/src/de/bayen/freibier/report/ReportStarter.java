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
package de.bayen.freibier.report;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.JRBaseFiller;
import net.sf.jasperreports.engine.fill.JRFiller;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSwapFile;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;

import org.adempiere.base.Service;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.DBException;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MSysConfig;
import org.compiere.model.X_AD_PInstance_Para;
import org.compiere.process.ClientProcess;
import org.compiere.process.ProcessCall;
import org.compiere.process.ProcessInfo;
import org.compiere.report.JRViewerProvider;
import org.compiere.util.CLogger;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Ini;
import org.compiere.util.Language;
import org.compiere.util.Trx;

/**
 * Starter Class to do a report using the JasperReports library. This starter is
 * well integrated into iDempiere and can be called everywhere where you can do
 * a report using the internal iDempiere report generator (using Printformat
 * etc.).
 * 
 * This class is loosely based on work done in the former JasperReports start
 * class "org.compiere.report.ReportStarter" done by many members of the
 * community: rlemeill, Ashley Ramdass, victor.perez@e-evolution.com, Teo Sarca
 * (www.arhipac.ro), Cristina Ghita (www.arhipac.ro)
 */
//TODO reading from http (with md5 checksums)
//TODO reading from database attachments
public class ReportStarter implements ProcessCall, ClientProcess
{
	private static final int DEFAULT_SWAP_MAX_PAGES = 100;
	/** Logger */
	private static CLogger log = CLogger.getCLogger(ReportStarter.class);

	@SuppressWarnings("unused")
	private IProcessUI m_processUI;

    public boolean startProcess(Properties ctx, ProcessInfo pi, Trx trx) {
		String trxName = trx == null ? null : trx.getTrxName();
		// read some later used jasper report configuration values
		boolean directPrint = false;
		String path = null;
		CPreparedStatement pstmt = null;
		ResultSet rs = null;
		{
			String sql = "SELECT pr.JasperReport, pr.IsDirectPrint " + "FROM AD_Process pr, AD_PInstance pi "
					+ "WHERE pr.AD_Process_ID = pi.AD_Process_ID " + " AND pi.AD_PInstance_ID=?";
			try {
				pstmt = DB.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, trxName);
				pstmt.setInt(1, pi.getAD_PInstance_ID());
				rs = pstmt.executeQuery();
				boolean isPrintPreview = pi.isPrintPreview();
				if (rs.next()) {
					path = rs.getString(1);
					if ("Y".equalsIgnoreCase(rs.getString(2)) && !Ini.isPropertyBool(Ini.P_PRINTPREVIEW)
							&& !isPrintPreview)
						directPrint = true;
				} else {
					log.severe("data not found; sql = " + sql);
					return false;
				}
			} catch (SQLException e) {
				throw new DBException(e, sql);
			} finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}

		// create and initialize parameter hash
		HashMap<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> parameter = getProcessParameters(pi.getAD_PInstance_ID(), trxName);
		//Map<String, Object> parameter = getProcessInfoParameters(pi.getParameter());
		// parameters are saved as map and direct
		params.put("IDEMPIERE_PARAMETER", parameter);
		params.putAll(parameter);
        params.put("SUBREPORT_DIR", "");
        if (pi.getRecord_ID() > 0)
        	params.put("RECORD_ID", new Integer(pi.getRecord_ID()));
        params.put("AD_PINSTANCE_ID", new Integer(pi.getAD_PInstance_ID()));
    	params.put("AD_CLIENT_ID", new Integer( Env.getAD_Client_ID(Env.getCtx())));
    	params.put("AD_ROLE_ID", new Integer( Env.getAD_Role_ID(Env.getCtx())));
    	params.put("AD_USER_ID", new Integer( Env.getAD_User_ID(Env.getCtx())));
    	Language currLang = Env.getLanguage(Env.getCtx());
       	params.put("AD_Language", currLang.getAD_Language());       	
       	// The following are just for compatibility:
       	params.put("CURRENT_LANG", currLang.getAD_Language());

    	// load jasper file
		ReportFileResolver fileResolver = new ReportFileResolverClasspath(null);
		fileResolver=new ReportFileResolverFileSystem(fileResolver);
		fileResolver=new ReportFileResolverImage(fileResolver);
		
		File jasperFile = fileResolver.resolveFile(path);
        JasperReport jasperReport;
        try {
			jasperReport = (JasperReport)JRLoader.loadObjectFromFile(jasperFile.getAbsolutePath());
		} catch (JRException e) {
			throw new AdempiereException(e);
		}

        Connection conn = DB.getConnectionRO();
		try {
			// create and initialize a virtualizer
			int maxPages = MSysConfig.getIntValue(MSysConfig.JASPER_SWAP_MAX_PAGES, DEFAULT_SWAP_MAX_PAGES);
			String swapPath = System.getProperty("java.io.tmpdir");
			JRSwapFile swapFile = new JRSwapFile(swapPath, 1024, 1024);
			JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(maxPages, swapFile, true);
			params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

			// create context
			LocalJasperReportsContext jasperContext=new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
			jasperContext.setClassLoader(JasperReport.class.getClassLoader());
			jasperContext.setFileResolver(fileResolver);
			JRPropertiesUtil.getInstance(jasperContext).setProperty("net.sf.jasperreports.awt.ignore.missing.font",
					"true");

			// fill the report and create a print object
			JRBaseFiller filler = JRFiller.createFiller(jasperContext, jasperReport);
			JasperPrint jasperPrint = filler.fill(params, conn);

			// view the report
			JRViewerProvider viewerLauncher = Service.locator().locate(JRViewerProvider.class).getService();
//			List<JRViewerProvider> viewerLaunchers = Service.locator().list(JRViewerProvider.class).getServices();
			viewerLauncher.openViewer(jasperPrint, pi.getTitle());
		} catch (Exception e) {
			throw new AdempiereException("error while creating jasper report", e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

		String errMsg = null;
		{
			int result = (errMsg == null ? 1 : 0);
			String sql = "UPDATE AD_PInstance SET Result=?, ErrorMsg=?" + " WHERE AD_PInstance_ID="
					+ pi.getAD_PInstance_ID();
			DB.executeUpdateEx(sql, new Object[] { result, errMsg }, trxName);
		}
		
		return true;
	}
	
//	private Map<String, Object> getProcessInfoParameters(ProcessInfoParameter[] para) {
//		Map<String, Object> params = new HashMap<String, Object>();
//		if (para == null)
//			return null;
//		for (int i = 0; i < para.length; i++) {
//			params.put(para[i].getParameterName(), para[i].getParameter());
//			if (para[i].getParameter_To() != null) {
//				params.put(para[i].getParameterName() + "To", para[i].getParameter_To());
//				// for compatibility:
//				params.put(para[i].getParameterName() + "1", para[i].getParameter());
//				params.put(para[i].getParameterName() + "2", para[i].getParameter_To());
//			}
//		}
//		return params;
//	}

    /**
     * Load Process Parameters into a map
     * 
     * @param AD_PInstance_ID
     * @param trxName
     */
	private static Map<String, Object> getProcessParameters(int AD_PInstance_ID, String trxName) {
		Map<String, Object> params = new HashMap<String, Object>();
		// @formatter:off
		final String sql = "SELECT "
			+" "+X_AD_PInstance_Para.COLUMNNAME_ParameterName
        	+","+X_AD_PInstance_Para.COLUMNNAME_P_String
            +","+X_AD_PInstance_Para.COLUMNNAME_P_String_To
            +","+X_AD_PInstance_Para.COLUMNNAME_P_Number
            +","+X_AD_PInstance_Para.COLUMNNAME_P_Number_To
            +","+X_AD_PInstance_Para.COLUMNNAME_P_Date
            +","+X_AD_PInstance_Para.COLUMNNAME_P_Date_To
            +","+X_AD_PInstance_Para.COLUMNNAME_Info
            +","+X_AD_PInstance_Para.COLUMNNAME_Info_To
            +" FROM "+X_AD_PInstance_Para.Table_Name
            +" WHERE "+X_AD_PInstance_Para.COLUMNNAME_AD_PInstance_ID+"=?";
		// @formatter:on
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, trxName);
			pstmt.setInt(1, AD_PInstance_ID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString(1);
				Object val = rs.getString(2);
				if (val == null)
					val = rs.getBigDecimal(4);
				if (val == null)
					val = rs.getDate(6);
				Object valTo = rs.getString(3);
				if (valTo == null)
					valTo = rs.getBigDecimal(5);
				if (valTo == null)
					valTo = rs.getDate(7);
				params.put(name, val);
				// info contains e.g. a name if value is an reference id
				params.put(name + "_Info", rs.getString(8));
				if (valTo != null) {
					params.put(name+"To", valTo);
					params.put(name + "_InfoTo", rs.getString(9));
					// only for compatibility:
					params.put(name + "1", val);
					params.put(name + "2", valTo);
					params.put(name + "_Info1", rs.getString(8));
					params.put(name + "_Info2", rs.getString(9));
				}
			}
		} catch (SQLException e) {
			throw new DBException(e, sql);
		} finally {
			DB.close(rs, pstmt);
		}
		return params;
	}
	
	@Override
	public void setProcessUI(IProcessUI processUI) {
		m_processUI = processUI;
	}


	// TODO if (reportPath.startsWith("@#LocalHttpAddr@")) {
	// TODO RESOURCE_PATH for compatibility
    // TODO query anpassen, wenn mehrere Record id gesetzt sind
    // TODO take language from print format
    // TODO take printer name from print format
    // TODO resource bundles
	// TODO direct print
	// TODO export into different types
	// TODO wofür ist onrows da?
}