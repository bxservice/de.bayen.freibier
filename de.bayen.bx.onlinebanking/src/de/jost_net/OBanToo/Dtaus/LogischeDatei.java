/*
 * $Source: /cvsroot/obantoo/obantoo/src/de/jost_net/OBanToo/Dtaus/LogischeDatei.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/05/28 09:07:31 $
 * $Author: jost $
 *
 * Copyright 2006 by Heiner Jostkleigrewe
 * Diese Datei steht unter LGPL - siehe beigefügte lpgl.txt
 */
package de.jost_net.OBanToo.Dtaus;

import java.util.Vector;
/**
 * Logische Datei innerhalb einer physikalischen DTAUS-Datei
 * @author Heiner Jostkleigrewe
 *
 */
public class LogischeDatei
{
  private ASatz asatz;

  private Vector csaetze;

  private ESatz esatz;

  private int cpos = -1;

  public LogischeDatei(ASatz asatz)
  {
    this.asatz = asatz;
    csaetze = new Vector();
  }

  public ASatz getASatz()
  {
    return asatz;
  }

  public void addCSatz(CSatz csatz)
  {
    csaetze.addElement(csatz);
  }

  public CSatz getNextCSatz()
  {
    cpos++;
    if (cpos >= csaetze.size())
    {
      return null;
    }
    return (CSatz) csaetze.elementAt(cpos);
  }

  public void setESatz(ESatz esatz)
  {
    this.esatz = esatz;
  }

  public ESatz getESatz()
  {
    return esatz;
  }
}
/*
 * $Log: LogischeDatei.java,v $
 * Revision 1.1  2006/05/28 09:07:31  jost
 * Neu: Logische Dateien innerhalb einer physikalischen Datei
 * Revision 1.2 2006/05/25 20:30:40 jost
 */
