package de.bayen.freibier.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

import org.compiere.model.MArchive;
import org.compiere.model.MInvoice;
import org.compiere.model.PrintInfo;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class ArchiveHelper {
	
	protected static final CLogger log = CLogger.getCLogger(ArchiveHelper.class);
	
	public static File getPDFArchiveFromInvoice(MInvoice invoice, String fileName, String trxName) {
		File pdfFile = null;
		MArchive[] archives = getArchivesFromInvoice(invoice, fileName, trxName);
		
		if (archives != null && archives.length > 0) {
			pdfFile = new File(fileName);
			try {
				Files.write(pdfFile.toPath(), archives[0].getBinaryData());
			} catch (IOException e) {
				log.severe(e.getMessage());
			}
		}
		
		return pdfFile;
	}
	
	private static MArchive[] getArchivesFromInvoice(MInvoice invoice, String fileName, String trxName) {
		StringBuilder sqlWhere = new StringBuilder(" AND AD_Table_ID=")
				.append(invoice.get_Table_ID())
				.append(" AND Record_ID=").append(invoice.getC_Order_ID())
				.append(" AND Name=").append(DB.TO_STRING(fileName));
		
		return MArchive.get(Env.getCtx(), sqlWhere.toString(), trxName);
	}
	
	public static void archivePDFPrintout(MInvoice invoice, File pdfFile, String trxName) {
		PrintInfo printInfo = new PrintInfo(pdfFile.getName(), invoice.get_Table_ID(), invoice.get_ID(), invoice.getC_BPartner_ID());
		byte[] data = getFileByteData(pdfFile);
		MArchive archive = new MArchive(Env.getCtx(), printInfo, trxName);
		archive.setBinaryData(data);
		archive.save();
	}
	
	public static File getInvoicePrintoutFromArchive(MInvoice invoice, String trxName) {
		return getPDFArchiveFromInvoice(invoice, PrintoutHelper.getFileName(invoice), trxName);
	}
	
	/** 
	 * convert File data into Byte Data
	 * @param tempFile
	 * @return file in ByteData 
	 */
	public static byte[] getFileByteData(File tempFile) {
		try {
			return Files.readAllBytes(tempFile.toPath());
		} catch (IOException ioe) {
			log.log(Level.SEVERE, "Exception while reading file " + ioe);
			return null;
		}
	} 

}
