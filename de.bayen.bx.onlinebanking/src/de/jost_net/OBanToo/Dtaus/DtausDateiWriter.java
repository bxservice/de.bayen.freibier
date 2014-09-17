/*
 * $Source: /cvsroot/obantoo/obantoo/src/de/jost_net/OBanToo/Dtaus/DtausDateiWriter.java,v $
 * $Revision: 1.7 $
 * $Date: 2008/02/08 18:45:02 $
 * $Author: jost $
 *
 * Copyright 2006 by Heiner Jostkleigrewe
 * Diese Datei steht unter LGPL - siehe beigefügte lpgl.txt
 */
package de.jost_net.OBanToo.Dtaus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Datenträgeraustauschdateien (DTAUS) erstellen
 * <p>
 * Mit dem DtausDateiWriter können DTAUS-Dateien mit einer oder mehreren
 * logischen Dateien erzeugt werden.
 * 
 * Beispiel für die Erstellung einer DTAUS-Datei:<br>
 * <br>
 * Zunächst ist eine OutputStream (z. B. FileOutputStream) zu instanziieren:<br>
 * <code>
 * FileOutputStream fos = new FileOutputStream("dtaus"); <br><br>
 * </code> Anschließend wird ein DtausDateiWriter instanziiert, dem der
 * Outputstream im Konstruktor übergeben wird:<br>
 * <code>
 * DtausDateiWriter dtausDateiWriter = new DtausDateiWriter(fos);<br>
 * </code> <br>
 * Jetzt wird der ASatz gefüllt und geschrieben:<br>
 * <code>
 * dtausDateiWriter.setAGutschriftLastschrift("GK");<br>
 * dtausDateiWriter.setABLZBank(40050060);<br>
 * dtausDateiWriter.setAKundenname("Donald Duck GmbH und Co");<br>
 * dtausDateiWriter.setAKonto(123456);<br>
 * dtausDateiWriter.writeASatz();<br>
 * </code> <br>
 * Ab hier werden die eigentlichen Zahlungssätze erstellt:<br>
 * <code>
 * dtausDateiWriter.setCBLZEndbeguenstigt(10020030);<br>
 * dtausDateiWriter.setCKonto(444444);<br>
 * dtausDateiWriter.setCTextschluessel(CSatz.TS_UEBERWEISUNGSGUTSCHRIFT);<br>
 * dtausDateiWriter.setCInterneKundennummer(1); <br>
 * dtausDateiWriter.setCBetragInEuro(1000);<br>
 * dtausDateiWriter.setCName("Donald Duck");<br>
 * dtausDateiWriter.addCVerwendungszweck("bekannt");<br>
 * dtausDateiWriter.writeCSatz();<br>
 * <br>
 * dtausDateiWriter.setCBLZEndbeguenstigt(80077711);<br>
 * dtausDateiWriter.setCKonto(666);<br>
 * dtausDateiWriter.setCTextschluessel(CSatz.TS_UEBERWEISUNGSGUTSCHRIFT);<br>
 * dtausDateiWriter.setCInterneKundennummer(2);<br>
 * dtausDateiWriter.setCBetragInEuro(1000);<br>
 * dtausDateiWriter.setCName("Micky Maus");<br>
 * dtausDateiWriter.addCVerwendungszweck("la li lu");<br>
 * dtausDateiWriter.addCVerwendungszweck("nur der Mann im Mond ");<br>
 * dtausDateiWriter.addCVerwendungszweck("schaut zu ");<br>
 * dtausDateiWriter.addCVerwendungszweck("wenn die kleinen Kinder");<br>
 * dtausDateiWriter.addCVerwendungszweck("schlafen");<br>
 * dtausDateiWriter.writeCSatz();<br>
 * </code> <br>
 * E-Satz schreiben = Ende einer logischen Datei.<br>
 * <code>
 * dtausDateiWriter.writeESatz();<br>
 * </code> <br>
 * Gegebenenfalls kann eine weitere logische Datei durch die folgenden Aufrufe
 * erstellt werden:<br>
 * <code>
 * dtausDateiWriter.open();<br>
 * dtausDateiWriter.setA.....<br>
 * dtausDateiWriter.writeASatz();<br>
 * :<br>
 * :<br>
 * dtausDateiWriter.writeCSatz();<br>
 * dtausDateiWriter.writeCSatz();<br>
 * dtausDateiWriter.writeASatz();<br>
 * dtausDateiWriter.close;<br>
 * <br>
 * System.out.println("DTAUS-Datei erstellt!");<br>
 * </code>
 */
public class DtausDateiWriter
{

  private static final long serialVersionUID = 8631473446209849972L;

  private DataOutputStream dos;

  private ASatz asatz;

  private CSatz csatz;

  private ESatz esatz;

  public DtausDateiWriter(OutputStream os) 
  {
    dos = new DataOutputStream(os);
    open();
  }

  /**
   * Die Open-Methode wird durch den Konstruktor aufgerufen. Ein erneuter Aufruf
   * darf erfolgen, nachdem der E-Satz geschrieben wurden. Damit wird die
   * Ausgabe einer weiteren logischen Datei möglich.
   * 
   */
  public void open() 
  {
    asatz = new ASatz();
    csatz = new CSatz();
    esatz = new ESatz();
  }

  /**
   * Die Close-Methode ist nach der Ausgabe des E-Satzes der letzten/einzigen
   * logischen Datei erforderlich.
   * 
   * @throws IOException
   */
  public void close() throws IOException
  {
    dos.flush();
    dos.close();
  }

  public void setAGutschriftLastschrift(String aGutschriftLastschrift)
      throws DtausException
  {
    asatz.setGutschriftLastschrift(aGutschriftLastschrift);
  }

  /**
   * Feld A4 - Bankleitzahl der Bank, bei der die Diskette eingereicht wird
   * Gültige Werte: Siehe Verzeichnis der Bankleitzahlen Standardwert: 0
   */
  public void setABLZBank(long aBLZBank)
  {
    asatz.setBlz(aBLZBank);
  }
  
  public long getABLZBank()
  {
    return asatz.getBlz();
  }

  /**
   * Feld A6 - Name des Auftraggebers. Gültige Werte: Siehe zugelassene Zeichen,
   * max Länge: 27 Standardwert: "" (leer)
   */
  public void setAKundenname(String aKundenname) throws DtausException
  {
    asatz.setKundenname(aKundenname);
  }
  
  public String getAKundenname()
  {
    return asatz.getKundenname();
  }

  /**
   * Feld A7 - Datum Format: TTMMJJ Standardwert: Tagesdatum
   */
  public void setADatum(String aDatum)
  {
    asatz.setDateierstellungsdatum(aDatum);
  }

  /**
   * Feld A7 - Datum Standardwert: Tagesdatum
   */
  public void setADatum(Date aDatum) 
  {
    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
    setADatum(sdf.format(aDatum));
  }

  /**
   * Feld A9 - Kontonummer des Auftraggebers Standardwert: 0
   */
  public void setAKonto(long aKonto)
  {
    asatz.setKonto(aKonto);
  }
  
  public long getAKonto()
  {
    return asatz.getKonto();
  }

  /**
   * Feld A11b - Ausführungsdatum Angabe freigestellt. Nicht jünger als
   * Diskettenerstellungsdatum (Feld A7), jedoch höchstens 15 Kalendertage über
   * dem Erstellungsdatum aus Feld A7. Soweit in diesem Datenfeld ein
   * Ausführungstermin angegeben wird, ist zu beachten, dass der in Abs. II, Nr.
   * 3 der Sonderbedingungen für den Datenträgeraustausch genannte
   * Nachweiszeitraum von mindestens zehn Kalendertagen erst ab dem genannten
   * Ausführungstermin zu berechnen ist.
   */
  public void setAAusfuehrungsdatum(Date aAusfuehrungsdatum)
  {
    asatz.setAusfuehrungsdatum(aAusfuehrungsdatum);
  }

  /**
   * Feld C3 - Bankleitzahl erstbeteiligtes Kreditinstitut, freigestellt
   */
  public void setCBLZErstbeteiligtesInstitut(long value) throws DtausException
  {
    csatz.setBlzErstbeteiligt(value);
  }

  /**
   * Feld C4 - Bankleitzahl endbegünstigtes Institut
   */
  public void setCBLZEndbeguenstigt(long value) throws DtausException
  {
    csatz.setBlzEndbeguenstigt(value);
  }
  
  public long getCBLZEndbeguenstigt()
  {
    return csatz.getBlzEndbeguenstigt();
  }

  /**
   * Feld C5 - Kontonummer des Überweisungsempfängers/Zahlungspflichtigen
   */
  public void setCKonto(long cKonto) throws DtausException
  {
    csatz.setKontonummer(cKonto);
  }

  public long getCKonto()
  {
    return csatz.getKontonummer();
  }
  /**
   * Feld C7 - Textschlüssel - Kennzeichnung der Zahlungsart "51000"
   * Überweisungs-Gutschrift "53000" Überweisung Lohn/Gehalt/Rente "5400J"
   * Vermögenswirksame Leistung (VL) ohne Sparzulage "5400J" Vermögenswirksame
   * Leistung (VL) mit Sparzulage "56000" Überweisung öffentlicher Kassen Die im
   * Textschlüssel mit J bezeichnete Stelle, wird bei Übernahme in eine Zahlung
   * automatisch mit der jeweils aktuellen Jahresendziffer (7, wenn 97) ersetzt.
   */

  public void setCTextschluessel(int value) throws DtausException
  {
    csatz.setTextschluessel(value);
  }

  /**
   * Feld C12 - Betrag in Euro, wenn Auftragswährung in den Feldern A12 und C17a =
   * "1"; sonst Nullen
   */
  public void setCBetragInCent(long cBetragInCent) throws DtausException
  {
    csatz.setBetragInCent(cBetragInCent);
  }

  public void setCBetragInEuro(double value) throws DtausException
  {
    csatz.setBetragInEuro(value);
  }
  
  public double getCBetragInEuro()
  {
    return csatz.getBetragInEuro();
  }

  /**
   * Feld C14a - Name des Überweisungsempfängers/Zahlungspflichtigen. Max. 27
   * Stellen
   */
  public void setCName(String value) throws DtausException
  {
    csatz.setNameEmpfaenger(value);
  }

  /**
   * Name des Überweisungsempfängers/Zahlungspflichtigen -Teil 2-, max. 27 Stellen.
   */
  public void setCName2(String value) throws DtausException
  {
    csatz.setNameEmpfaenger2(value);
  }

  public void setCInterneKundennummer(long value) throws DtausException
  {
    csatz.setInterneKundennummer(value);
  }

  /**
   * Maximal 13 mal pro Buchungssatz aufrufbar
   */
  public void addCVerwendungszweck(String value) throws DtausException
  {
    csatz.addVerwendungszweck(value);
  }

  public void writeASatz() throws Exception
  {
    asatz.write(dos);
  }
  
  public String writeCSatz() throws IOException, DtausException
  {
    csatz.setNameAbsender(asatz.getKundenname());
    csatz.setErstbeauftragtesInstitut(asatz.getBlz());
    csatz.setKontoAuftraggeber(asatz.getKonto());
    esatz.add(csatz); // Kontrollsummen addieren
    csatz.write(dos);
    String ret = csatz.toString();
    csatz = new CSatz();
    return ret;
  }

  public void writeESatz() throws IOException
  {
    esatz.write(dos);
  }
  
  public long getAnzahlSaetze()
  {
    return esatz.getAnzahlDatensaetze();
  }

  public BigInteger getSummeKontonummern()
  {
    return esatz.getSummeKontonummern();
  }

  public BigInteger getSummeBankleitzahlen()
  {
    return esatz.getSummeBankleitzahlen();
  }

  public BigInteger getSummeBetraege()
  {
    return esatz.getSummeBetraege();
  }

  public BigDecimal getSummeBetraegeDecimal()
  {
    return new BigDecimal(esatz.getSummeBetraege()).divide(new BigDecimal(100));
  }
}
/*
 * $Log: DtausDateiWriter.java,v $
 * Revision 1.7  2008/02/08 18:45:02  jost
 * *** empty log message ***
 *
 * Revision 1.6  2007/09/18 17:50:58  jost
 * überflüssige throws entfernt.
 * Zusätzliche Methoden.
 *
 * Revision 1.5  2007/02/22 18:40:04  jost
 * Implementierung der Erweiterungsteile 01 (Name Empfänger/Zahlungspflichtiger 2) und 03 (Absender/Zahlungsempfänger 2)
 * Revision 1.4 2006/09/20 15:47:29 jost Neue
 * Methode "BigDecimal getSummeBetraegeDecimal()" Revision 1.3 2006/08/28
 * 19:03:42 jost Korrekte Behandlung von Groß-Kleinschreibung und ÄÖÜß Revision
 * 1.2 2006/06/14 19:56:41 jost Mehrere logische Dateien können jetzt ausgegeben
 * werden. Revision 1.1 2006/06/05 09:34:51 jost Neu
 * 
 */
