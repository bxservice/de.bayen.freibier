package de.bayen.freibier.ngv;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import de.bayen.freibier.model.X_BAY_Statistikperiode;
import de.bayen.util.ResourceLoader;

/**
 * Erzeugt die eigentliche Export-Datei für die NGV. Der Dateiname sollte der
 * Konvention der NGV entsprechen (z.B. "CSV_Bez_Import_2012-04.csv" für
 * Einkaufsdaten und "CSV_Import_2012-04.csv" für Absatzdaten). Diese beiden
 * Dateien dann bitte im Verzeichnis Dokumente/Daten/NGV archivieren und per
 * EMail an die NGV schicken.
 * 
 * Dieser Prozess stellt die alte, bis 2013 benutzte Schnittstelle dar.
 * 
 * @author tbayen
 */
public class ExportNGV extends SvrProcess {

	private int periode_id;
	private String dateiname = null;

	@Override
	protected void prepare() {
		periode_id = getRecord_ID();
		ProcessInfoParameter[] params = getParameter();
		for (ProcessInfoParameter parameter : params) {
			if ("Dateiname".equals(parameter.getParameterName())) {
				dateiname = (String) parameter.getParameter();
			}
		}
	}

	public void setPeriode_id(int periode_id) {
		this.periode_id = periode_id;
	}

	public void setDateiname(String dateiname) {
		this.dateiname = dateiname;
	}

	@Override
	protected String doIt() throws Exception {
		X_BAY_Statistikperiode statistikperiode = new X_BAY_Statistikperiode(getCtx(), periode_id,
				get_TrxName());
		String statistiktyp = statistikperiode.getStatistiktyp();
		if ("NGVB".equals(statistiktyp) || statistiktyp.matches("(J|M)ED"))
			return bezugsstatistik();
		else if ("NGVA".equals(statistiktyp) || statistiktyp.matches("(J|M)D")) {
			return absatzstatistik();
		} else {
			return "Statistiktyp kann nicht exportiert werden.";
		}
	}

	protected String absatzstatistik() throws Exception {
		String sql = ResourceLoader.loadSQL(this, "readAbsatz");
		CPreparedStatement stat = DB.prepareStatement(sql, get_TrxName());
		stat.setInt(1, periode_id);
		ResultSet rs = stat.executeQuery();

		String[] header = { "Partner", "KdNr", "Name1", "Name2", "Strasse", "PLZ", "Ort", "ArtNr",
				"Artbez", "Artbez2", "Inhalt", "Packart", "MEH", "Menge", "VON", "BIS", "Gratis",
				"EAN1", "EAN2", "EAN3", "EAN4", "EAN5" };
		FileOutputStream fost = new FileOutputStream(dateiname);
		Writer writer = new OutputStreamWriter(fost, "ISO-8859-15");
		CsvPreference csvPref=new CsvPreference.Builder('"', '\t', "\n").build();
		CsvMapWriter csv = new CsvMapWriter(writer, csvPref);
		csv.writeHeader(header);
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < header.length; i++) {
				final String headerName = header[i];
				Object value = rs.getObject(headerName);
				if (value == null)
					value = "";
				else if (value.getClass() == java.sql.Date.class)
					value = dateFormat.format(value);
				else if (value.getClass() == Date.class)
					value = dateFormat.format(value);
				else if (value.getClass() == Timestamp.class)
					value = dateFormat.format(value);
				else if (value.getClass() == BigDecimal.class)
					value = ((BigDecimal) value).toPlainString().replace(".", ",");
				// else if ("ArtNr".equals(headerName))
				// value="\""+value+"\""; // OOo liest sonst die Werte falsch
				// ein
				map.put(headerName, value);
			}
			// Anpassungen wg. des Gratis-Formats der NGV
			BigDecimal menge = rs.getBigDecimal("Menge");
			if (menge != null && menge.compareTo(BigDecimal.ZERO) != 0) {
				// Zeile für berechnete Menge
				map.put("Gratis", "");
				csv.write(map, header);
			}
			BigDecimal mengeGratis = rs.getBigDecimal("Gratis");
			if (mengeGratis != null && mengeGratis.compareTo(BigDecimal.ZERO) != 0) {
				// Zeile für gratis Menge
				map.put("Menge", mengeGratis.toPlainString().replace(".", ","));
				map.put("Gratis", "x");
				csv.write(map, header);
			}
		}
		rs.close();
		stat.close();
		csv.close();
		writer.close();
		fost.close();
		return null;
	}

	protected String bezugsstatistik() throws Exception {
		String sql = ResourceLoader.loadSQL(this, "readBezug");
		CPreparedStatement stat = DB.prepareStatement(sql, get_TrxName());
		stat.setInt(1, periode_id);
		ResultSet rs = stat.executeQuery();

		String[] header = { "Partner", "LiefNr", "Name", "Strasse", "PLZ", "Ort", "ArtNr",
				"Artbez", "Inhalt", "Packart", "MEH", "Menge", "VON", "BIS", "EAN1", "EAN2",
				"EAN3", "EAN4", "EAN5" };
		FileOutputStream fost = new FileOutputStream(dateiname);
		Writer writer = new OutputStreamWriter(fost, "ISO-8859-15");
		CsvPreference csvPref=new CsvPreference.Builder('"', '\t', "\n").build();
		CsvMapWriter csv = new CsvMapWriter(writer, csvPref);
		csv.writeHeader(header);
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < header.length; i++) {
				final String headerName = header[i];
				Object value = rs.getObject(headerName);
				if (value == null)
					value = "";
				else if (value.getClass() == java.sql.Date.class)
					value = dateFormat.format(value);
				else if (value.getClass() == Date.class)
					value = dateFormat.format(value);
				else if (value.getClass() == Timestamp.class)
					value = dateFormat.format(value);
				else if (value.getClass() == BigDecimal.class)
					value = ((BigDecimal) value).toPlainString().replace(".", ",");
				map.put(headerName, value);
			}
			csv.write(map, header);
		}
		rs.close();
		stat.close();
		csv.close();
		writer.close();
		fost.close();
		return null;
	}
}
