//package de.bayen.freibier.model;
//
//import java.util.Properties;
//
//import org.compiere.model.CalloutEngine;
//import org.compiere.model.GridField;
//import org.compiere.model.GridTab;
//
///**
// * Aufnehmen einer Liste von Datensätzen.
// * 
// * @author tbayen
// */
//public class CalloutSearchSelection extends CalloutEngine {
//	/**
//	 * Das hier ist ein nicht ganz so hübscher Hack... Ich möchte eine Auswahl
//	 * von Datensätzen in einem Process verarbeiten. Dazu starte ich zuerst
//	 * eine Suche mit dem normalen Suchfeld und rufe dann einen Button auf.
//	 * Dieser startet zuerst dieses Callout hier, das die angegebene Auswahl
//	 * speichert und dann danach den Process. Dieser kann dann in diesem
//	 * statischen Feld die Auswahl abholen.
//	 * 
//	 * Nach der Benutzung sollte dieser Wert wieder auf <code>null</code> gestellt 
//	 * werden.
//	 */
//	static public int[] selection;
//
//	/**
//	 * Auswahl von Datensätzen erfassen und in einer statischen Variable merken.
//	 * 
//	 * @param ctx
//	 * @param WindowNo
//	 * @param mTab
//	 * @param mField
//	 * @param value
//	 * @param oldValue
//	 * @return
//	 */
//	public String select(Properties ctx, int WindowNo,
//			GridTab mTab, GridField mField, Object value, Object oldValue) {
//		//log.warning("Gruppen zuordnen");
//		int count = mTab.getRowCount();
//		selection=new int[count];
//		for(int i=0;i<count;i++){
//			selection[i]=mTab.getKeyID(i);
//			//log.warning("Zeile "+i+": "+mTab.getKeyID(i));
//		}
//		return "";
//	}
//}
