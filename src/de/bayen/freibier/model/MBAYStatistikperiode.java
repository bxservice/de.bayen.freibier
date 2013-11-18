package de.bayen.freibier.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Statistikperiode.
 * 
 * Diese Klasse habe ich erstellt, um die Stringkonstanten für die
 * Statistiktypen fest anzulegen.
 * 
 * @author tbayen
 * 
 */
public class MBAYStatistikperiode extends X_BAY_Statistikperiode {

	private static final long serialVersionUID = 1L;

	public static final String STATISTIKTYP_JAEHRLICHDURST = "JD";
	public static final String STATISTIKTYP_JAEHRLICHEINKAUFDURST = "JED";
	public static final String STATISTIKTYP_MONATLICHDURST = "MD";
	public static final String STATISTIKTYP_MONATLICHEINKAUFDURST = "MED";
	public static final String STATISTIKTYP_NGVABSATZ = "NGVA";
	public static final String STATISTIKTYP_NGVBEZUG = "NGVB";

	public MBAYStatistikperiode(Properties ctx, int BAY_Statistikperiode_ID,
			String trxName) {
		super(ctx, BAY_Statistikperiode_ID, trxName);
	}

	public MBAYStatistikperiode(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

}
