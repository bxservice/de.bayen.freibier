//package de.bayen.freibier.model;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Properties;
//
//import org.compiere.util.CPreparedStatement;
//import org.compiere.util.DB;
//
//import de.bayen.util.ResourceLoader;
//
//public class MBAYArtikelgruppe extends X_BAY_Artikelgruppe {
//
//	private static final long serialVersionUID = 1L;
//
//	public MBAYArtikelgruppe(Properties ctx, int BAY_Artikelgruppe_ID,
//			String trxName) {
//		super(ctx, BAY_Artikelgruppe_ID, trxName);
//	}
//
//	public MBAYArtikelgruppe(Properties ctx, ResultSet rs, String trxName) {
//		super(ctx, rs, trxName);
//	}
//
//	@Override
//	protected boolean beforeSave(boolean newRecord) {
//		/**
//		 * Das SQL-Skript enthält ein SELECT, das alle zu löschenden
//		 * Artikelgruppen-Datensätze ergibt. Man könnte auch direkt ein
//		 * DELETE schreiben, ich wollte hier aber die Kontrolle in Java
//		 * behalten.
//		 */
//		String sql=ResourceLoader.loadSQL(this, "exklusive");
//		CPreparedStatement stat = DB.prepareStatement(sql,get_TrxName());
//		try {
//			stat.setInt(1, get_ID());
//			ResultSet rs = stat.executeQuery();
//			while(rs.next()){
//				String id = rs.getString("bay_artikelgruppe_id");
//				log.warning("loeschen: "+id);
//				// FIXME hier bin ich ins Bett gegangen...
//				
//			}
//		} catch (SQLException ex) {
//			throw new RuntimeException(ex);
//		}
//		return super.beforeSave(newRecord);
//	}
//}
