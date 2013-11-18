package de.bayen.freibier.ngv.oldcode;
//package de.bayen.freibier.process;
//
//import org.compiere.model.MTable;
//import org.compiere.process.ProcessInfoParameter;
//import org.compiere.process.SvrProcess;
//
//import de.bayen.freibier.model.CalloutSearchSelection;
//import de.bayen.freibier.model.X_BAY_Artikelgruppe;
//import de.bayen.freibier.model.X_BAY_BPGruppe;
//
///**
// * Erlaubt die Zuordnung einer Liste von Artikeln oder Geschäftspartnern zu
// * einer neuen Gruppe.
// * 
// * @author tbayen
// */
//public class GruppenZuordnung extends SvrProcess {
//
//	int ich;
//	int gruppe_id;
//
//	@Override
//	protected void prepare() {
//		ich = getRecord_ID();
//		ProcessInfoParameter[] params = getParameter();
//		for (ProcessInfoParameter parameter : params) {
//			if ("BAY_Gruppe_ID".equals(parameter.getParameterName())) {
//				gruppe_id = parameter.getParameterAsInt();
//			}
//		}
//	}
//
//	@Override
//	protected String doIt() throws Exception {
//		if (gruppe_id < 1)
//			return "angegebene Gruppe nicht gültig";
//		int[] ids = CalloutSearchSelection.selection;
//		// Wo bin ich?
//		MTable meineTabelle = new MTable(getCtx(), getTable_ID(), get_TrxName());
//		/*
//		 * Diese Methode funktioniert sowohl, wenn sie aus einer Artikelliste
//		 * als auch wenn sie aus einer Geschäftspartnerliste heraus aufgerufen
//		 * werden.
//		 */
//		if ("M_Product".equals(meineTabelle.getTableName())) {
//			for (int i = 0; i < ids.length; i++) {
//				X_BAY_Artikelgruppe artikelgruppe = new X_BAY_Artikelgruppe(
//						getCtx(), 0, get_TrxName());
//				artikelgruppe.setBAY_Gruppe_ID(gruppe_id);
//				artikelgruppe.setM_Product_ID(ids[i]);
//				artikelgruppe.save();
//			}
//		} else if ("C_BPartner".equals(meineTabelle.getTableName())) {
//			// C_BPartner
//			for (int i = 0; i < ids.length; i++) {
//				X_BAY_BPGruppe geschaeftspartnergruppe = new X_BAY_BPGruppe(
//						getCtx(), 0, get_TrxName());
//				geschaeftspartnergruppe.setBAY_Gruppe_ID(gruppe_id);
//				geschaeftspartnergruppe.setC_BPartner_ID(ids[i]);
//				geschaeftspartnergruppe.save();
//			}
//		} else {
//			throw new RuntimeException(
//					"Gruppenzuordnung mit ungültigem Datentyp: "
//							+ meineTabelle.getTableName());
//		}
//		commitEx();
//		CalloutSearchSelection.selection = null;
//		// log.warning("IDs: " + ids);
//		return null;
//	}
//}
