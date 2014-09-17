/*
 * $Source: /cvsroot/obantoo/obantoo/src/de/jost_net/OBanToo/Tools/Util.java,v $
 * $Revision: 1.3 $
 * $Date: 2007/02/14 14:43:56 $
 * $Author: jost $
 *
 * Copyright 2006 by Heiner Jostkleigrewe
 * Diese Datei steht unter LGPL - siehe beigefügte lpgl.txt
 */
package de.jost_net.OBanToo.Tools;

import java.text.NumberFormat;
import java.util.Locale;

public class Util
{
  public static String toHex(String val)
  {
    String ret = "0x";
    for (int i = 0; i < val.length(); i++)
    {
      ret += toHex(val.charAt(i), 2);
    }
    return ret;
  }

  public static String formatCurrency(double value)
  {
    return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(value);
  }

  public static String toHex(char c, int width)
  {
    int i = (int) c;
    return toHex(i, width);
  }

  public static String toHex(int val, int width)
  {
    String s = Integer.toHexString(val).toUpperCase();
    while (s.length() < width)
    {
      s = "0" + s;
    }
    return s;
  }

}
/*
 * $Log: Util.java,v $
 * Revision 1.3  2007/02/14 14:43:56  jost
 * Neue Methode: formatCurrency(double)
 * Revision 1.2 2006/09/25 18:29:24 jost Sichtbarkeit der
 * Methoden auf public erweitert Revision 1.1 2006/09/25 18:16:21 jost Neu
 * Revision 1.4 2006/06/05 09:35:13 jost
 */
