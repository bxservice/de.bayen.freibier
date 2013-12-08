package de.bayen.freibier.ngv.oldcode;
//package de.bayen.freibier.process;
//
//import java.math.BigDecimal;
//import java.sql.ResultSet;
//
//import org.compiere.process.SvrProcess;
//import org.compiere.util.CPreparedStatement;
//import org.compiere.util.DB;
//
//import de.bayen.freibier.model.X_BAY_Condition;
//import de.bayen.freibier.model.X_BAY_Condition_Groups;
//import de.bayen.util.ResourceLoader;
//
///**
// * Importiert Preise & Konditionen von DURST.
// * 
// * @author tbayen
// */
//public class ImportKonditionen extends SvrProcess {
//
//	@Override
//	protected void prepare() {
//	}
//
//	@Override
//	protected String doIt() throws Exception {
//		String sql=ResourceLoader.loadSQL(this, "Rueckverguetungen");
//		CPreparedStatement stat = DB.prepareStatement(sql, get_TrxName());
//		ResultSet rs = stat.executeQuery();
//		while(rs.next()){
//			X_BAY_Condition condition = new X_BAY_Condition(getCtx(), 0, get_TrxName());
//			int empfaenger_id=rs.getInt("empfaenger_id");
//			int bezieher_id=rs.getInt("bezieher_id");
//			//String artikelauswahl=rs.getString("artikelauswahl");
//			int artikel_id=rs.getInt("artikel_id");
//			int artikelgruppe_id=rs.getInt("artikelgruppe_id");
//			String uom=rs.getString("uom");
//			BigDecimal betrag=rs.getBigDecimal("betrag");
//			String mwstkz=rs.getString("mwstkz");
//			BigDecimal abMenge=rs.getBigDecimal("abmenge");
//			//
//			condition.setName("Rückvergütung");
//			condition.setDescription("Übernommen aus DURST");
//			condition.setC_BPartner_ID(bezieher_id);
//			if(artikel_id!=0){
//				// einzelner Artikel ausgewählt
//				condition.setM_Product_ID(artikel_id);
//			}else{
//				// Artikelgruppe ausgewählt
//				condition.setM_Product_ID(0);
//				// schonmal speichern, damit ich eine ID bekomme
//				condition.saveEx();
//				X_BAY_Condition_Groups gruppe = new X_BAY_Condition_Groups(getCtx(), 0, get_TrxName());
//				gruppe.setBAY_Gruppe_ID(artikelgruppe_id);
//				gruppe.setBAY_Condition_ID(condition.get_ID());
//				gruppe.saveEx();
//			}
//			condition.setConditionLevel("0900"); // Rückvergütungen
//			//
//			/* (H)ektoliter, (S)tueck, (U)msatz, (F)üllung */
//			if("H".equals(uom)){
//				condition.setConditionType("increase");
//				condition.setConditionUOM("hl");
//			}else if("S".equals(uom)){
//				condition.setConditionType("increase");
//				condition.setConditionUOM("item");
//			}else if("U".equals(uom)){
//				condition.setConditionType("percent");
//				condition.setConditionUOM("item");  // eigentlich egal
//			}else if("F".equals(uom)){
//				condition.setConditionType("increase");
//				condition.setConditionUOM("unit");
//			}
//			condition.setAmount(betrag);
//			if("0".equals(mwstkz)){
//				// ohne MwSt
//				condition.setBruttoNetto(null);
//			}else if("1".equals(mwstkz)){
//				// plus MwSt
//				condition.setBruttoNetto("N");
//			}else if("2".equals(mwstkz)){
//				// inklusive MwSt, kommt aber in DURST nicht vor
//				condition.setBruttoNetto("B");
//			}else if(" ".equals(mwstkz)){
//				// ist in DURST nicht erklärt, kommt aber vor...
//				condition.setBruttoNetto("B");
//			}
//			// abMenge?
//			if(abMenge!=null){
//				condition.setMinimumInterval("year");
//				if("H".equals(uom)){
//					condition.setMinimumUOM("hl");
//				}else if("S".equals(uom)){
//					condition.setMinimumUOM("item");
//				}else if("U".equals(uom)){
//					condition.setMinimumUOM("???");  // FIXME Baustelle
//				}else if("F".equals(uom)){
//					condition.setMinimumUOM("unit");
//				}
//			}
//			//
//			if(bezieher_id != empfaenger_id){
//				condition.setBeneficiary(empfaenger_id);
//			}
//			condition.saveEx();
//		}
//		rs.close();
//		stat.close();
//		commitEx();
//		return null;
//	}
//}
