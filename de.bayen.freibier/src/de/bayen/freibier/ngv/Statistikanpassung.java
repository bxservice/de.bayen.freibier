package de.bayen.freibier.ngv;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.compiere.model.MProduct;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;

import de.bayen.freibier.model.I_BAY_StatAdjust;
import de.bayen.freibier.model.I_BAY_Statistikperiode;
import de.bayen.freibier.model.X_BAY_StatAdjust;
import de.bayen.freibier.model.X_BAY_Umsatzstatistik;
import de.bayen.util.ResourceLoader;

/**
 * Führt die Anpassungen in BAY_StatAdjust aus.
 * 
 * Eigentlich gehört das meiste Gesummsel hier in eine eigene Klasse
 * MStatAdjust, aber hier ist es beim Entwickeln einfacher, weil diese Klasse
 * womöglich besser von Exlipse zur Laufzeit ausgetauscht werden kann.
 * 
 * @author tbayen
 * 
 */
// TODO der ganze Kram berechnet keine Eurowerte, sondern nur Stückzahlen
// TODO Was ist mit AD_Client_ID?
// TODO was macht die SQL-Security-Schicht eigentlich damit?
public class Statistikanpassung extends SvrProcess {

	// Konstanten
	/**
	 * Gleichlautend mit der Referenz BAY_Anpassungsregel
	 * 
	 * @author tbayen
	 */
	static public enum commands {
		addieren, löschen, mindestwert, umbuchen
	};

	static public enum uom {
		hl, proz, stk
	};

	private int periode_id;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] params = getParameter();
		for (ProcessInfoParameter parameter : params) {
			if (I_BAY_Statistikperiode.COLUMNNAME_BAY_Statistikperiode_ID.equals(parameter
					.getParameterName())) {
				periode_id = parameter.getParameterAsInt();
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		String sql = ResourceLoader.loadSQL(this, "findeUmsatzauswahl");
		CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
		stmt.setInt(1, periode_id);

		ResultSet rs = stmt.executeQuery();
		int count = 0;
		while (rs.next()) {
			X_BAY_StatAdjust umsatzauswahl = new X_BAY_StatAdjust(getCtx(), rs.getInt(1),
					get_TrxName());
			System.out.println("Regel "+umsatzauswahl.getValue()+": "+umsatzauswahl.getName());
			final String anweisung = umsatzauswahl.getCommand();
			if (commands.umbuchen.name().equals(anweisung)) {
				count += umbuchenKunde(umsatzauswahl);
			} else if (commands.addieren.name().equals(anweisung)) {
				count += addierenEinArtikel(umsatzauswahl);
			} else if (commands.mindestwert.name().equals(anweisung)) {
				count += aufMindestwertJeArtikel(umsatzauswahl);
			} else if (commands.löschen.name().equals(anweisung)) {
				count += loeschen(umsatzauswahl);
			} else {
				throw new RuntimeException("unbekannte Anweisung: " + anweisung);
			}
			commitEx();
		}
		return "Datensätze verändert: " + count;
	}

	/**
	 * Umbuchen eines Kunden oder eine Gruppe von Kunden auf einen anderen
	 * Kunden. So können z.B. alle Privatkunden auf einer Nummer zusammengefasst
	 * werden.
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected int umbuchenKunde(I_BAY_StatAdjust umsatzauswahl) throws SQLException {
		String sql = ResourceLoader.loadSQL(this, "sucheZeilen");
		CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
		stmt.setInt(1, periode_id);
		stmt.setInt(2, umsatzauswahl.getBAY_StatAdjust_ID());
		ResultSet rs = stmt.executeQuery();
		int count = 0;
		while (rs.next()) {
			X_BAY_Umsatzstatistik alteZeile = new X_BAY_Umsatzstatistik(getCtx(), rs, get_TrxName());
			/*
			 * Zuerst suche ich, ob es bereits einen Eintrag gibt, der auf den
			 * angegebenen Kunden und Artikel passt.
			 */
			if (alteZeile.getC_BPartner_ID() == umsatzauswahl.getTargetCustomer_ID()) {
				/*
				 * Das Ziel selber kann auch Teil der Zielgruppe sein, dann
				 * brauche ich natürlich nicht zu kopieren.
				 */
				continue;
			}
			String sql2 = "SELECT * FROM BAY_Umsatzstatistik "
					+ "WHERE BAY_Statistikperiode_ID=? AND M_Product_ID=? AND C_BPartner_ID=?";
			CPreparedStatement stmt2 = DB.prepareStatement(sql2, get_TrxName());
			stmt2.setInt(1, periode_id);
			stmt2.setInt(2, alteZeile.getM_Product_ID());
			stmt2.setInt(3, umsatzauswahl.getTargetCustomer_ID());
			ResultSet rs2 = stmt2.executeQuery();
			X_BAY_Umsatzstatistik umsatz;
			if (rs2.next()) {
				// ein passender Datensatz existiert bereits
				umsatz = new X_BAY_Umsatzstatistik(getCtx(), rs2, get_TrxName());
			} else {
				// ein passender Datensatz existiert noch nicht
				umsatz = new X_BAY_Umsatzstatistik(getCtx(), 0, get_TrxName());
				umsatz.setAD_Org_ID(alteZeile.getAD_Org_ID());
				umsatz.setBAY_Statistikperiode_ID(periode_id);
				umsatz.setC_BPartner_ID(umsatzauswahl.getTargetCustomer_ID());
				umsatz.setM_Product_ID(alteZeile.getM_Product_ID());
			}
			umsatz.setLieferungen(umsatz.getLieferungen() + alteZeile.getLieferungen());
			umsatz.setStueck(umsatz.getStueck().add(alteZeile.getStueck()));
			umsatz.setStueckGratis(umsatz.getStueckGratis().add(alteZeile.getStueckGratis()));
			umsatz.setUmsatz(umsatz.getUmsatz().add(alteZeile.getUmsatz()));
			umsatz.setUmsatzEK(umsatz.getUmsatzEK().add(alteZeile.getUmsatzEK()));
			umsatz.saveEx();
			// alten Eintrag löschen
			alteZeile.deleteEx(false);
			// commit?
			count++;
		}
		rs.close();
		stmt.close();
		return count;
	}

	protected int addierenEinArtikel(I_BAY_StatAdjust umsatzauswahl) throws SQLException {
		// FIXME für welchen Zeitraum gilt diese Regel?
		int count = 0;
		// vorhandene Umsatzzeilen suchen und ändern
		{
			String sql = ResourceLoader.loadSQL(this, "sucheZeilen");
			CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
			stmt.setInt(1, periode_id);
			stmt.setInt(2, umsatzauswahl.getBAY_StatAdjust_ID());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				X_BAY_Umsatzstatistik alteZeile = new X_BAY_Umsatzstatistik(getCtx(), rs,
						get_TrxName());
				/**
				 * Hinzufügen laut Regel:
				 */
				// stueck, hl, prozent
				String masseinheit = umsatzauswahl.getCommandUom();
				BigDecimal deltawert = umsatzauswahl.getAmount();
				if (uom.proz.name().equals(masseinheit)) {
					BigDecimal proz = BigDecimal.ONE.add(umsatzauswahl.getAmount()
							.scaleByPowerOfTen(-2));
					BigDecimal neuerWert = alteZeile.getStueck().multiply(proz)
							.setScale(0, RoundingMode.HALF_UP);
					alteZeile.setStueck(neuerWert);
				} else if (uom.stk.name().equals(masseinheit)) {
					alteZeile.setStueck(alteZeile.getStueck().add(deltawert));
				} else if (uom.hl.name().equals(masseinheit)) {
					MProduct artikel = new MProduct(getCtx(), alteZeile.getM_Product_ID(),
							get_TrxName());
					BigDecimal hlProGebinde = artikel.getVolume();
					BigDecimal stueck = deltawert.divide(hlProGebinde.scaleByPowerOfTen(-2),
							RoundingMode.UP).setScale(0, RoundingMode.UP);
					alteZeile.setStueck(alteZeile.getStueck().add(stueck));
				} else {
					throw new RuntimeException("unbekannte Masseinheit");
				}
				if (alteZeile.getStueck().equals(BigDecimal.ZERO))
					alteZeile.delete(false);
				else
					alteZeile.saveEx();
				count++;
			}
			rs.close();
			stmt.close();
		}

		// nicht vorhandene
		{
			String sql = ResourceLoader.loadSQL(this, "sucheNichtvorhandeneZeilen");
			CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
			stmt.setInt(1, periode_id);
			stmt.setInt(2, umsatzauswahl.getBAY_StatAdjust_ID());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				final int myProduct_ID = rs.getInt("M_Product_ID");
				/*
				 * Mit der Neuprogrammierung weiss ich nicht mehr, ob das noch
				 * so ist. Ich kommentiere es aus und wenn das hier beim Testen
				 * keine Exception gibt, kann es wahrscheinlich dann weg...:
				 */
				// if (myProduct_ID < 1) {
				// /**
				// * Wenn ich die Umsätze eines Kunden pauschal prozentual
				// * verändern möchte und dabei keinen Artikel angebe (um alle
				// * Artikel gleich zu behandeln), gibt es einen Sonderfall,
				// * wenn der Kunde in diesem Monat gar nichts bezieht. Dann
				// * entsteht eine einzelne Ergebniszeile, in der alles NULL
				// * ist ausser dem bpartner. Dann brechen wir das hier am
				// * besten ab.
				// */
				// continue;
				// }
				X_BAY_Umsatzstatistik neueZeile = new X_BAY_Umsatzstatistik(getCtx(), 0,
						get_TrxName());
				neueZeile.setAD_Org_ID(umsatzauswahl.getAD_Org_ID());
				neueZeile.setBAY_Statistikperiode_ID(periode_id);
				neueZeile.setC_BPartner_ID(rs.getInt("C_BPartner_ID"));
				neueZeile.setM_Product_ID(myProduct_ID);
				/**
				 * Hinzufügen laut Regel:
				 */
				// stueck, hl, prozent
				String masseinheit = umsatzauswahl.getCommandUom();
				BigDecimal deltawert = umsatzauswahl.getAmount();
				if ("prozent".equals(masseinheit)) {
					/*
					 * Eine neu angelegte Statistikzeile kann nicht erhöht
					 * werden, also mache ich hier nichts.
					 */
					continue;
				} else if ("stueck".equals(masseinheit)) {
					neueZeile.setStueck(deltawert);
				} else if ("hl".equals(masseinheit)) {
					MProduct artikel = new MProduct(getCtx(), myProduct_ID, get_TrxName());
					BigDecimal hlProGebinde = artikel.getVolume();
					BigDecimal stueck = deltawert.divide(hlProGebinde.scaleByPowerOfTen(-2),
							RoundingMode.UP).setScale(0, RoundingMode.UP);
					neueZeile.setStueck(stueck);
				} else {
					throw new RuntimeException("unbekannte Masseinheit");
				}
				if (!neueZeile.getStueck().equals(BigDecimal.ZERO))
					neueZeile.saveEx();
				count++;
			}
			rs.close();
			stmt.close();
		}

		return count;
	}

	protected int aufMindestwertJeArtikel(I_BAY_StatAdjust umsatzauswahl) throws SQLException {
		// FIXME für welchen Zeitraum gilt diese Regel?
		int count = 0;
		// vorhandene Umsatzzeilen suchen und ändern
		{
			String sql = ResourceLoader.loadSQL(this, "sucheZeilen");
			CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
			stmt.setInt(1, periode_id);
			stmt.setInt(2, umsatzauswahl.getBAY_StatAdjust_ID());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				X_BAY_Umsatzstatistik alteZeile = new X_BAY_Umsatzstatistik(getCtx(), rs,
						get_TrxName());
				/**
				 * Hinzufügen laut Regel:
				 */
				// stueck, hl, prozent
				String masseinheit = umsatzauswahl.getCommandUom();
				BigDecimal deltawert = umsatzauswahl.getAmount();
				if (uom.proz.name().equals(masseinheit)) {
					throw new RuntimeException("Mindestwert kann nicht prozentual sein");
				} else if (uom.stk.name().equals(masseinheit)) {
					if (alteZeile.getStueck().compareTo(deltawert) < 0)
						alteZeile.setStueck(deltawert);
				} else if (uom.hl.name().equals(masseinheit)) {
					MProduct artikel = new MProduct(getCtx(), alteZeile.getM_Product_ID(),
							get_TrxName());
					BigDecimal hlProGebinde = artikel.getVolume().scaleByPowerOfTen(-2);
					if (hlProGebinde.compareTo(BigDecimal.ZERO) == 0)
						throw new RuntimeException("Artikel nicht in Hektoliter ausdrückbar: "
								+ artikel.getValue() + "[" + artikel.getName() + "] - Regel '"
								+ umsatzauswahl.getName() + "'");
					BigDecimal hlVorher = alteZeile.getStueck().divide(hlProGebinde,
							RoundingMode.HALF_UP);
					if (hlVorher.compareTo(deltawert) < 0) {
						BigDecimal neuStueck = deltawert.divide(hlProGebinde, RoundingMode.HALF_UP)
								.setScale(0, RoundingMode.UP);
						alteZeile.setStueck(neuStueck);
					}
					BigDecimal stueck = deltawert.divide(hlProGebinde, RoundingMode.UP).setScale(0,
							RoundingMode.UP);
					alteZeile.setStueck(alteZeile.getStueck().add(stueck));
				} else {
					throw new RuntimeException("unbekannte Masseinheit");
				}
				if (alteZeile.getStueck().equals(BigDecimal.ZERO))
					alteZeile.delete(false);
				else
					alteZeile.saveEx();
				count++;
			}
			rs.close();
			stmt.close();
		}

		// nicht vorhandene
		{
			String sql = ResourceLoader.loadSQL(this, "sucheNichtvorhandeneZeilen");
			CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
			stmt.setInt(1, periode_id);
			stmt.setInt(2, umsatzauswahl.getBAY_StatAdjust_ID());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				X_BAY_Umsatzstatistik neueZeile = new X_BAY_Umsatzstatistik(getCtx(), 0,
						get_TrxName());
				neueZeile.setAD_Org_ID(umsatzauswahl.getAD_Org_ID());
				neueZeile.setBAY_Statistikperiode_ID(periode_id);
				neueZeile.setC_BPartner_ID(rs.getInt("myBPartner_ID"));
				final int myProduct_ID = rs.getInt("myProduct_ID");
				neueZeile.setM_Product_ID(myProduct_ID);
				/**
				 * Hinzufügen laut Regel:
				 */
				// stueck, hl, prozent
				String masseinheit = umsatzauswahl.getCommandUom();
				BigDecimal deltawert = umsatzauswahl.getAmount();
				if ("prozent".equals(masseinheit)) {
					throw new RuntimeException("Mindestwert kann nicht prozentual sein");
				} else if ("stueck".equals(masseinheit)) {
					neueZeile.setStueck(deltawert);
				} else if ("hl".equals(masseinheit)) {
					MProduct artikel = new MProduct(getCtx(), myProduct_ID, get_TrxName());
					BigDecimal hlProGebinde = artikel.getVolume().scaleByPowerOfTen(-2);
					BigDecimal stueck = deltawert.divide(hlProGebinde, RoundingMode.HALF_UP)
							.setScale(0, RoundingMode.UP);
					neueZeile.setStueck(stueck);
				}
				if (!neueZeile.getStueck().equals(BigDecimal.ZERO))
					neueZeile.saveEx();
				count++;
			}
			rs.close();
			stmt.close();
		}
		return count;
	}

	protected int loeschen(I_BAY_StatAdjust umsatzauswahl) throws SQLException {
		String sql = ResourceLoader.loadSQL(this, "sucheZeilen");
		CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
		stmt.setInt(1, periode_id);
		stmt.setInt(2, umsatzauswahl.getBAY_StatAdjust_ID());
		ResultSet rs = stmt.executeQuery();
		int count = 0;
		while (rs.next()) {
			X_BAY_Umsatzstatistik alteZeile = new X_BAY_Umsatzstatistik(getCtx(), rs, get_TrxName());
			// alten Eintrag löschen
			System.out.println("löschen Zeile "+alteZeile.get_ID());
			/*
			 * Ich frage hier keinen Fehler ab, da es doppelte Zeilen geben
			 * kann, wenn ein Artikel z.B. zwei Hersteller hat. Der wird dann
			 * ggf. zweimal gelöscht, was natürlich nicht geht.
			 */
			boolean erg = alteZeile.delete(false);
			if (!erg)
				System.out.println("Problem beim löschen von " + alteZeile.get_ID()
						+ ". Kann bei Artikeln mit zwei Herstellern normal sein.");
			count++;
		}
		rs.close();
		stmt.close();
		return count;
	}

}
