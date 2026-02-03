package de.bayen.freibier.util;

import org.compiere.model.MBPartner;
import org.compiere.model.MInvoice;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportEngine;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Util;

public class PrintoutHelper {

	protected static final CLogger log = CLogger.getCLogger(PrintoutHelper.class);
	
	public static String getFileName(MInvoice invoice) {
		ReportEngine re = ReportEngine.get(invoice.getCtx(), ReportEngine.INVOICE, invoice.getC_Invoice_ID(), invoice.get_TrxName());
		if (re == null)
			return null;
		MPrintFormat format = re.getPrintFormat();
		String fileNamePattern = format.getFileNamePattern();
		
		String fileName = null;
		if (Util.isEmpty(fileNamePattern)) {
			fileName = getDefaultName(invoice);
		} else {
			fileName = Env.parseVariable(fileNamePattern, invoice, invoice.get_TrxName(), true);
		}
		return fileName + ".pdf";
	}
	
	private static String getDefaultName(MInvoice invoice) {
		//Default BPValue_BPName_DocumentNo
		MBPartner bpartner = MBPartner.get(invoice.getCtx(), invoice.getC_BPartner_ID());
		return bpartner.getValue() + "_" + bpartner.getName() + "_" + invoice.getDocumentNo();
	}
}
