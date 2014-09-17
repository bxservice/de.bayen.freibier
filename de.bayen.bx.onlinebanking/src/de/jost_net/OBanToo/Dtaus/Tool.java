/*
 * $Source: /cvsroot/obantoo/obantoo/src/de/jost_net/OBanToo/Dtaus/Tool.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/06/15 12:27:06 $
 * $Author: jost $
 *
 * Copyright 2006 by Heiner Jostkleigrewe
 * Diese Datei steht unter LGPL - siehe beigefügte lpgl.txt
 */
package de.jost_net.OBanToo.Dtaus;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tool
{
  private static String leer = "                                                                              ";

  public static String space(int anzahl)
  {
    return leer.substring(0, anzahl);
  }

  public static String formatSL(int value)
  {
    DecimalFormat dfSL = new DecimalFormat("0000");
    return dfSL.format(value);
  }

  public static String formatErweiterung(int value)
  {
    DecimalFormat dfErw = new DecimalFormat("00");
    return dfErw.format(value);
  }

  public static String formatKontollAnzahl(int value)
  {
    DecimalFormat dfCtrlAnz = new DecimalFormat("0000000");
    return dfCtrlAnz.format(value);
  }

  public static String formatKontrollSumme(BigInteger value)
  {
    DecimalFormat dfCtrlSum = new DecimalFormat("0000000000000");
    return dfCtrlSum.format(value);
  }

  public static String formatKontroll17(BigInteger value)
  {
    DecimalFormat dfCtrl17 = new DecimalFormat("00000000000000000");
    return dfCtrl17.format(value);
  }

  public static String formatBetrag(BigInteger value)
  {
    BigDecimal bdBetrag = new BigDecimal(value);
    bdBetrag = bdBetrag.divide(new BigDecimal(100));
    DecimalFormat nf = new DecimalFormat("#,###,##0.00 EUR");
    return nf.format(bdBetrag);
  }

  public static String formatQIFBetrag(double value)
  {
    DecimalFormat df = new DecimalFormat("##########0.00");
    return df.format(value).replace(',', '.');
  }

  public static String formatBLZ(long value)
  {
    DecimalFormat dfBLZ = new DecimalFormat("00000000");
    return dfBLZ.format(value);
  }

  public static String formatKonto(long value)
  {
    DecimalFormat dfKonto = new DecimalFormat("0000000000");
    return dfKonto.format(value);
  }

  public static String formatTextschluessel(int value)
  {
    DecimalFormat dfTextschluessel = new DecimalFormat("00000");
    return dfTextschluessel.format(value);
  }

  public static String formatQIFDate(Date value)
  {
    SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yy");
    return sdf.format(value);
  }

  public static Date parseQIFDate(String value) throws java.text.ParseException
  {
    SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yy");
    return sdf.parse(value);
  }
}
/*
 * $Log: Tool.java,v $
 * Revision 1.2  2006/06/15 12:27:06  jost
 * Neue Methoden aufgenommen
 * Revision 1.1 2006/06/05 09:36:11 jost Erweiterungen f. d.
 * DtausDateiWriter
 * 
 */
