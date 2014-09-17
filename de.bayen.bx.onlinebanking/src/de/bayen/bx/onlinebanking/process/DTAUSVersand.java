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
package de.bayen.bx.onlinebanking.process;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MBPBankAccount;
import org.compiere.model.MBankAccount;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;

import de.bayen.bx.onlinebanking.hbci.HBCIPaymentProcessor;
import de.bayen.bx.util.AbstractSvrProcess;
import de.jost_net.OBanToo.Dtaus.CSatz;
import de.jost_net.OBanToo.Dtaus.DtausDateiParser;

/**
 * Für eine Übergangszeit können mit diesem Prozeß DTAUS-Dateien versendet
 * werden. Diese fallen z.B. an, wenn eine ältere externe Software diese noch
 * produziert. Die Geschäftspartner müssen in iDempiere angelegt und mit
 * ordentlichen SEPA-Daten versehen sein. Dieser Prozeß ordnet die Kontonummern
 * dann diesen SEPA-Daten zu und versendet die Daten per HBCI.
 * 
 * @author tbayen
 */
public class DTAUSVersand extends AbstractSvrProcess {

	static public interface Parameter {
		MBankAccount getC_BankAccount();

		String getFileName();
	}

	@Override
	protected Class<?>[] getParameterInterfaces() {
		return new Class[] { Parameter.class };
	}

	@Override
	protected String doIt() throws Exception {
		super.doIt();
		Properties ctx = getCtx();
		String trxName = get_TrxName();
		Parameter bean = (Parameter) getParameterBean();
		MBankAccount bankAccount = bean.getC_BankAccount();
		String fileName = bean.getFileName() == null ? "/tmp/DTAUS.TXT" : bean.getFileName();

		HBCIPaymentProcessor pp = new HBCIPaymentProcessor();

		FileInputStream istrm = new FileInputStream(fileName);
		DtausDateiParser parser = new DtausDateiParser(istrm, DtausDateiParser.FALSCHESWAEHRUNGSKENNZEICHENERLAUBT);
		boolean isDebit = parser.getASatz().getGutschriftLastschrift().charAt(0) == 'L';
		pp.init(getCtx(), bankAccount, isDebit, this.processUI, get_TrxName());

		CSatz csatz;
		do {
			csatz = parser.next();
			if (csatz == null)
				break;

			long blz = csatz.getBlzEndbeguenstigt();
			long kontonummer = csatz.getKontonummer();
			{
				// lex di marino und oruz:
				// if
				if(csatz.getNameEmpfaenger().startsWith("MARINO DI ANNA MARIA")){
					blz = 32070024;
				}
				// lex oruz
				if (csatz.getNameEmpfaenger().startsWith("ORUZ JENTGESALLEE")){
					blz = 32070024;
					kontonummer = 58946500;
				}
				// lex giesecke
				if (csatz.getNameEmpfaenger().startsWith("GIESECKE  UWE ET KEMPSCHE")){
					blz = 32070080;
				}
				if (csatz.getNameEmpfaenger().startsWith("UHLEN KLAUS STEINSTR")){
					blz = 32070080;
				}
			}

			// System.out.println(csatz.toString());
			String sql = "SELECT C_BP_BankAccount_ID FROM C_BP_BankAccount "
					+ "LEFT JOIN C_Bank ON(C_Bank.C_Bank_ID=C_BP_BankAccount.C_Bank_ID) "
					+ "LEFT JOIN C_BPartner ON(C_BPartner.C_BPartner_ID=C_BP_BankAccount.C_BPartner_ID) WHERE "
					+ "C_BP_BankAccount.AccountNo = ? AND "
					+ "C_Bank.RoutingNo = ? AND C_BP_BankAccount.IsActive = 'Y' AND C_BPartner.IsActive = 'Y' ";
			CPreparedStatement stat = DB.prepareStatement(sql, trxName);
			stat.setString(1, Long.toString(kontonummer));

			stat.setString(2, Long.toString(blz));
			ResultSet rs = stat.executeQuery();
			boolean gefunden = rs.next();
			if (!gefunden) {
				throw new AdempiereException("Konto kann nicht gefunden werden! : " + csatz.getNameEmpfaenger() + "/"
						+ csatz.getKontonummer() + "/" + blz);
			}
			Integer accountID = rs.getInt(1);
			MBPBankAccount kontoGefunden = new MBPBankAccount(ctx, accountID, trxName);
			if (rs.next()) {
				// kann vorkommen, wenn jemand zwei Objekte hat. Ich logge es
				// aber hier.
				System.out.println("Konto doppelt gefunden : " + csatz.getNameEmpfaenger() + "/"
						+ csatz.getKontonummer() + "/" + blz);
			}

			// Verwendungszweck zusammenbauen
			String usage = "";
			for (int i = 0; i < csatz.getAnzahlVerwendungszwecke(); i++)
				usage += csatz.getVerwendungszweck(i + 1);

			// und fertig zum Absenden!
			pp.addTransaction(kontoGefunden, usage, Long.toString(new Date().getTime()),
					new BigDecimal(BigInteger.valueOf(csatz.getBetragInCent()), 2));

		} while (csatz != null);

		String error = pp.isOK();
		if (error != null) {
			rollback();
			throw new AdempiereException(error);
		}

		error = pp.process();
		if (error == null) {
			return "";
		} else {
			/*
			 * Trotzdem ordentlich verlassen und kein rollback, da Teile der
			 * Zahlungen durchgegangen sein könnten. Dieser Fall ist hier zwar
			 * nicht schön gelöst, aber so hat man wenigstens die Logs in der
			 * EftJob-Tabelle und kann versuchen, von Hand etwas intelligentes
			 * zu tun.
			 */
			return error;
		}

	}
}
