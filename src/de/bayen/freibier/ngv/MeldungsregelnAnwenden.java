//package de.bayen.freibier.process;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import org.compiere.model.MProduct;
//import org.compiere.process.ProcessInfoParameter;
//import org.compiere.process.SvrProcess;
//import org.compiere.util.CPreparedStatement;
//import org.compiere.util.DB;
//
//import de.bayen.freibier.model.I_BAY_Statistikperiode;
//import de.bayen.freibier.model.I_BAY_Umsatzauswahl;
//import de.bayen.freibier.model.X_BAY_Umsatzauswahl;
//import de.bayen.freibier.model.X_BAY_Umsatzstatistik;
//import de.bayen.util.ResourceLoader;
//
//public class MeldungsregelnAnwenden extends SvrProcess {
//
//	private int periode_id;
//
//	@Override
//	protected void prepare() {
//		ProcessInfoParameter[] params = getParameter();
//		for (ProcessInfoParameter parameter : params) {
//			if (I_BAY_Statistikperiode.COLUMNNAME_BAY_Statistikperiode_ID
//					.equals(parameter.getParameterName())) {
//				periode_id = parameter.getParameterAsInt();
//			}
//		}
//	}
//
//	/**
//	 * Setter für periode_id für den Fall, das ich diesen Prozess direkt
//	 * aufrufen will und dazu die Parameter direkt setzen möchte.
//	 * 
//	 * @param periode_id
//	 */
//	public void setPeriode_id(int periode_id) {
//		this.periode_id = periode_id;
//	}
//
//	@Override
//	protected String doIt() throws Exception {
//		String sql = ResourceLoader.loadSQL(this, "findeUmsatzauswahl");
//		CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
//		stmt.setInt(1, periode_id);
//		ResultSet rs = stmt.executeQuery();
//		int count = 0;
//		while (rs.next()) {
//			X_BAY_Umsatzauswahl umsatzauswahl = new X_BAY_Umsatzauswahl(
//					getCtx(), rs.getInt(1), get_TrxName());
//			final String anweisung = umsatzauswahl.getAnweisung();
//			if ("umbuchenKunde".equals(anweisung)) {
//				count += umbuchenKunde(umsatzauswahl);
//			} else if ("addierenEinArtikel".equals(anweisung)) {
//				count += addierenEinArtikel(umsatzauswahl);
//			} else if ("aufMindestwertJeArtikel".equals(anweisung)) {
//				count += aufMindestwertJeArtikel(umsatzauswahl);
//			} else if ("löschen".equals(anweisung)) {
//				count += loeschen(umsatzauswahl);
//			} else {
//				throw new RuntimeException("unbekannte Anweisung: " + anweisung);
//			}
//		}
//		commitEx();
//		return "Datensätze verändert: " + count;
//	}
//
//	/**
//	 * Umbuchen eines Kunden oder eine Gruppe von Kunden auf einen anderen
//	 * Kunden. So können z.B. alle Privatkunden auf einer Nummer zusammengefasst
//	 * werden.
//	 * 
//	 * @return
//	 * @throws SQLException
//	 */
//	protected int umbuchenKunde(I_BAY_Umsatzauswahl umsatzauswahl)
//			throws SQLException {
//		String sql = ResourceLoader.loadSQL(this, "sucheZeilen");
//		CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
//		stmt.setInt(1, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//		stmt.setInt(2, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//		stmt.setInt(3, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//		stmt.setInt(4, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//		stmt.setInt(5, periode_id);
//		ResultSet rs = stmt.executeQuery();
//		int count = 0;
//		while (rs.next()) {
//			X_BAY_Umsatzstatistik alteZeile = new X_BAY_Umsatzstatistik(
//					getCtx(), rs, get_TrxName());
//			/*
//			 * Zuerst suche ich, ob es bereits einen Eintrag gibt, der auf den
//			 * angegebenen Kunden und Artikel passt.
//			 */
//			if (alteZeile.getC_BPartner_ID() == umsatzauswahl
//					.getC_BPartner_ID()) {
//				/*
//				 * Das Ziel selber kann auch Teil der Zielgruppe sein, dann
//				 * brauche ich natürlich nicht zu kopieren.
//				 */
//				continue;
//			}
//			String sql2 = "SELECT * FROM BAY_Umsatzstatistik "
//					+ "WHERE BAY_Statistikperiode_ID=? AND M_Product_ID=? AND C_BPartner_ID=?";
//			CPreparedStatement stmt2 = DB.prepareStatement(sql2, get_TrxName());
//			stmt2.setInt(1, periode_id);
//			stmt2.setInt(2, alteZeile.getM_Product_ID());
//			stmt2.setInt(3, umsatzauswahl.getC_BPartner_ID());
//			ResultSet rs2 = stmt2.executeQuery();
//			X_BAY_Umsatzstatistik umsatz;
//			if (rs2.next()) {
//				// ein passender Datensatz existiert bereits
//				umsatz = new X_BAY_Umsatzstatistik(getCtx(), rs2, get_TrxName());
//			} else {
//				// ein passender Datensatz existiert noch nicht
//				umsatz = new X_BAY_Umsatzstatistik(getCtx(), 0, get_TrxName());
//				umsatz.setAD_Org_ID(alteZeile.getAD_Org_ID());
//				umsatz.setBAY_Statistikperiode_ID(periode_id);
//				umsatz.setC_BPartner_ID(umsatzauswahl.getC_BPartner_ID());
//				umsatz.setM_Product_ID(alteZeile.getM_Product_ID());
//			}
//			umsatz.setLieferungen(umsatz.getLieferungen()
//					+ alteZeile.getLieferungen());
//			umsatz.setStueck(umsatz.getStueck().add(alteZeile.getStueck()));
//			umsatz.setStueckGratis(umsatz.getStueckGratis().add(
//					alteZeile.getStueckGratis()));
//			umsatz.setUmsatz(umsatz.getUmsatz().add(alteZeile.getUmsatz()));
//			umsatz.setUmsatzEK(umsatz.getUmsatzEK()
//					.add(alteZeile.getUmsatzEK()));
//			umsatz.saveEx();
//			// alten Eintrag löschen
//			alteZeile.deleteEx(false);
//			count++;
//		}
//		rs.close();
//		stmt.close();
//		return count;
//	}
//
//	protected int addierenEinArtikel(I_BAY_Umsatzauswahl umsatzauswahl)
//			throws SQLException {
//		// FIXME für welchen Zeitraum gilt diese Regel?
//		int count = 0;
//		// vorhandene Umsatzzeilen suchen und ändern
//		{
//			String sql = ResourceLoader.loadSQL(this, "sucheZeilen");
//			CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
//			stmt.setInt(1, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(2, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(3, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(4, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(5, periode_id);
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				X_BAY_Umsatzstatistik alteZeile = new X_BAY_Umsatzstatistik(
//						getCtx(), rs, get_TrxName());
//				/**
//				 * Hinzufügen laut Regel:
//				 */
//				// stueck, hl, prozent
//				String masseinheit = umsatzauswahl.getMasseinheit();
//				BigDecimal deltawert = umsatzauswahl.getAenderungswert();
//				if ("prozent".equals(masseinheit)) {
//					BigDecimal proz = BigDecimal.ONE.add(umsatzauswahl
//							.getAenderungswert().scaleByPowerOfTen(-2));
//					BigDecimal neuerWert = alteZeile.getStueck().multiply(proz)
//							.setScale(0, RoundingMode.HALF_UP);
//					alteZeile.setStueck(neuerWert);
//				} else if ("stueck".equals(masseinheit)) {
//					alteZeile.setStueck(alteZeile.getStueck().add(deltawert));
//				} else if ("hl".equals(masseinheit)) {
//					MProduct artikel = new MProduct(getCtx(),
//							alteZeile.getM_Product_ID(), get_TrxName());
//					BigDecimal hlProGebinde = artikel.getVolume();
//					BigDecimal stueck = deltawert
//							.divide(hlProGebinde.scaleByPowerOfTen(-2),
//									RoundingMode.UP).setScale(0,
//									RoundingMode.UP);
//					alteZeile.setStueck(alteZeile.getStueck().add(stueck));
//				} else {
//					throw new RuntimeException("unbekannte Masseinheit");
//				}
//				if (alteZeile.getStueck().equals(BigDecimal.ZERO))
//					alteZeile.delete(false);
//				else
//					alteZeile.saveEx();
//				count++;
//			}
//			rs.close();
//			stmt.close();
//		}
//
//		// nicht vorhandene
//		{
//			String sql = ResourceLoader.loadSQL(this,
//					"sucheNichtvorhandeneZeilen");
//			CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
//			stmt.setInt(1, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(2, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(3, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(4, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(5, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(6, periode_id);
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				final int myProduct_ID = rs.getInt("myProduct_ID");
//				if (myProduct_ID < 1) {
//					/**
//					 * Wenn ich die Umsätze eines Kunden pauschal prozentual
//					 * verändern möchte und dabei keinen Artikel angebe (um alle
//					 * Artikel gleich zu behandeln), gibt es einen Sonderfall,
//					 * wenn der Kunde in diesem Monat gar nichts bezieht. Dann
//					 * entsteht eine einzelne Ergebniszeile, in der alles NULL
//					 * ist ausser dem bpartner. Dann brechen wir das hier am
//					 * besten ab.
//					 */
//					continue;
//				}
//				X_BAY_Umsatzstatistik neueZeile = new X_BAY_Umsatzstatistik(
//						getCtx(), 0, get_TrxName());
//				neueZeile.setAD_Org_ID(umsatzauswahl.getAD_Org_ID());
//				neueZeile.setBAY_Statistikperiode_ID(periode_id);
//				neueZeile.setC_BPartner_ID(rs.getInt("myBPartner_ID"));
//				neueZeile.setM_Product_ID(myProduct_ID);
//				/**
//				 * Hinzufügen laut Regel:
//				 */
//				// stueck, hl, prozent
//				String masseinheit = umsatzauswahl.getMasseinheit();
//				BigDecimal deltawert = umsatzauswahl.getAenderungswert();
//				if ("prozent".equals(masseinheit)) {
//					/*
//					 * Eine neu angelegte Statistikzeile kann nicht erhöht
//					 * werden, also mache ich hier nichts.
//					 */
//				} else if ("stueck".equals(masseinheit)) {
//					neueZeile.setStueck(deltawert);
//				} else if ("hl".equals(masseinheit)) {
//					MProduct artikel = new MProduct(getCtx(), myProduct_ID,
//							get_TrxName());
//					BigDecimal hlProGebinde = artikel.getVolume();
//					BigDecimal stueck = deltawert
//							.divide(hlProGebinde.scaleByPowerOfTen(-2),
//									RoundingMode.UP).setScale(0,
//									RoundingMode.UP);
//					neueZeile.setStueck(stueck);
//				} else {
//					throw new RuntimeException("unbekannte Masseinheit");
//				}
//				if (!neueZeile.getStueck().equals(BigDecimal.ZERO))
//					neueZeile.saveEx();
//				count++;
//			}
//			rs.close();
//			stmt.close();
//		}
//
//		return count;
//	}
//
//	protected int aufMindestwertJeArtikel(I_BAY_Umsatzauswahl umsatzauswahl)
//			throws SQLException {
//		// FIXME für welchen Zeitraum gilt diese Regel?
//		int count = 0;
//		// vorhandene Umsatzzeilen suchen und ändern
//		{
//			String sql = ResourceLoader.loadSQL(this, "sucheZeilen");
//			CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
//			stmt.setInt(1, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(2, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(3, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(4, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(5, periode_id);
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				X_BAY_Umsatzstatistik alteZeile = new X_BAY_Umsatzstatistik(
//						getCtx(), rs, get_TrxName());
//				/**
//				 * Hinzufügen laut Regel:
//				 */
//				// stueck, hl, prozent
//				String masseinheit = umsatzauswahl.getMasseinheit();
//				BigDecimal deltawert = umsatzauswahl.getAenderungswert();
//				if ("prozent".equals(masseinheit)) {
//					throw new RuntimeException(
//							"Mindestwert kann nicht prozentual sein");
//				} else if ("stueck".equals(masseinheit)) {
//					if (alteZeile.getStueck().compareTo(deltawert) < 0)
//						alteZeile.setStueck(deltawert);
//				} else if ("hl".equals(masseinheit)) {
//					MProduct artikel = new MProduct(getCtx(),
//							alteZeile.getM_Product_ID(), get_TrxName());
//					BigDecimal hlProGebinde = artikel.getVolume()
//							.scaleByPowerOfTen(-2);
//					if (hlProGebinde.compareTo(BigDecimal.ZERO) == 0)
//						throw new RuntimeException(
//								"Artikel nicht in Hektoliter ausdrückbar: "
//										+ artikel.getValue() + "["
//										+ artikel.getName() + "] - Regel '"
//										+ umsatzauswahl.getName() + "'");
//					BigDecimal hlVorher = alteZeile.getStueck().divide(
//							hlProGebinde, RoundingMode.HALF_UP);
//					if (hlVorher.compareTo(deltawert) < 0) {
//						BigDecimal neuStueck = deltawert.divide(hlProGebinde,
//								RoundingMode.HALF_UP).setScale(0,
//								RoundingMode.UP);
//						alteZeile.setStueck(neuStueck);
//					}
//					BigDecimal stueck = deltawert.divide(hlProGebinde,
//							RoundingMode.UP).setScale(0, RoundingMode.UP);
//					alteZeile.setStueck(alteZeile.getStueck().add(stueck));
//				} else {
//					throw new RuntimeException("unbekannte Masseinheit");
//				}
//				if (alteZeile.getStueck().equals(BigDecimal.ZERO))
//					alteZeile.delete(false);
//				else
//					alteZeile.saveEx();
//				count++;
//			}
//			rs.close();
//			stmt.close();
//		}
//
//		// nicht vorhandene
//		{
//			String sql = ResourceLoader.loadSQL(this,
//					"sucheNichtvorhandeneZeilen");
//			CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
//			stmt.setInt(1, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(2, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(3, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(4, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(5, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//			stmt.setInt(6, periode_id);
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				X_BAY_Umsatzstatistik neueZeile = new X_BAY_Umsatzstatistik(
//						getCtx(), 0, get_TrxName());
//				neueZeile.setAD_Org_ID(umsatzauswahl.getAD_Org_ID());
//				neueZeile.setBAY_Statistikperiode_ID(periode_id);
//				neueZeile.setC_BPartner_ID(rs.getInt("myBPartner_ID"));
//				final int myProduct_ID = rs.getInt("myProduct_ID");
//				neueZeile.setM_Product_ID(myProduct_ID);
//				/**
//				 * Hinzufügen laut Regel:
//				 */
//				// stueck, hl, prozent
//				String masseinheit = umsatzauswahl.getMasseinheit();
//				BigDecimal deltawert = umsatzauswahl.getAenderungswert();
//				if ("prozent".equals(masseinheit)) {
//					throw new RuntimeException(
//							"Mindestwert kann nicht prozentual sein");
//				} else if ("stueck".equals(masseinheit)) {
//					neueZeile.setStueck(deltawert);
//				} else if ("hl".equals(masseinheit)) {
//					MProduct artikel = new MProduct(getCtx(), myProduct_ID,
//							get_TrxName());
//					BigDecimal hlProGebinde = artikel.getVolume()
//							.scaleByPowerOfTen(-2);
//					BigDecimal stueck = deltawert.divide(hlProGebinde,
//							RoundingMode.HALF_UP).setScale(0, RoundingMode.UP);
//					neueZeile.setStueck(stueck);
//				}
//				if (!neueZeile.getStueck().equals(BigDecimal.ZERO))
//					neueZeile.saveEx();
//				count++;
//			}
//			rs.close();
//			stmt.close();
//		}
//		return count;
//	}
//
//	protected int loeschen(I_BAY_Umsatzauswahl umsatzauswahl)
//			throws SQLException {
//		String sql = ResourceLoader.loadSQL(this, "sucheZeilen");
//		CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
//		stmt.setInt(1, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//		stmt.setInt(2, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//		stmt.setInt(3, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//		stmt.setInt(4, umsatzauswahl.getBAY_Umsatzauswahl_ID());
//		stmt.setInt(5, periode_id);
//		ResultSet rs = stmt.executeQuery();
//		int count = 0;
//		while (rs.next()) {
//			X_BAY_Umsatzstatistik alteZeile = new X_BAY_Umsatzstatistik(
//					getCtx(), rs, get_TrxName());
//			// alten Eintrag löschen
//			alteZeile.deleteEx(false);
//			count++;
//		}
//		rs.close();
//		stmt.close();
//		return count;
//	}
//
//}
