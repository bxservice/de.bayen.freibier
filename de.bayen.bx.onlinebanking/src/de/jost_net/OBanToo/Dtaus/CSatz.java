/*
 * $Source: /cvsroot/obantoo/obantoo/src/de/jost_net/OBanToo/Dtaus/CSatz.java,v $
 * $Revision: 1.17 $
 * $Date: 2008/04/21 18:15:14 $
 * $Author: jost $
 *
 * Copyright 2006 by Heiner Jostkleigrewe
 * Diese Datei steht unter LGPL - siehe beigefügte lpgl.txt
 */
package de.jost_net.OBanToo.Dtaus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Vector;

/**
 * C-Satz - Zahlungsaustauschsatz
 * 
 * @author Heiner Jostkleigrewe
 * 
 */
// todo Sätze mit mehr als 3 Erweiterungsteilen testen
public class CSatz extends Satz
{

  public static final int TS_LASTSCHRIFT_ABBUCHUNGSVERFAHREN = 4000;

  public static final int TS_LASTSCHRIFT_EINZUGSERMAECHTIGUNGSVERFAHREN = 5000;

  public static final int TS_LASTSCHRIFT_EC_CASH = 5005;

  public static final int TS_LASTSCHRIFT_EC_CASH_AUSLAND = 5006;

  public static final int TS_LASTSCHRIFT_KREDITKARTE = 5008;

  public static final int TS_LASTSCHRIFT_POS = 05015;

  public static final int TS_BANK_09 = 9000;

  public static final int TS_UEBERWEISUNGSGUTSCHRIFT = 51000;

  public static final int TS_UEBERWEISUNG_LOHN_GEHALT_RENTE = 53000;

  public static final int TS_UEBERWEISUNG_OEFFENTL_KASSEN = 56000;

  public static final int TS_BANK_59 = 59000;

  public static final int TS_BANK_67 = 67000;

  public static final int TS_BANK_68 = 68000;

  public static final int TS_BANK_69 = 69000;

  /**
   * Feld c01, 4 Byte, numerisch, Satzlänge, konstanter Teil 187 Bytes +
   * Erweiterungsteil( e) zu 29 Bytes, max. 0622 Stellen
   */
  private int cSatzlaenge = 0;

  /**
   * Feld c02, 1 Byte, alpha, Satzart, konstant 'C'
   */
  private String cSatzart = "C";

  /**
   * Feld c03, 8 Bytes, numerisch, Bankleitzahl, erstbeteiligtes Kreditinstitut,
   * freigestellt
   */
  private long cBlzErstbeteiligt = 0;

  /**
   * Feld c04, 8 Bytes, numerisch, Bankleitzahl, endbegünstigtes
   * Kreditinstitut/Zahlstelle
   */
  private long cBlzEndbeguenstigt = 0;

  private boolean cBlzEndbeguenstigtSet = false;

  /**
   * Feld c05, 10 Bytes, numerisch, Kontonummer,
   * Überweisungsempfänger/Zahlungspflichtiger, rechtsbündig, nicht belegte
   * Stellen 0
   */
  private long cKonto = 0;

  private boolean cKontoSet = false;

  /**
   * Feld c06, 13 Bytes, numerisch, interne Kundennummer, 1. Byte = 0, 2.-12.
   * Byte = interne Kundennummer oder Nullen, 13. Byte = 0
   */
  private long cInterneKundennummer = 0L;

  private boolean cInterneKundennummerSet = false;

  /**
   * Feld c07, 5 Bytes, numerisch, Textschlüssel, Kennzeichnung der Zahlungsart
   * und Textschlüsselergänzungen
   */
  private int cTextschluessel;

  private boolean cTextschluesselSet = false;

  /**
   * Feld c10, 8 Bytes, numerisch, erstbeauftragtes Institut/erste Inkassostelle
   */
  private long cErstbeauftragtesInstitut = 0;

  private boolean cErstbeauftragtesInstitutSet = false;

  /**
   * Feld c11, 10 Bytes, numerisch, Auftraggeber/Zahlungsempfänger,
   * rechtsbündig, nicht belegte Stellen 0
   */
  private long cKontoAuftraggeber = 0;

  private boolean cKontoAuftraggeberSet = false;

  /**
   * Feld c12, 11 Bytes, numerisch, Betrag in Euro einschl. Nachkommastellen
   * (müßte eigentlich Betrag in Cent heißen)
   */
  private long cBetrag = 0;

  private boolean cBetragSet = false;

  /**
   * Feld c14a, 27 Bytes, alpha, Name,
   * Überweisungsempfänger/Zahlungspflichtiger, linksbündig
   */
  private String cNameEmpfaenger = null;

  private boolean cNameEmpfaengerSet = false;

  /**
   * Feld c15, 27 Bytes, alpha, Name, Überweisender/Zahlungsempfänger
   * (linksbündig), es sind möglichst kurze Bezeichnungen zu verwenden
   */
  private String cNameAbsender = null;

  private boolean cNameAbsenderSet = false;

  /**
   * Feld c16, 27 Bytes, alpha, Verwendungszweck, Es sind möglichst kurze
   * Angaben zu machen. Linksbündig sind solche Angaben unterzubringen, auf die
   * der Begünstigte bei Überweisungen möglicherweise zuzugreifen beabsichtigt
   * (z. B. Bausparkonto, Versicherungs-, Rechnungsnummer) oder die der
   * Zahlungsempfänger bei Lastschriften benötigt, falls die Zahlung als
   * unbezahlt bzw. unanbringlich zurückgeleitet wird.
   */
  private Vector cVerwendungszweck = null;

  private boolean cVerwendungszweckSet = false;

  private String cNameEmpfaenger2 = null;

  private String cNameAbsender2 = null;

  /**
   * Feld 18, 2 Bytes, numerisch, Erweiterungszeichen, 00 = es folgt kein
   * Erweiterungsteil, 01-15 = Anzahl der Erweiterungsteile
   */
  private int cErweiterungszeichen = 0;

  private Vector cErweiterungsteile = null;

  public CSatz()
  {
    init();
  }

  /**
   * Konstruktor mit der Übergabe eines zu parsenden Satzes
   */
  public CSatz(String satz, int toleranz) throws DtausException
  {
    satz = codingFromDtaus(satz, toleranz);
    validCharacters(satz);
    init();
    checkSatzlaengenfeld(satz.substring(0, 4));
    if (!satz.substring(4, 5).equals(cSatzart))
    {
      throw new DtausException(DtausException.C_SATZART_FEHLERHAFT, satz
          .substring(4, 5));
    }
    setBlzErstbeteiligt(satz.substring(5, 13));
    setBlzEndbeguenstigt(satz.substring(13, 21));
    setKontonummer(satz.substring(21, 31));
    setInterneKundennummer(satz.substring(32, 44));
    setTextschluessel(satz.substring(44, 49));
    setErstbeauftragtesInstitut(satz.substring(61, 69));
    setKontoAuftraggeber(satz.substring(69, 79));
    setBetragInCent(satz.substring(79, 90));
    setNameEmpfaenger(satz.substring(93, 120));
    setNameAbsender(satz.substring(128, 155));
    addVerwendungszweck(satz.substring(155, 182));
    setErweiterungskennzeichen(satz.substring(185, 187));
    if (getSatzlaenge() != 187 + (getErweiterungszeichen() * 29))
    {
      throw new DtausException(DtausException.C_SATZLAENGE_FEHLERHAFT, satz
          .substring(0, 4));
    }

    // Startpositionen der Erweiterungsteile
    int[] pos = { 187, 216, 256, 285, 314, 343, 384, 413, 442, 471, 512, 541,
        570, 599 };
    for (int i = 0; i < this.getErweiterungszeichen(); i++)
    {
      int p = pos[i];
      addErweiterung(satz.substring(p, p + 29));
    }
  }

  private void init()
  {
    cVerwendungszweck = new Vector();
  }

  private void checkSatzlaengenfeld(String value) throws DtausException
  {
    try
    {
      cSatzlaenge = Integer.parseInt(value);
    }
    catch (NumberFormatException e)
    {
      throw new DtausException(DtausException.C_SATZLAENGE_FEHLERHAFT, value);
    }
    if (cSatzlaenge < 187 || cSatzlaenge > 622)
    {
      throw new DtausException(DtausException.C_SATZLAENGE_FEHLERHAFT, value);
    }
  }

  public int getSatzlaenge()
  {
    return cSatzlaenge;
  }

  public void setBlzErstbeteiligt(String value) throws DtausException
  {
    try
    {
      setBlzErstbeteiligt(Long.parseLong(value));
    }
    catch (NumberFormatException e)
    {
      throw new DtausException(DtausException.C_BLZERSTBETEILIGT_FEHLERHAFT,
          value);
    }
  }

  public void setBlzErstbeteiligt(long value) throws DtausException
  {
    if (value < 0 || value > 99999999)
    {
      throw new DtausException(DtausException.C_BLZERSTBETEILIGT_FEHLERHAFT,
          value + "");
    }
    cBlzErstbeteiligt = value;
  }

  public long getBlzErstbeteiligt()
  {
    return cBlzErstbeteiligt;
  }

  public void setBlzEndbeguenstigt(String value) throws DtausException
  {
    try
    {
      setBlzEndbeguenstigt(Long.parseLong(value));
    }
    catch (NumberFormatException e)
    {
      throw new DtausException(DtausException.C_BLZENDBEGUENSTIGT_FEHLERHAFT,
          value);
    }
  }

  public void setBlzEndbeguenstigt(long value) throws DtausException
  {
    if (value <= 0 || value >= 99999999)
    {
      throw new DtausException(DtausException.C_BLZENDBEGUENSTIGT_FEHLERHAFT);
    }
    this.cBlzEndbeguenstigt = value;
    this.cBlzEndbeguenstigtSet = true;
  }

  public long getBlzEndbeguenstigt()
  {
    return cBlzEndbeguenstigt;
  }

  public void setKontonummer(String value) throws DtausException
  {
    try
    {
      setKontonummer(Long.parseLong(value));
    }
    catch (NumberFormatException e)
    {
      throw new DtausException(DtausException.C_KONTONUMMER_FEHLERHAFT, value);
    }
  }

  public void setKontonummer(long value) throws DtausException
  {
    if (value <= 0 || value >= 9999999999L)
    {
      throw new DtausException(DtausException.C_KONTONUMMER_FEHLERHAFT);
    }
    cKonto = value;
    cKontoSet = true;
  }

  public long getKontonummer()
  {
    return cKonto;
  }

  public void setInterneKundennummer(String value) throws DtausException
  {
    try
    {
      setInterneKundennummer(Long.parseLong(value));
    }
    catch (NumberFormatException e)
    {
      throw new DtausException(DtausException.C_INTERNEKUNDENNUMMER_FEHLERHAFT,
          value);
    }
  }

  public void setInterneKundennummer(long value) throws DtausException
  {
    if (value < 0)
    {
      throw new DtausException(DtausException.C_INTERNEKUNDENNUMMER_FEHLERHAFT,
          value + "");
    }
    this.cInterneKundennummer = value;
    this.cInterneKundennummerSet = true;
  }

  public long getInterneKundennummer()
  {
    return cInterneKundennummer;
  }

  public void setTextschluessel(String value) throws DtausException
  {
    try
    {
      setTextschluessel(Integer.parseInt(value));
    }
    catch (NumberFormatException e)
    {
      throw new DtausException(DtausException.C_TEXTSCHLUESSEL_FEHLERHAFT,
          value);
    }
  }

  public void setTextschluessel(int value) throws DtausException
  {
    DecimalFormat df = new DecimalFormat("00000");
    String valueStr = df.format(value);
    String textschluesselStr = valueStr.substring(0, 2);
    int textschluessel = Integer.parseInt(textschluesselStr);
    String textschluesselergaenzungStr = valueStr.substring(2);
    int textschluesselergaenzung = Integer
        .parseInt(textschluesselergaenzungStr);

    boolean zulässig = false;

    if (textschluessel == 1)
    {
      if (textschluesselergaenzung == 0 || textschluesselergaenzung == 888)
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 2)
    {
      if (textschluesselergaenzung == 0 || textschluesselergaenzung == 888)
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 3 && textschluesselergaenzung == 0)
    {
      zulässig = true;
    }
    else if (textschluessel == 4)
    {
      if (textschluesselergaenzung == 0 || textschluesselergaenzung == 444
          || textschluesselergaenzung == 888)
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 5)
    {
      if (textschluesselergaenzung == 0
          || textschluesselergaenzung == 1
          || textschluesselergaenzung == 2
          || textschluesselergaenzung == 3
          || textschluesselergaenzung == 4
          || textschluesselergaenzung == 5
          || textschluesselergaenzung == 6
          || textschluesselergaenzung == 7
          || textschluesselergaenzung == 8
          || textschluesselergaenzung == 9
          || textschluesselergaenzung == 10
          || textschluesselergaenzung == 11
          || textschluesselergaenzung == 15
          || textschluesselergaenzung == 19
          || textschluesselergaenzung == 20
          || textschluesselergaenzung == 21
          || textschluesselergaenzung == 71
          || textschluesselergaenzung == 73
          || textschluesselergaenzung == 200
          || textschluesselergaenzung == 201
          || textschluesselergaenzung == 202
          || textschluesselergaenzung == 210
          || textschluesselergaenzung == 222
          || textschluesselergaenzung == 230
          || textschluesselergaenzung == 240
          || textschluesselergaenzung == 242
          || textschluesselergaenzung == 444
          || textschluesselergaenzung == 888
          || (textschluesselergaenzungStr.length() == 3 && textschluesselergaenzungStr
              .endsWith("9")))
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 9)
    {
      zulässig = true;
    }
    else if (textschluessel == 10 && textschluesselergaenzung == 0)
    {
      zulässig = true;
    }
    else if (textschluessel == 11)
    {
      if (textschluesselergaenzung == 0 || textschluesselergaenzung == 888)
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 12 && textschluesselergaenzung == 0)
    {
      zulässig = true;
    }
    else if (textschluessel == 13 && textschluesselergaenzung == 0)
    {
      zulässig = true;
    }
    else if (textschluessel == 14)
    {
      if (textschluesselergaenzung == 1 || textschluesselergaenzung == 2
          || textschluesselergaenzung == 3 || textschluesselergaenzung == 5
          || textschluesselergaenzung == 6 || textschluesselergaenzung == 7
          || textschluesselergaenzung == 8 || textschluesselergaenzung == 9
          || textschluesselergaenzung == 10 || textschluesselergaenzung == 11
          || textschluesselergaenzung == 12 || textschluesselergaenzung == 13
          || textschluesselergaenzung == 84 || textschluesselergaenzung == 85
          || textschluesselergaenzung == 86 || textschluesselergaenzung == 87
          || textschluesselergaenzung == 88 || textschluesselergaenzung == 89)
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 15
        && (textschluesselergaenzung == 0 || isEUStaat(textschluesselergaenzung)))
    {
      zulässig = true;
    }
    else if (textschluessel == 51)
    {
      if (textschluesselergaenzung == 0
          || textschluesselergaenzung == 200
          || textschluesselergaenzung == 210
          || textschluesselergaenzung == 211
          || textschluesselergaenzung == 212
          || textschluesselergaenzung == 220
          || textschluesselergaenzung == 221
          || textschluesselergaenzung == 230
          || textschluesselergaenzung == 240
          || textschluesselergaenzung == 241
          || textschluesselergaenzung == 401
          || textschluesselergaenzung == 402
          || textschluesselergaenzung == 403
          || textschluesselergaenzung == 405
          || textschluesselergaenzung == 406
          || textschluesselergaenzung == 407
          || textschluesselergaenzung == 408
          || textschluesselergaenzung == 409
          || textschluesselergaenzung == 410
          || textschluesselergaenzung == 411
          || textschluesselergaenzung == 412
          || textschluesselergaenzung == 413
          || textschluesselergaenzung == 444
          || textschluesselergaenzung == 445
          || textschluesselergaenzung == 484
          || textschluesselergaenzung == 485
          || textschluesselergaenzung == 486
          || textschluesselergaenzung == 487
          || textschluesselergaenzung == 488
          || textschluesselergaenzung == 489
          || textschluesselergaenzung == 501
          || textschluesselergaenzung == 502
          || textschluesselergaenzung == 503
          || textschluesselergaenzung == 504
          || textschluesselergaenzung == 505
          || textschluesselergaenzung == 506
          || textschluesselergaenzung == 507
          || textschluesselergaenzung == 509
          || textschluesselergaenzung == 510
          || textschluesselergaenzung == 511
          || textschluesselergaenzung == 520
          || textschluesselergaenzung == 521
          || textschluesselergaenzung == 571
          || textschluesselergaenzung == 573
          || textschluesselergaenzung == 888
          || textschluesselergaenzung == 990
          || (textschluesselergaenzungStr.length() == 3 && textschluesselergaenzungStr
              .endsWith("9")))
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 52)
    {
      if (textschluesselergaenzung == 0 || textschluesselergaenzung == 444
          || textschluesselergaenzung == 888)
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 53)
    {
      if (textschluesselergaenzung == 0 || textschluesselergaenzung == 444
          || textschluesselergaenzung == 445 || textschluesselergaenzung == 888)
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 54)
    {
      zulässig = true;
    }
    else if (textschluessel == 56)
    {
      if (textschluesselergaenzung == 0 || textschluesselergaenzung == 444
          || textschluesselergaenzung == 445)
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 59)
    {
      zulässig = true;
    }
    else if (textschluessel == 63
        && (textschluesselergaenzung == 0 || isEUStaat(textschluesselergaenzung)))
    {
      zulässig = true;
    }
    else if (textschluessel == 65 && isEUStaat(textschluesselergaenzung))
    {
      zulässig = true;
    }
    else if (textschluessel == 67)
    {
      if (textschluesselergaenzung == 0 || textschluesselergaenzung == 444
          || textschluesselergaenzung == 445 || textschluesselergaenzung == 888)
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 68)
    {
      if (textschluesselergaenzung == 0 || textschluesselergaenzung == 444
          || textschluesselergaenzung == 445 || textschluesselergaenzung == 888)
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 69)
    {
      if (textschluesselergaenzung == 0 || textschluesselergaenzung == 444
          || textschluesselergaenzung == 445 || textschluesselergaenzung == 888)
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 81)
    {
      if (textschluesselergaenzung == 0)
      {
        zulässig = true;
      }
    }
    else if (textschluessel == 84)
    {
      if (textschluesselergaenzung == 0)
      {
        zulässig = true;
      }
    }

    if (!zulässig)
    {
      throw new DtausException(DtausException.C_TEXTSCHLUESSEL_FEHLERHAFT,
          valueStr);
    }
    cTextschluessel = value;
    cTextschluesselSet = true;
  }

  private boolean isEUStaat(int texterweiterung)
  {
    return (texterweiterung == 56 || texterweiterung == 208
        || texterweiterung == 246 || texterweiterung == 250
        || texterweiterung == 300 || texterweiterung == 826
        || texterweiterung == 372 || texterweiterung == 352
        || texterweiterung == 380 || texterweiterung == 442
        || texterweiterung == 528 || texterweiterung == 578
        || texterweiterung == 040 || texterweiterung == 620
        || texterweiterung == 752 || texterweiterung == 756);
  }

  public long getTextschluessel()
  {
    return cTextschluessel;
  }

  public void setErstbeauftragtesInstitut(String value) throws DtausException
  {
    try
    {
      setErstbeauftragtesInstitut(Long.parseLong(value));
    }
    catch (NumberFormatException e)
    {
      throw new DtausException(
          DtausException.C_ERSTBEAUFTRAGTESINSTITUT_FEHLERHAFT, value);
    }
  }

  public void setErstbeauftragtesInstitut(long value) throws DtausException
  {
    if (value < 0)
    {
      throw new DtausException(
          DtausException.C_ERSTBEAUFTRAGTESINSTITUT_FEHLERHAFT);
    }
    this.cErstbeauftragtesInstitut = value;
    this.cErstbeauftragtesInstitutSet = true;
  }

  public long getErstbeauftragtesInstitut()
  {
    return cErstbeauftragtesInstitut;
  }

  public void setKontoAuftraggeber(String value) throws DtausException
  {
    try
    {
      setKontoAuftraggeber(Long.parseLong(value));
    }
    catch (NumberFormatException e)
    {
      throw new DtausException(DtausException.C_KONTOAUFTRAGGEBER_FEHLERHAFT,
          value);
    }
  }

  public void setKontoAuftraggeber(long value) throws DtausException
  {
    if (value <= 0)
    {
      throw new DtausException(DtausException.C_KONTOAUFTRAGGEBER_FEHLERHAFT,
          value + "");
    }
    this.cKontoAuftraggeber = value;
    this.cKontoAuftraggeberSet = true;
  }

  public long getKontoAuftraggeber()
  {
    return cKontoAuftraggeber;
  }

  public void setBetragInCent(String value) throws DtausException
  {
    try
    {
      setBetragInCent(Long.parseLong(value));
    }
    catch (NumberFormatException e)
    {
      throw new DtausException(DtausException.C_BETRAG_FEHLERHAFT, value);
    }
  }

  public void setBetragInCent(long value) throws DtausException
  {
    if (value <= 0)
    {
      throw new DtausException(DtausException.C_BETRAG_FEHLERHAFT, value + "");
    }
    cBetrag = value;
    cBetragSet = true;
  }

  /**
   * Betrag in Euro setzen.
   */
  public void setBetragInEuro(double value) throws DtausException
  {
    BigDecimal bval = new BigDecimal(value + "");
    bval = bval.multiply(new BigDecimal("100"));
    setBetragInCent(bval.longValue());
  }

  public long getBetragInCent()
  {
    return cBetrag;
  }

  public double getBetragInEuro()
  {
    return cBetrag / 100d;
  }

  public void setNameEmpfaenger(String value) throws DtausException
  {
    value = value.trim();
    if (value.length() > 27)
    {
      throw new DtausException(DtausException.C_NAME_EMPFAENGER);
    }
    this.cNameEmpfaenger = makeValid(value);
    validCharacters(this.cNameEmpfaenger);
    this.cNameEmpfaengerSet = true;
  }

  public String getNameEmpfaenger()
  {
    return this.cNameEmpfaenger;
  }

  public void setNameEmpfaenger2(String value) throws DtausException
  {
    value = value.trim();
    if (value.length() > 27)
    {
      throw new DtausException(DtausException.C_NAME_EMPFAENGER2, value);
    }
    this.cNameEmpfaenger2 = makeValid(value);
    validCharacters(this.cNameEmpfaenger2);
  }

  public String getNameEmpfaenger2()
  {
    return this.cNameEmpfaenger2;
  }

  public void setNameAbsender(String value) throws DtausException
  {
    value = value.trim();
    if (value.length() > 27)
    {
      throw new DtausException(DtausException.C_NAME_ABSENDER);
    }
    this.cNameAbsender = makeValid(value);
    validCharacters(this.cNameAbsender);
    this.cNameAbsenderSet = true;
  }

  public String getNameAbsender()
  {
    return cNameAbsender;
  }

  public void setNameAbsender2(String value) throws DtausException
  {
    value = value.trim();
    if (value.length() > 27)
    {
      throw new DtausException(DtausException.C_NAME_ABSENDER2);
    }
    this.cNameAbsender2 = makeValid(value);
    validCharacters(this.cNameAbsender2);
  }

  public String getNameAbsender2()
  {
    return cNameAbsender2;
  }

  public void addVerwendungszweck(String value) throws DtausException
  {
    value = value.trim();
    if (value.length() > 27)
    {
      throw new DtausException(DtausException.C_VERWENDUNGSZWECK_FEHLERHAFT,
          value);
    }
    String vzw = makeValid(value);
    validCharacters(vzw);
    this.cVerwendungszweck.addElement(vzw);
    this.cVerwendungszweckSet = true;
  }

  public String getVerwendungszweck(int nr)
  {
    if (nr <= getAnzahlVerwendungszwecke())
    {
      return (String) cVerwendungszweck.elementAt(nr - 1);
    }
    else
    {
      return null;
    }
  }

  private String getErweiterungsteil(int nr)
  {
    if (nr <= cErweiterungszeichen)
    {
      return (String) cErweiterungsteile.elementAt(nr - 1);
    }
    else
    {
      return null;
    }
  }

  public int getAnzahlVerwendungszwecke()
  {
    return cVerwendungszweck.size();
  }

  public void setErweiterungskennzeichen(String value) throws DtausException
  {
    try
    {
      cErweiterungszeichen = Integer.parseInt(value);
    }
    catch (NumberFormatException e)
    {
      throw new DtausException(DtausException.C_ERWEITERUNGSZEICHEN_FEHLERHAFT,
          value);
    }
  }

  public int getErweiterungszeichen()
  {
    return cErweiterungszeichen;
  }

  public void addErweiterung(String value) throws DtausException
  {
    String val = value.substring(2).trim();
    val = makeValid(val);
    validCharacters(val);
    if (value.startsWith("02"))
    {
      addVerwendungszweck(val);
    }
    else if (value.startsWith("01"))
    {
      setNameEmpfaenger2(val);
    }
    else if (value.startsWith("03"))
    {
      setNameAbsender2(val);
    }
    else
    {
      throw new DtausException(DtausException.C_ERWEITERUNG_FEHLERHAFT, value);
    }
  }

  public void write(DataOutputStream dos) throws IOException, DtausException
  {
    if (!isOK())
    {
      throw new DtausException("Nicht alle C-Satz-Daten gefüllt.");
    }
    cErweiterungszeichen = cVerwendungszweck.size() - 1;
    if (cNameEmpfaenger2 != null)
    {
      cErweiterungszeichen++;
    }
    if (cNameAbsender2 != null)
    {
      cErweiterungszeichen++;
    }
    // Feld 1 - Satzlänge
    dos.writeBytes(Tool.formatSL(187 + (cErweiterungszeichen * 29)));
    // Feld 2 - Satzart
    dos.writeBytes("C");
    // Feld 3 - Bankleitzahl erstbeteiligtes Institut
    dos.writeBytes(Tool.formatBLZ(cBlzErstbeteiligt));
    // Feld 4 - Bankleitzahl endbegünstigtes Institut / Zahlstelle
    dos.writeBytes(Tool.formatBLZ(cBlzEndbeguenstigt));
    // Feld 5 - Kontonummer Überweisungsempfänger / Zahlungspflichtiger
    dos.writeBytes(Tool.formatKonto(cKonto));
    // Feld 6 - Interne Kundennummer
    dos.writeBytes("0000000000000");
    // Feld 7 - Textschluessel
    dos.writeBytes(Tool.formatTextschluessel(cTextschluessel));
    // Feld 8 - Bankinternes Feld
    dos.writeBytes(" ");
    // Feld 9 - Betrag in DM
    DecimalFormat dfBetrag = new DecimalFormat("00000000000");
    dos.writeBytes(dfBetrag.format(0));
    // Feld 10 - Bankleitzahl erstbeauftragtes Institut/erste Inkassostelle
    dos.writeBytes(Tool.formatBLZ(this.cErstbeauftragtesInstitut));
    // Feld 11 - Konto Auftraggeber
    dos.writeBytes(Tool.formatKonto(this.cKontoAuftraggeber));
    // Feld 12 - Betrag in Euro
    dos.writeBytes(dfBetrag.format(this.cBetrag));
    // Feld 13 - Reserve
    dos.writeBytes(Tool.space(3));
    // Feld 14a - Name Überweisungsempfänger / Zahlungspflichtiger
    dos.writeBytes(make27(this.cNameEmpfaenger));
    // Feld 14b - Abgrenzung des Satzabschnittes
    dos.writeBytes(Tool.space(8));
    // Feld 15 - Name Auftraggeber / Zahlungsempfänger
    dos.writeBytes(make27(this.cNameAbsender));
    // Feld 16 - Verwendungszweck 1
    dos.writeBytes(make27(this.getVerwendungszweck(1)));
    // Feld 17a - Währungskennzeichen
    dos.writeBytes("1");
    // Feld 17b - Reserve
    dos.writeBytes(Tool.space(2));
    // Erweiterungsteile aufbauen
    cErweiterungsteile = new Vector();
    if (cNameEmpfaenger2 != null)
    {
      cErweiterungsteile.addElement("01" + make27(this.getNameEmpfaenger2()));
    }
    for (int i = 2; i <= this.getAnzahlVerwendungszwecke(); i++)
    {
      cErweiterungsteile.addElement("02" + make27(this.getVerwendungszweck(i)));
    }
    if (cNameAbsender2 != null)
    {
      cErweiterungsteile.addElement("03" + make27(this.getNameAbsender2()));
    }
    // Feld 18 - Anzahl Erweiterungsteile
    dos.writeBytes(Tool.formatErweiterung(this.cErweiterungszeichen));
    // 2. Satzabschnitt
    if (this.cErweiterungszeichen >= 1)
    {
      dos.writeBytes(this.getErweiterungsteil(1));
    }
    else
    {
      dos.writeBytes(Tool.space(29));
    }
    if (this.cErweiterungszeichen >= 2)
    {
      dos.writeBytes(this.getErweiterungsteil(2));
    }
    else
    {
      dos.writeBytes(Tool.space(29));
    }
    dos.writeBytes(Tool.space(11));
    ausgebenErweiterungsteile(dos, 3);
    ausgebenErweiterungsteile(dos, 7);
    ausgebenErweiterungsteile(dos, 11);
  }

  private void ausgebenErweiterungsteile(DataOutputStream dos, int pos)
      throws IOException
  {
    if (this.cErweiterungszeichen >= pos)
    {
      for (int i = pos; i < pos + 4; i++)
      {
        String vzweck = this.getErweiterungsteil(i);
        if (vzweck != null)
        {
          dos.writeBytes(vzweck);
        }
        else
        {
          dos.writeBytes(Tool.space(29));
        }
      }
      dos.writeBytes(Tool.space(12));
    }
  }

  public boolean isOK() throws DtausException
  {
    if (!cBlzEndbeguenstigtSet)
    {
      throw new DtausException(DtausException.C_BLZENDBEGUENSTIGT_FEHLERHAFT);
    }
    if (!cKontoSet)
    {
      throw new DtausException(DtausException.C_KONTONUMMER_FEHLERHAFT);
    }
    if (!cInterneKundennummerSet)
    {
      throw new DtausException(DtausException.C_INTERNEKUNDENNUMMER_FEHLERHAFT);
    }
    if (!cTextschluesselSet)
    {
      throw new DtausException(DtausException.C_TEXTSCHLUESSEL_FEHLERHAFT);
    }
    if (!cErstbeauftragtesInstitutSet)
    {
      throw new DtausException(DtausException.C_BLZERSTBETEILIGT_FEHLERHAFT);
    }
    if (!cKontoAuftraggeberSet)
    {
      throw new DtausException(DtausException.C_KONTOAUFTRAGGEBER_FEHLERHAFT);
    }
    if (!cBetragSet)
    {
      throw new DtausException(DtausException.C_BETRAG_FEHLERHAFT);
    }
    if (!cNameEmpfaengerSet)
    {
      throw new DtausException(DtausException.C_NAME_EMPFAENGER);
    }
    if (!cNameAbsenderSet)
    {
      throw new DtausException(DtausException.C_NAME_ABSENDER);
    }
    if (!cVerwendungszweckSet)
    {
      throw new DtausException(DtausException.C_VERWENDUNGSZWECK_FEHLERHAFT);
    }
    return true;
  }

  public String toString()
  {
    String ret = "Satzlänge=" + this.getSatzlaenge() + ", BLZ erstbeteiligt="
        + this.getBlzErstbeteiligt() + ", BLZ endbegünstigt="
        + this.getBlzEndbeguenstigt() + ", Kontonummer="
        + this.getKontonummer() + ", interne Kundennummer="
        + this.getInterneKundennummer() + ", Textschluessel="
        + this.getTextschluessel() + ", erstbeauftragtes Institut="
        + this.getErstbeauftragtesInstitut() + ", Konto Auftraggeber="
        + this.getKontoAuftraggeber() + ", Betrag=" + this.getBetragInCent()
        + ", Name Empfänger=" + this.getNameEmpfaenger() + ", Name Empfänger2="
        + this.getNameEmpfaenger2() + ", Name Absender="
        + this.getNameAbsender() + ", Name Absender2="
        + this.getNameAbsender2() + ", Erweiterungszeichen="
        + this.getErweiterungszeichen();
    for (int i = 1; i <= this.getAnzahlVerwendungszwecke(); i++)
    {
      ret += ", Verwendungszweck[" + (i) + "]=" + getVerwendungszweck(i);
    }
    return ret;
  }

  public static void main(String[] args) throws DtausException
  {
    CSatz c = new CSatz();
    c.setTextschluessel("53009");
  }
}
/*
 * $Log: CSatz.java,v $
 * Revision 1.17  2008/04/21 18:15:14  jost
 * Neue Textschluessel
 * Revision 1.16 2008/02/08 18:44:46 jost Bugfix
 * Erweiterungsteile Revision 1.15 2007/11/15 20:01:22 jost PrÃ¼fung der
 * TextschlÃ¼ssel erweitert. Revision 1.14 2007/10/29 18:17:08 jost trim()
 * eingebaut
 * 
 * Revision 1.13 2007/09/18 17:49:47 jost ÃberflÃ¼ssige throws und castings
 * entfernt. Revision 1.12 2007/07/17 19:24:43 jost Bugfix in der Methode
 * setBetragInEuro(double) Revision 1.11 2007/05/15 13:29:28 jost Bugfix
 * Erweiterungsteile Revision 1.10 2007/03/19 14:59:04 jost Bugfix bei der
 * PrÃ¼fung der TextschlÃ¼ssel Revision 1.9 2007/03/19 08:53:35 jost
 * TextschlÃ¼ssel fÃ¼r Bankzwecke zugelassen. Revision 1.8 2007/02/22 18:39:39
 * jost Implementierung der Erweiterungsteile 01 (Name
 * EmpfÃ¤nger/Zahlungspflichtiger 2) und 03 (Absender/ZahlungsempfÃ¤nger 2)
 * Revision 1.7 2007/01/07 20:42:18 jost Verwendungszwecke der LÃ¤nge 0
 * zugelassen.
 * 
 * Revision 1.6 2006/10/06 12:44:57 jost Optionale Fehlertoleranz Revision 1.5
 * 2006/08/28 19:01:32 jost Korrekte Behandlung von Groß-Kleinschreibung und
 * ÄÖÜß Revision 1.4 2006/06/05 09:34:36 jost Erweiterungen f. d.
 * DtausDateiWriter Revision 1.3 2006/05/29 16:37:37 jost Anpassungen für den
 * Einsatz in Hibiscus Revision 1.2 2006/05/25 20:30:05 jost Alle
 * Erweiterungsteile können jetzt verarbeitet werden. Revision 1.1 2006/05/24
 * 16:24:44 jost Prerelease
 * 
 */
