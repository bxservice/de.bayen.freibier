///**
// * 
// */
//package de.bayen.freibier.process;
//
//import java.sql.Timestamp;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//
//import org.apache.poi.ss.formula.eval.NotImplementedException;
//import org.compiere.process.ProcessInfo;
//import org.compiere.process.SvrProcess;
//import org.compiere.util.Trx;
//
//import de.bayen.freibier.model.MBAYStatistikperiode;
//import de.bayen.freibier.model.X_BAY_Statistikperiode;
//
///**
// * Dieser Prozess erzeugt möglichst vollständig und ohne weitere manuelle
// * Zwischenschritte die NGV-Meldungen.
// * 
// * Vorherige Schritte, die sich auf die Übernahme der Daten aus DURST beziehen,
// * habe ich hier weggelassen, weil das letztlich nur eine Übergangslösung ist
// * und ich nicht den gesamten Code des DurstKonverters hier einbinden wollte.
// * Dazu werde ich ggf. ein gesondertes Skript schreiben.
// * 
// * @author tbayen
// */
//public class NGVMeldungen extends SvrProcess {
//
//	static boolean debug = true;
//	// TODO Verzeichnis hier direkt mit angeben? (muss mit / enden)
//	static String datenDirectory = "";
//
//	/**
//	 * @see org.compiere.process.SvrProcess#prepare()
//	 */
//	@Override
//	protected void prepare() {
//	}
//
//	/**
//	 * @see org.compiere.process.SvrProcess#doIt()
//	 */
//	@Override
//	protected String doIt() throws Exception {
//
//		Calendar date = Calendar.getInstance();
//		if (date.get(Calendar.DAY_OF_MONTH) > 12 && !debug)
//			throw new IllegalArgumentException(
//					"NGV-Meldungen müssen bis zum 10. des Monats durchgeführt werden");
//		date.set(Calendar.DAY_OF_MONTH, 1);
//		int monat = date.get(Calendar.MONTH);
//		if (monat == 0)
//			throw new NotImplementedException(
//					"Jahreswechsel nicht implementiert");
//		date.add(Calendar.MONTH, -1);
//		DateFormat format = new SimpleDateFormat("MM/yyyy");
//		String dateNice = format.format(date.getTime());
//		String dateString = new SimpleDateFormat("yyMM").format(date.getTime());
//		String fileDateString = new SimpleDateFormat("yyyy-MM").format(date
//				.getTime());
//		log.info("NGVMeldungen für den Monat ab dem " + dateNice);
//
//		/*
//		 * Ich gehe nach meiner Anleitung auf
//		 * http://www.frei-bier.org/wiki/HB/Statistik/Umsatzmeldungen Schritt
//		 * für Schritt vor.
//		 */
//
//		// Neue Periodenstatistik - Einträge anlegen
//		int[] vorhanden = X_BAY_Statistikperiode.getAllIDs(
//				X_BAY_Statistikperiode.Table_Name, "value LIKE '__"
//						+ dateString + "%'", get_TrxName());
//		if (vorhanden.length > 0 && !debug)
//			throw new IllegalArgumentException(
//					"Es sind bereits Statistiken der Periode " + dateString
//							+ " vorhanden.");
//
//		Calendar endDate = (Calendar) date.clone();
//		endDate.add(Calendar.MONTH, 1);
//		endDate.add(Calendar.DAY_OF_MONTH, -1);
//
//		X_BAY_Statistikperiode periodeVK = new X_BAY_Statistikperiode(getCtx(),
//				0, get_TrxName());
//		periodeVK.setValue("st" + dateString);
//		periodeVK.setName("Monatsstatistik " + dateNice
//				+ (debug ? " debug" : ""));
//		periodeVK.setStartDate(new Timestamp(date.getTimeInMillis()));
//		periodeVK.setEndDate(new Timestamp(endDate.getTimeInMillis()));
//		periodeVK
//				.setStatistiktyp(MBAYStatistikperiode.STATISTIKTYP_MONATLICHDURST);
//		periodeVK.saveEx();
//		log.info(Integer.toString(periodeVK.get_ID()));
//
//		X_BAY_Statistikperiode periodeEK = new X_BAY_Statistikperiode(getCtx(),
//				0, get_TrxName());
//		periodeEK.setValue("lm" + dateString);
//		periodeEK.setName("Monats-Einkaufsstatistik " + dateNice
//				+ (debug ? " debug" : ""));
//		periodeEK.setStartDate(new Timestamp(date.getTimeInMillis()));
//		periodeEK.setEndDate(new Timestamp(endDate.getTimeInMillis()));
//		periodeEK
//				.setStatistiktyp(MBAYStatistikperiode.STATISTIKTYP_MONATLICHDURST);
//		periodeEK.saveEx();
//		log.info(Integer.toString(periodeEK.get_ID()));
//		commitEx();
//
//		boolean result;
//		if(false){
//		// DURST-Daten einlesen in die VK-Periode
//		ProcessInfo pinfo = new ProcessInfo(this.getClass().getSimpleName(),
//				getProcessInfo().getAD_Process_ID(),
//				MBAYStatistikperiode.Table_ID, periodeVK.get_ID());
//		{
//			ImportStatisticsFromDurst process = new ImportStatisticsFromDurst();
//			process.ich = periodeVK.get_ID();
//			result = process.startProcess(getCtx(), pinfo,
//					Trx.get(get_TrxName(), false));
//			if (!result)
//				throw new RuntimeException("Kann Prozess nicht ausführen.");
//		}
//
//		// Neue Periode für Absatzmeldungen erzeugen
//		{
//			ErzeugeAbsatzMeldungen process = new ErzeugeAbsatzMeldungen();
//			process.ich = periodeVK.get_ID();
//			result = process.startProcess(getCtx(), pinfo,
//					Trx.get(get_TrxName(), false));
//			if (!result)
//				throw new RuntimeException("Kann Prozess nicht ausführen.");
//		}
//
//		// Meldungsregeln auf die Verkaufsstatistik anwenden
//		{
//			MeldungsregelnAnwenden process = new MeldungsregelnAnwenden();
//			process.setPeriode_id(periodeEK.get_ID());
//			result = process.startProcess(getCtx(), pinfo,
//					Trx.get(get_TrxName(), false));
//			if (!result)
//				throw new RuntimeException("Kann Prozess nicht ausführen.");
//		}
//
//		// Export der Datei im von der NGV geforderten CSV-Format
//		{
//			ExportNGV process = new ExportNGV();
//			process.setPeriode_id(periodeEK.get_ID());
//			process.setDateiname(datenDirectory + "CSVImport_" + fileDateString
//					+ ".csv");
//			result = process.startProcess(getCtx(), pinfo,
//					Trx.get(get_TrxName(), false));
//			if (!result)
//				throw new RuntimeException("Kann Prozess nicht ausführen.");
//		}
//	}
//		// Einkaufsstatistik - zuerst die Jahresstatistik aktualisieren
//		String jahresEKValue = "lk"
//				+ new SimpleDateFormat("yy").format(date.getTime()) + "00";
//		int[] ids = MBAYStatistikperiode.getAllIDs(
//				MBAYStatistikperiode.Table_Name, "value = '" + jahresEKValue
//						+ "'", get_TrxName());
//		if (ids.length == 0)
//			throw new IllegalArgumentException(
//					"kann keine Jahreseinkaufsstatistik finden: "
//							+ jahresEKValue);
//		int jahresEKPeriode = ids[0];
//		// Jahres-Einkaufs-Statistik importieren
//		{
//			ProcessInfo pinfoEKJ = new ProcessInfo(this.getClass()
//					.getSimpleName(), getProcessInfo().getAD_Process_ID(),
//					MBAYStatistikperiode.Table_ID, jahresEKPeriode);
//			ImportStatisticsFromDurst process = new ImportStatisticsFromDurst();
//			process.ich = jahresEKPeriode;
//			result = process.startProcess(getCtx(), pinfoEKJ,
//					Trx.get(get_TrxName(), false));
//			if (!result)
//				throw new RuntimeException("Kann Prozess nicht ausführen.");
//		}
//		// Jetzt die Einkaufs-Monatsstatistik
//		ProcessInfo pinfoEKM = new ProcessInfo(this.getClass().getSimpleName(),
//				getProcessInfo().getAD_Process_ID(),
//				MBAYStatistikperiode.Table_ID, periodeEK.get_ID());
//		{
//			ImportStatisticsFromDurst process = new ImportStatisticsFromDurst();
//			process.ich = periodeEK.get_ID();
//			result = process.startProcess(getCtx(), pinfoEKM,
//					Trx.get(get_TrxName(), false));
//			if (!result)
//				throw new RuntimeException("Kann Prozess nicht ausführen.");
//		}
//
//		// Export der Datei im von der NGV geforderten CSV-Format
//		{
//			ExportNGV process = new ExportNGV();
//			process.setPeriode_id(periodeEK.get_ID());
//			process.setDateiname(datenDirectory + "CSVImport_Bez_"
//					+ fileDateString + ".csv");
//			result = process.startProcess(getCtx(), pinfoEKM,
//					Trx.get(get_TrxName(), false));
//			if (!result)
//				throw new RuntimeException("Kann Prozess nicht ausführen.");
//		}
//		commitEx();
//
//		return "NGV-Export abgeschlossen";
//	}
//}
