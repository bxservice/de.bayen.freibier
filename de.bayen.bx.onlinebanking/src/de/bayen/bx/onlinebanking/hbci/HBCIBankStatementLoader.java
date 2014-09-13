/******************************************************************************
 * Copyright (C) 2014 Thomas Bayen                                            *
 * Copyright (C) 2014 Jakob Bayen KG             							  *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package de.bayen.bx.onlinebanking.hbci;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.compiere.impexp.BankStatementLoaderInterface;
import org.compiere.model.MBankStatementLoader;
import org.compiere.model.Query;

import de.bayen.bx.onlinebanking.model.I_BAY_MT940;
import de.bayen.bx.onlinebanking.model.X_BAY_MT940;
import de.bayen.bx.util.IBANUtil;


/**
 * BankStatementLoader to load Bank Statement out of the MT940 table.
 * 
 * You can use this BankStatementLoader by configuring it in the "Bank" Window
 * in Tab "Bank Statement Loader". Use
 * "de.bayen.freibier.hbci.HBCIBankStatementLoader" as the Loader class name.
 * You have to configure it by filling the following fields:
 * 
 * PinTan:
 * 
 * UserID: Enter your Volksbank "VR Kennung" (or similar ID for other banks) If
 * your user-ID and Customer-ID is different (depends on the bank), you can use
 * the syntax "UserID:CustomerID".
 * 
 * PIN: You do not have to enter it. If it is empty then iDempiere will ask you
 * everytime it is needed.
 * 
 * RDH:
 * 
 * host address ("Serveradresse"): Set to "RDHNew;300" for a rdh passport
 * ("Schlüsseldiskette") and HBCI Version 300 (this is the recent version for
 * SEPA).
 * 
 * filename: This points to the RDH file.
 * 
 * You have to start first the Process MT940Import which uses the given
 * credentials to get the MT940 statement data from the bank into the table
 * BAY_MT940. Then - in a second step - you start this StatementLoader to take
 * the data from the table BAY_MT940 into the iDempiere Import table for Bank
 * Statements.
 * 
 * @author tbayen
 */
public class HBCIBankStatementLoader implements BankStatementLoaderInterface {

	private String lastErrorMessage = null;
	private String lastErrorDescription = null;
	private Timestamp dateLastRun;

	private MBankStatementLoader controller;
	private X_BAY_MT940 mt940line;

	@Override
	public boolean init(MBankStatementLoader controller) {
		this.controller = controller;
		return true;
	}

	protected String getStackTraceAsString(Exception ex) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		ex.printStackTrace(writer);
		writer.close();
		return stringWriter.toString();
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean loadLines() {
		String trxName = controller.get_TrxName();
		// @formatter:off
		String whereClause=
			X_BAY_MT940.COLUMNNAME_Processed + "='N'"
			+ " AND " + X_BAY_MT940.COLUMNNAME_C_BankAccount_ID + "=?"
		;
		// @formatter:on
		Query query = new Query(controller.getCtx(), X_BAY_MT940.Table_Name, whereClause, trxName);
		query.setParameters(controller.getC_BankAccount_ID());
		List<X_BAY_MT940> newLines = query.list();
		for (X_BAY_MT940 line : newLines) {
			mt940line = line;
			controller.saveLine();
			line.setProcessed(true);
			line.saveEx(trxName);
			Timestamp datum = line.getStatementDate();
			if (controller.getDateLastRun() != null && controller.getDateLastRun().getTime() < datum.getTime()) {
				controller.setDateLastRun(datum);
				controller.saveEx(trxName);
			}
		}
		return true;
	}

	@Override
	public String getLastErrorMessage() {
		return lastErrorMessage;
	}

	@Override
	public String getLastErrorDescription() {
		return lastErrorDescription;
	}

	@Override
	public Timestamp getDateLastRun() {
		return dateLastRun;
	}

	@Override
	public String getRoutingNo() {
		return mt940line.getC_BankAccount().getC_Bank().getRoutingNo();
	}

	@Override
	public String getBankAccountNo() {
		return mt940line.getC_BankAccount().getAccountNo();
	}

	@Override
	public String getStatementReference() {
		/*
		 * Soweit ich das verstanden habe, ist das eine Referenz, die sich auf
		 * den Kontoauszug bezieht, also z.B. eine Auszugsnummer. Sowas gibt mir
		 * HBCI4Java aber nicht.
		 */
		return null;
	}

	@Override
	public Timestamp getStatementDate() {
		return mt940line.getStatementDate();
	}

	@Override
	public String getTrxID() {
		/*
		 * Ich weiss nicht so recht, welchen Wert aus dem MT940-Datensatz man
		 * hier am besten übernimmt. Das Feld
		 * 
		 * Sollte es noch einen interesanten Wert geben, mit dem ein Matcher
		 * etwas anfangen kann, kann man den hier übergeben. Bis es hier ein
		 * gutes Argument für einen anderen Wert gibt, nehme ich einstweilen das
		 * Primanota-Feld. Ich glaube, das ist eine Bankinterne Nummer und daher
		 * eigentlich völlig unnütz für unsere Zwecke, aber sonst ist mir auch
		 * nichts nützliches aufgefallen.
		 */
		return mt940line.getReference();
	}

	@Override
	public String getReference() {
		/*
		 * Das Reference-Feld im MT940-Datensatz enthält immer null oder eine
		 * Zahl, der ich keine Bedeutung zuordnen kann. Es könnte eine ID der
		 * Bank für die Zahlung oder sowas sein. Diesen Wert speichere ich im
		 * Bankauszug als TrxID.
		 * 
		 * Dahingegen gibt es zwei Felder EREF und KREF, die Daten enthalten,
		 * die evtl. vom Kunden ausgefüllt werden können. Bei KREF bin ich mir
		 * nicht ganz sicher, wo der Wert herkommt, im EREF-Feld steht
		 * allerdings oft auch z.B. unsere Rechnungs- und Kundennummer. Das Feld
		 * ist also wahrscheinlich das, das für die Angabe genau solch einer
		 * Nummer vorgesehen ist. Man könnte es an das Memo-Feld, das den
		 * restlichen Verwendungszweck enthält, anhängen. Ich habe mich
		 * allerdings entschlossen, getrennte Daten so lange wie möglich
		 * getrennt zu halten. Man weiss nie, wofür es gut ist. Der KREF-Wert
		 * kommt sehr selten vor und enthielt auch bisher keine nützlichen
		 * Daten. Den lasse ich erstmal unter den Tisch fallen. Man könnte ihn
		 * hier auch anhängen.
		 */
		return mt940line.getEREF();
	}

	@Override
	public String getCheckNo() {
		/*
		 * Hier hätte man noch ein leeres Feld, um Daten zu übertragen. Ich habe
		 * allerdings bisher nichts sinnvolles gefunden.
		 * 
		 * Dieses Feld ist im notmalen iDempiere nur 20 Zeichen breit. Wer mehr
		 * Daten übertragen will, muss es vorher verbreitern.
		 */
		return null;
	}

	@Override
	public String getPayeeName() {
		return mt940line.getBPName();
	}

	@Override
	public String getPayeeAccountNo() {
		/*
		 * Dieses Feld sollte die Kontonummer des Zahlenden in der Auszugszeile
		 * enthalten. Diese wird später zum Matchen mit meinen Geschäftspartnern
		 * benutzt. Leider gibt es kein Feld für die BLZ, so das ich mich hier
		 * entscheiden musste, ob ich das Risiko eingehe, das mehrere
		 * Geschäftspartner das gleiche Konto bei einer anderen Bank haben oder
		 * ob ich den Wert verhackstücke. Da das hier früher oder später
		 * bestimmt IBANs enthalten wird, kann es allerdings nicht wirklich
		 * schaden, wenn ich schon jetzt dazu übergehe, Kontonummer und BLZ
		 * einfach aneinanderzuhängen (etwas ähnliches stellt die IBAN ja auch
		 * dar). Sollten hier später wirklich IBANs stehen, dann fangen die zur
		 * Unterscheidung mit zwei Buchstaben an.
		 */
		String konto = mt940line.getBPAccountNo();
		String blz = mt940line.getBPRoutingNo();
		String erg = konto;
		if (erg == null || erg.length() == 0)
			return null;
		if (blz != null && !IBANUtil.isIBAN(erg)) {
			erg = blz + "/" + erg;
		}
		return erg;
	}

	@Override
	public Timestamp getStatementLineDate() {
		return mt940line.getStatementDate();
	}

	@Override
	public Timestamp getValutaDate() {
		return mt940line.getValutaDate();
	}

	@Override
	public String getTrxType() {
		/*
		 * Bezeichnung des Geschäftsvorfalls zusammen mit der GV-Nummer. Hier
		 * steht dann z.B. "051: Überweisung-Gutschrift" oder
		 * "009: Rücklastschrift".
		 */
		String gvcode = mt940line.getGVCode();
		String art = mt940line.getGVText();
		String erg = gvcode;
		if (erg != null)
			erg += ": ";
		else
			erg = "";
		if (art != null)
			erg += art;
		if (erg.length() > 20)
			erg = erg.substring(0, 20);
		return erg;
	}

	@Override
	public boolean getIsReversal() {
		/*
		 * Ich habe das praktisch noch nie in einem Auszug gesehen.
		 */
		return mt940line.isReversal();
	}

	@Override
	public String getCurrency() {
		/*
		 * HBCI kann nur deutsch, also sind alle unsere Daten in Euro.
		 */
		return "EUR";
	}

	@Override
	public BigDecimal getStmtAmt() {
		/*
		 * Das hier ist der Wert, der auf dem Auszug draufsteht. Normalerweise
		 * ist das auch gleichzeitig der, der für die entsprechende
		 * Zahlungstransaktion verbucht wird (also TrxAmt). Das kann aber
		 * abweichen, falls man Gebühren oder Zinsen angibt, die dann als
		 * Teilbetrag woanders hingebucht werden. Das entscheide ich aber nicht
		 * als Loader, sondern das muss später ein Matcher regeln.
		 */
		return mt940line.getAmount();
	}

	@Override
	public BigDecimal getTrxAmt() {
		/*
		 * es muss immer gelten:
		 * 
		 * TrxAmt + ChargeAmt + InterestAmt = StmtAmt
		 * 
		 * Ich setze das hier gleich dem StmtAmt-Wert. Ändert man ansonsten
		 * nichts an den Daten dieses Importers, ist dann alles gut. Gibt man
		 * eine Charge ein, so muss diese dann entsprechend abgezogen werden.
		 */
		return getStmtAmt().subtract(getChargeAmt());
	}

	@Override
	public BigDecimal getInterestAmt() {
		/*
		 * Ein Extra-Feld für Zinsen gibt es offenbar im MT940 Datensatz nicht.
		 */
		return BigDecimal.ZERO;
	}

	@Override
	public String getMemo() {
		/*
		 * Hier ergibt sich theoretisch eine Länge von 14 x 27 = 378 Zeichen.
		 * Das MEmo-Feld hat in iDempiere aber nur 255. Das müsste man also
		 * entweder verbreitern (in I_BankStatement und in C_BankStatement) oder
		 * einsehen, das die Wahnsinnigen, die so lange VWZs versenden, sowieso
		 * keine hilfreichen Daten da reinschreiben. Ein automatischer MAtcher
		 * ist davon überfordert und ein menschlicher Bearbeiter sowieso. Der
		 * wird dann eher ein Begleitschreiben zu Rate ziehen.
		 * 
		 * Zum jetzigen Stand (Auszüge von Ende 2013) kommt das bei mir
		 * praktisch bei der SWK (wegen eines sehr langen SEPA-Erklärtextes) und
		 * der Fako vor. Für mich habe ich mich daher entschlossen, die
		 * Feldlänge nicht zu ändern.
		 */
		return mt940line.getVWZ();
	}

	@Override
	public String getChargeName() {
		return null;
	}

	@Override
	public BigDecimal getChargeAmt() {
		return mt940line.getChargeAmt();
	}
}
