package de.bayen.freibier.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.adempiere.model.GridTabWrapper;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.I_M_Product;
import org.compiere.model.X_M_Product_Category;

public class CalloutProduct extends CalloutEngine implements IColumnCallout {

	private final static String PRODUKT_KATEGORIE_LEERGUT = "99";
	public final static String COLUMNNAME_LEERGUT = "isDeposit";

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value,
			Object oldValue) {
		X_M_Product_Category kat = new X_M_Product_Category(ctx,
				value==null?0:(Integer) value, null);
		I_M_Product record = GridTabWrapper.create(mTab, I_M_Product.class);
		if (PRODUKT_KATEGORIE_LEERGUT.equals(kat.getValue())) {
			record.setVolume(BigDecimal.ZERO);
			record.setUnitsPerPack(0);
			// es gibt keine Methode setIsDeposit(), 
			// weil ich das selber hinzugefügt habe:
			mTab.setValue(COLUMNNAME_LEERGUT, "Y");

			// record.set
		} else {
			// Nicht-Leergut wurde ausgewählt
			if (oldValue != null) {
				X_M_Product_Category oldkat = new X_M_Product_Category(ctx,
						(Integer) oldValue, null);
				if (PRODUKT_KATEGORIE_LEERGUT.equals(oldkat.getValue())) {
					// das geht nicht per Wrapper, weil ich das selber
					// hinzugefügt habe:
					mTab.setValue(COLUMNNAME_LEERGUT, "N");
				}
			} else
				mTab.setValue(COLUMNNAME_LEERGUT, "N");
		}
		return "";
	}
}
