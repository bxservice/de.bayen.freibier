/*
 * $Source: /cvsroot/obantoo/obantoo/src/de/jost_net/OBanToo/Dtaus/Satz.java,v $
 * $Revision: 1.11 $
 * $Date: 2008/07/09 13:01:42 $
 * $Author: jost $
 *
 * Copyright 2006 by Heiner Jostkleigrewe
 * Diese Datei steht unter LGPL - siehe beigefügte lpgl.txt
 */
package de.jost_net.OBanToo.Dtaus;

import de.jost_net.OBanToo.Tools.Util;

public class Satz
{

  public Satz()
  {
    //
  }

  public Satz(String value)
  {
    //
  }

  protected String makeValid(String value)
  {
    value = value.replaceAll("a", "A");
    value = value.replaceAll("b", "B");
    value = value.replaceAll("c", "C");
    value = value.replaceAll("d", "D");
    value = value.replaceAll("e", "E");
    value = value.replaceAll("f", "F");
    value = value.replaceAll("g", "G");
    value = value.replaceAll("h", "H");
    value = value.replaceAll("i", "I");
    value = value.replaceAll("j", "J");
    value = value.replaceAll("k", "K");
    value = value.replaceAll("l", "L");
    value = value.replaceAll("m", "M");
    value = value.replaceAll("n", "N");
    value = value.replaceAll("o", "O");
    value = value.replaceAll("p", "P");
    value = value.replaceAll("q", "Q");
    value = value.replaceAll("r", "R");
    value = value.replaceAll("s", "S");
    value = value.replaceAll("t", "T");
    value = value.replaceAll("u", "U");
    value = value.replaceAll("v", "V");
    value = value.replaceAll("w", "W");
    value = value.replaceAll("x", "X");
    value = value.replaceAll("y", "Y");
    value = value.replaceAll("z", "Z");
    value = value.replaceAll("ä", "Ä");
    value = value.replaceAll("ö", "Ö");
    value = value.replaceAll("ü", "Ü");
    value = value.replaceAll("é", "E");
    value = value.replaceAll("è", "E");
    value = value.replaceAll("É", "E");
    value = value.replaceAll("'", " ");
    return value;
  }

  protected void validCharacters(String value) throws DtausException
  {
    for (int i = 0; i < value.length(); i++)
    {
      char c = value.charAt(i);
      if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == ' '
          || c == '.' || c == ',' || c == '&' || c == '-' || c == '+'
          || c == '*' || c == '%' || c == '/' || c == '$' || c == 'Ä'
          || c == 'Ö' || c == 'Ü' || c == 'ß' || c== '\'')
      {
        // gültig
      }
      else
      {
        throw new DtausException(DtausException.UNGUELTIGES_ZEICHEN, value
            .substring(i, i + 1)
            + "("
            + Util.toHex(value.substring(i, i + 1))
            + ")"
            + " an Position " + i + ": " + value);
      }
    }
  }

  private String codingToDtaus(String value)
  {
    String ret = value;
    ret = ret.replace('Ä', (char) 0x5b);
    ret = ret.replace('ä', (char) 0x5b);
    ret = ret.replace('Ö', (char) 0x5c);
    ret = ret.replace('ö', (char) 0x5c);
    ret = ret.replace('Ü', (char) 0x5d);
    ret = ret.replace('ü', (char) 0x5d);
    ret = ret.replace('ß', (char) 0x7e);
    return ret;
  }

  protected String codingFromDtaus(String value, int toleranz)
  {
    String ret = value;
    ret = ret.replace((char) 0x5b, 'Ä');
    ret = ret.replace((char) 0x8e, 'Ä');
    ret = ret.replace((char) 0x5c, 'Ö');
    ret = ret.replace((char) 0x99, 'Ö');
    ret = ret.replace((char) 0x5d, 'Ü');
    ret = ret.replace((char) 0x9a, 'Ü');
    ret = ret.replace((char) 0x7e, 'ß');
    ret = ret.replace((char) 0xe1, 'ß');
    if (toleranz == DtausDateiParser.UMLAUTUMSETZUNG
        || toleranz == DtausDateiParser.HEX00TOSPACE
        || (toleranz & DtausDateiParser.UMLAUTUMSETZUNG) == DtausDateiParser.UMLAUTUMSETZUNG)
    {
      ret = ret.replace((char) 0x84, 'Ä');
      ret = ret.replace((char) 0x94, 'Ö');
      ret = ret.replace((char) 0x81, 'Ü');
    }
    if (toleranz == DtausDateiParser.HEX00TOSPACE
        || (toleranz & DtausDateiParser.HEX00TOSPACE) == DtausDateiParser.HEX00TOSPACE)
    {
      ret = ret.replace((char) 0x00, ' ');
    }
    return ret;
  }

  /**
   * Datenfelder auf die Länge 27 bringen
   */
  public String make27(String in)
  {
    String out = "";
    if (in.length() >= 27)
    {
      out = in.substring(0, 27);
    }
    if (in.length() < 27)
    {
      out = in + Tool.space(27 - in.length());
    }
    out = codingToDtaus(out);
    return out;
  }

}
/*
 * $Log: Satz.java,v $
 * Revision 1.11  2008/07/09 13:01:42  jost
 * Hochkomma durch Leerzeichen ersetzen
 * Revision 1.10 2008/05/07 15:57:40 jost e mit Accent grave
 * durch e ersetzt
 * 
 * Revision 1.9 2008/02/17 08:31:26 jost Neuer Toleranzlevel Revision 1.8
 * 2007/09/18 17:51:57 jost ÃberflÃ¼ssige throws entfernt. Revision 1.7
 * 2006/11/12 07:30:26 jost Korrekte Umlautbehandlung (DTAUS0/DTAUS1). Revision
 * 1.6 2006/10/08 18:40:08 jost Bugfix: Korrekte Behandlung von Textfeldern der
 * Länge 27
 * 
 * Revision 1.5 2006/10/06 12:48:18 jost Optionale Fehlertoleranz Revision 1.4
 * 2006/09/25 18:28:57 jost Fehlerhaftes Zeichen wird auch als Hex-Wert
 * ausgegeben. Revision 1.3 2006/08/28 19:04:43 jost Korrekte Behandlung von
 * Groß-Kleinschreibung und ÄÖÜß Revision 1.2 2006/06/05 09:35:59 jost
 * Erweiterungen f. d. DtausDateiWriter Revision 1.1 2006/05/24 16:24:44 jost
 * Prerelease
 * 
 */
