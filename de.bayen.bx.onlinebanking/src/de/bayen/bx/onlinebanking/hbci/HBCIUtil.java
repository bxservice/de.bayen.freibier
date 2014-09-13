package de.bayen.bx.onlinebanking.hbci;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MBankStatementLoader;
import org.compiere.util.Util;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;

/**
 * Helper Class to work with HBCI4Java. This class allows to create easy a
 * so-called "HBCI-Passport".
 * 
 * @author tbayen
 */
// TODO besser dokumentieren, was ein Passport ist und ob ich dazu eine Datei brauche
public class HBCIUtil {

	protected final static String PASSPORT_TYPE_PINTAN = "PinTan";
	protected final static String PASSPORT_TYPE_RDHNEW = "RDHNew";
	protected final static String PASSPORT_TYPE_DEFINED_BY_PROPERTIESFILE = "properties";

	public static HBCIPassport createPassport(MBankStatementLoader loader, HBCICallback callback) {
		
		String passportFile = loader.getFileName();
		// default ist hier PinTan. Das dürfte die wenigste Verwirrung
		// hervorrufen.
		String extension = "PinTan";
		int dotIndex = passportFile==null ? -1 : passportFile.lastIndexOf('.');
		if (dotIndex > -1) {
			extension = passportFile.substring(dotIndex + 1);
			if (HBCIUtil.PASSPORT_TYPE_PINTAN.toUpperCase().equals(extension.toUpperCase())) {
				extension = HBCIUtil.PASSPORT_TYPE_PINTAN;
			} else if (HBCIUtil.PASSPORT_TYPE_DEFINED_BY_PROPERTIESFILE.toUpperCase().equals(extension.toUpperCase())) {
				extension = HBCIUtil.PASSPORT_TYPE_DEFINED_BY_PROPERTIESFILE;
			} else if (HBCIUtil.PASSPORT_TYPE_RDHNEW.toUpperCase().equals(extension.toUpperCase())) {
				extension = HBCIUtil.PASSPORT_TYPE_RDHNEW;
			} else if ("rdh".toUpperCase().equals(extension.toUpperCase())) {
				/*
				 * Eine Dateiendung "rdh" wird auch so interpretiert, das das
				 * eine Schlüsseldisketten-Datei vom Typ "RDHNew" ist. Das mache
				 * ich einfach mal so. Den alten rdh-Typ benutzt eh kein Mensch,
				 * der das mit iDempiere neu einrichtet.
				 */
				extension = HBCIUtil.PASSPORT_TYPE_RDHNEW;
			}
		}
		// hier ein else, das PinTan als Standard setzt? Oder eine Fehlermeldung?
		String pType=extension;
		
		Properties hbciProps = new Properties();
		/**
		 * RDHNew bietet sogenannte "Schlüsseldisketten", mit denen es dann
		 * möglich ist, ohne weitere PIN auf das Konto zuzugreifen.
		 * 
		 * Hier kann auch z.B. "PinTan" verwendet werden.
		 */
		hbciProps.setProperty("log.loglevel.default", "2");  // 2 ist gut, 4/5 ist gelaber
		hbciProps.setProperty("log.filter", "2");  // 2 ist gut, 0 ist alles
		
		if (PASSPORT_TYPE_RDHNEW.equals(pType)) {
			hbciProps.setProperty("client.passport.default", pType);
			hbciProps.setProperty("client.passport.RDHNew.filename", loader.getFileName());
			hbciProps.setProperty("client.passport.RDHNew.init", "1");
		} else if (PASSPORT_TYPE_PINTAN.equals(pType)) {
			// TODO dieser Kommentar kann weg, wenn es läuft
			/*
			 * Das ist nicht getestes und läuft wohl so auch nicht. Ich empfehle
			 * einstweilen die Konfiguration durch eine Properties-Datei. Später
			 * sollte man entscheiden, ob das hier komplett weg kann oder so
			 * fertiggemacht wird.
			 */
			hbciProps.setProperty("client.passport.default", pType);
			String fileName=loader.getFileName();
			if(fileName==null){
				try {
					File file=File.createTempFile("HBCI_", ".PinTan");
					fileName=file.getAbsolutePath();
					file.delete();
					file.deleteOnExit();
				} catch (IOException e) {
					throw new AdempiereException(e);
				}
			}
			hbciProps.setProperty("client.passport.PinTan.filename", fileName);
			hbciProps.setProperty("client.passport.PinTan.init", "1");
		} else if (PASSPORT_TYPE_DEFINED_BY_PROPERTIESFILE.equals(pType)) {
			try {
				InputStream istrm = new FileInputStream(loader.getFileName());
				hbciProps.load(istrm);
				istrm.close();
				String fileName = hbciProps.getProperty("client.passport.PinTan.filename");
				if (fileName != null) {
					new File(fileName).delete();
				} else {
					File file = File.createTempFile("hbci-", ".passport");
					file.delete();
					hbciProps.setProperty("client.passport.PinTan.filename", file.getCanonicalPath());
				}
			} catch (IOException ex) {
				throw new AdempiereException(ex);
			}
		}

		HBCIUtils.init(hbciProps, callback);
		// TODO irgendwo done() machen
		HBCIPassport passport = AbstractHBCIPassport.getInstance();

		String pVersion = loader.getDateFormat();
		if (Util.isEmpty(pVersion)) {
			pVersion = passport.getHBCIVersion();
			pVersion = ((pVersion.length() != 0) ? pVersion : "300");
		}
		HBCIUtils.setParam("client.passport.hbciversion.default", pVersion);
		return passport;
	}

}
