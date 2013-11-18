package de.bayen.freibier.ngv;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.compiere.process.SvrProcess;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;

import de.bayen.freibier.model.X_BAY_Statistikperiode;
import de.bayen.freibier.model.X_BAY_Umsatzstatistik;
import de.bayen.util.ResourceLoader;

/**
 * Sollte von einem Button in einer Statistikperiode aufgerufen werden.
 * 
 * Die angewählte Statistikperiode wird mit Daten gefüllt. Diese werden aus dem
 * Datenbank "durst" gelesen. das vorher vom externen Programm "DurstKonverter"
 * gefüllt worden sein sollte.
 * 
 * Bei Einkaufsstatistiken gibt es eine Besonderheit. Aus DURST kann man direkt
 * immer nur eine Jahresstatistik importieren. Wenn Monatsstatistiken importiert
 * werden sollen, werden diese also aus den bereits vorhandenen Tabellen
 * errechnet. Das bedeutet also, das die Jahrestabelle vorher aktualisiert sein
 * muss und alle vorherigen Monatstabellen auch bereits erzeugt sein müssen.
 * 
 * @author tbayen
 */
public class ImportStatisticsFromDurst extends SvrProcess {

	int ich;

	@Override
	protected void prepare() {
		ich = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		String result = "";
		X_BAY_Statistikperiode statistikperiode = new X_BAY_Statistikperiode(getCtx(), ich,
				getName());
		{
			// Tabelle erstmal
			String sql = ResourceLoader.loadSQL(this, "delete");
			CPreparedStatement stat = DB.prepareStatement(sql, get_TrxName());
			stat.setString(1, statistikperiode.getValue());
			int updateCount = stat.executeUpdate();
			if (updateCount > 0)
				result = Integer.toString(updateCount) + " Datensätze gelöscht. ";
		}
		if (statistikperiode.getStatistiktyp().equals("MED")) {
			// monatlicher Einkauf DURST muss besonders behandelt werden
			return result + monatlicherEinkaufDurst(statistikperiode);
		}
		String sql = ResourceLoader.loadSQL(this, "readDurstStatistiken");
		CPreparedStatement stat = DB.prepareStatement(sql, get_TrxName());
		stat.setString(1, statistikperiode.getValue());
		ResultSet rs = stat.executeQuery();
		int count = 0;
		while (rs.next()) {
			X_BAY_Umsatzstatistik rec;
			try {
				rec = new X_BAY_Umsatzstatistik(getCtx(), 0, get_TrxName());
				int partner_id = rs.getInt(1);
				if (partner_id < 1)
					throw new RuntimeException("Geschäftspartner existiert nicht: "
							+ rs.getString("NumKunde"));
				rec.setC_BPartner_ID(partner_id);
				int artikel_id = rs.getInt(2);
				if (artikel_id < 1)
					throw new RuntimeException("Artikel existiert nicht: "
							+ rs.getString("NumArtikel"));
				rec.setM_Product_ID(artikel_id);
				rec.setStueck(rs.getBigDecimal(3));
				rec.setUmsatz(rs.getBigDecimal(4));
				rec.setUmsatzEK(rs.getBigDecimal(5));
				rec.setLieferungen(rs.getInt(6));
				rec.setStueckGratis(rs.getBigDecimal(7));
				rec.setBAY_Statistikperiode_ID(ich);
				rec.save();
				count++;
				// log.warning("Count: "+Integer.toString(count));
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		commitEx();
		rs.close();
		stat.close();
		return result + Integer.toString(count) + " Datensätze importiert.";
	}

	private String monatlicherEinkaufDurst(X_BAY_Statistikperiode statistikperiode)
			throws SQLException {
		String sql = ResourceLoader.loadSQL(this, "kopiereEinkauf");
		CPreparedStatement stat = DB.prepareStatement(sql, get_TrxName());
		stat.setString(1, statistikperiode.getValue());
		ResultSet rs = stat.executeQuery();
		int count = 0;
		while (rs.next()) {
			count++;
			X_BAY_Umsatzstatistik neu = new X_BAY_Umsatzstatistik(getCtx(), 0, get_TrxName());
			neu.setBAY_Statistikperiode_ID(statistikperiode.get_ID());
			neu.setAD_Org_ID(rs.getInt(X_BAY_Umsatzstatistik.COLUMNNAME_AD_Org_ID));
			neu.setC_BPartner_ID(rs.getInt(X_BAY_Umsatzstatistik.COLUMNNAME_C_BPartner_ID));
			neu.setM_Product_ID(rs.getInt(X_BAY_Umsatzstatistik.COLUMNNAME_M_Product_ID));
			neu.setLieferungen(rs.getInt(X_BAY_Umsatzstatistik.COLUMNNAME_Lieferungen));
			neu.setStueck(rs.getBigDecimal(X_BAY_Umsatzstatistik.COLUMNNAME_Stueck));
			neu.setStueckGratis(rs.getBigDecimal(X_BAY_Umsatzstatistik.COLUMNNAME_StueckGratis));
			neu.setUmsatz(rs.getBigDecimal(X_BAY_Umsatzstatistik.COLUMNNAME_Umsatz));
			neu.setUmsatzEK(rs.getBigDecimal(X_BAY_Umsatzstatistik.COLUMNNAME_UmsatzEK));
			neu.save();
		}
		commitEx();
		return Integer.toString(count) + " Datensätze erzeugt";
	}

}
