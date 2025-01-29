package de.bayen.freibier.util;

import java.io.File;

import org.compiere.model.MOrder;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.model.SystemIDs;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ServerProcessCtl;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

public class PrintoutHelper {

	protected static final CLogger log = CLogger.getCLogger(PrintoutHelper.class);

	public static File generatePDFPrintout(MOrder order) {
		MProcess orderPrintout = MProcess.get(Env.getCtx(), SystemIDs.PROCESS_RPT_C_ORDER);
		
		ProcessInfo pi = new ProcessInfo(orderPrintout.getName(), SystemIDs.PROCESS_RPT_C_ORDER);
		pi.setRecord_ID(order.getC_Order_ID());
		pi.setAD_Client_ID(Env.getAD_Client_ID(Env.getCtx()));
		pi.setTable_ID(order.get_Table_ID());
		pi.setPrintPreview(true);
		pi.setIsBatch(true);
		pi.setPDFFileName(getFileName(order));
		pi.setReportType("PDF");

		MPInstance instance = new MPInstance(orderPrintout, order.get_Table_ID(), order.getC_Order_ID(), order.getC_Order_UU());
		instance.saveEx();

		ServerProcessCtl.process(pi, null);

		if (pi.isError()) 
			log.severe(pi.getSummary());
		
		return pi.getPDFReport();
	}
	
	public static String getFileName(MOrder order) {
		return order.getDocumentNo() + ".pdf";
	}
}
