package de.bayen.bx.util;

import java.math.BigDecimal;

import org.adempiere.exceptions.AdempiereException;

/**
 * static helper functions to deal with Bank data.
 * 
 * @author tbayen
 */
public class IBANUtil {

	/**
	 * check if the string has a guilty IBAN format.
	 * 
	 * @param string
	 * @return
	 */
	static public boolean isIBAN(String iban){
		return checkIBAN(iban).length()==0;
	}
	
	/**
	 * Checks the IBAN number as good as possible (please help improving for
	 * other countries - and please contribute this back!). The result is an
	 * error message string or "" if the IBAN is guilty.
	 * 
	 * @param iban
	 * @return "" for a good iban
	 */
	static public String checkIBAN(String iban) {
		if (iban == null)
			return "IBAN empty";
		iban=iban.replace(" ", "");
		if (iban.length() < 16 || iban.length() > 34)
			return "wrong IBAN length";
		if(iban.startsWith("DE")){
			if(iban.length()!=22)
				return "wrong IBAN length";
			// siehe http://www.iban.de/iban-pruefsumme.html
			String checknumber = iban.substring(4)+"1314"+iban.substring(2, 4);
			BigDecimal modulo97 = new BigDecimal(checknumber).remainder(new BigDecimal(97));
			if(!modulo97.equals(BigDecimal.ONE))
				return "Prüfsumme der IBAN stimmt nicht";
			return "";
		}else{
			// to be implemented for other countries
			if(iban.charAt(0)<'A' || iban.charAt(0)>'Z')
				return "first iban character not an upper alpha";
			if(iban.charAt(1)<'A' || iban.charAt(1)>'Z')
				return "second iban character not an upper alpha";
			for (int i = 2; i < iban.length(); i++) {
				if (iban.charAt(i) < '0' || iban.charAt(i) > '9')
					return "IBAN number has to be numeric";
			}
			return "";
		}
	}

	/**
	 * Formats an IBAN number for better readability. The standard for IBANs is
	 * to add one space every four characters.
	 * 
	 * @param iban
	 * @return
	 */
	public static String formatIBAN(String iban) {
		iban = iban.replace(" ", "");
		int start = ((iban.length() - 1) / 4);
		for (int i = start; i > 0; i--) {
			iban = iban.substring(0, i * 4) + " " + iban.substring(i * 4);
		}
		return iban;
	}

	/**
	 * Gets the Bank routing No from the IBAN.
	 * 
	 * This is not as easy as it sounds because every country may have its own
	 * system to connect Banks and IBANs. For now I implement this for germany
	 * and I hope we will do it better later.
	 * 
	 * @param iban
	 * @return null if we do not know the iban format
	 */
	static public String extractRoutingNoFromIBAN(String iban) {
		if (iban.startsWith("DE")) {
			iban=iban.replace(" ", "");
			/*
			 * In germany the older routing system (until 2013) used a routing
			 * number called "Bankleitzahl". This number is part of the new
			 * IBAN. So we can easy extract it.
			 */
			if (iban.length() != 22)
				throw new AdempiereException("IBAN has wrong format: " + iban);
			return iban.substring(4, 12);
		} else
			//throw new AdempiereException("can not parse IBAN from unknown country: " + iban);
			return null;
	}

	/**
	 * Gets the bank account no from the IBAN.
	 * 
	 * For example in germany it is possible to extract the bank account no in
	 * most cases from the IBAN. This is implemented here. For other countries
	 * it is not (yet) implemented. The extracted number is not the right number
	 * in every case even in germany (z.B. "Unterkonten der deutschen Bank"). So
	 * you should use the result of this method with caution. The best idea is
	 * to use it only for a default value if there is no manually entered
	 * account no.
	 * 
	 * @param iban
	 */
	static public String extractAccountNoFromIBAN(String iban) {
		if (iban.startsWith("DE")) {
			iban=iban.replace(" ", "");
			if (iban.length() != 22)
				throw new AdempiereException("IBAN has wrong format: " + iban);
			String erg = iban.substring(16);
			while(erg.startsWith("0"))
				erg=iban.substring(1);
			return erg;
		} else
			//throw new AdempiereException("can not parse IBAN from unknown country: " + iban);
			return null;
	}

}
