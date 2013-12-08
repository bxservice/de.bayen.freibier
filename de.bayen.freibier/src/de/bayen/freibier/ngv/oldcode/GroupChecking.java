package de.bayen.freibier.ngv.oldcode;
///**
// * 
// */
//package de.bayen.freibier.process;
//
//import java.sql.ResultSet;
//
//import org.compiere.process.SvrProcess;
//import org.compiere.util.CPreparedStatement;
//import org.compiere.util.DB;
//
//import de.bayen.util.ResourceLoader;
//
///**
// * Untersucht das System aus Gruppen und Gruppen-Kategorien auf Inkonsistenzen
// * und bügelt diese ggf. aus.
// * 
// * @author tbayen
// */
//public class GroupChecking extends SvrProcess {
//	@Override
//	protected void prepare() {
//	}
//
//	@Override
//	protected String doIt() throws Exception {
//		/*
//		 * Diese Abfrage "findeKategorien" findet alle Gruppen-Kategorien, die
//		 * einer bereits vorhandenen Tabellenspalte zugeordnet sind.
//		 */
//		String sql=ResourceLoader.loadSQL(this, "findeKategorien");
//		CPreparedStatement stat = DB.prepareStatement(sql, get_TrxName());
//		stat.setInt(1, getAD_Client_ID());
//		ResultSet rs = stat.executeQuery();
//		while(rs.next()){
//			/*
//			 * Wenn der Name der Spalte auf *_ID endet, handelt es sich um einen
//			 * Fremdschlüssel. Das bedeutet, um an den wahren Wert zu kommen,
//			 * muss ich zuerst eine andere Tabelle mit einbeziehen. Deshalb ist
//			 * dieser Fall anders zu behandeln als ohne *_ID.
//			 */
//			String columnname = rs.getString("columnname");
//			if(columnname.toUpperCase().endsWith("_ID")){
//				sql=ResourceLoader.loadSQL(this, "findeUngleiche");
//			}
//		}
//		return null;
//	}
//}
