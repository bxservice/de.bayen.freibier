package de.bayen.freibier.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.compiere.model.I_M_Product;
import org.compiere.model.PO;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;

import de.bayen.util.ResourceLoader;
import de.bayen.util.StringUtils;

/**
 * Model for Beverage Trading Unit (Gebinde).
 * 
 * @author tbayen
 */
@SuppressWarnings("serial")
public class MBAYTradingUnit extends X_BAY_TradingUnit {

	public MBAYTradingUnit(Properties ctx, int BAY_TradingUnit_ID,
			String trxName) {
		super(ctx, BAY_TradingUnit_ID, trxName);
	}

	public MBAYTradingUnit(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	private void rebuildDepositValue(){
		String sql=ResourceLoader.loadSQL(this, "calculateDepositValue");
		CPreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
		try {
			stmt.setInt(1, getDepositPackage_ID());
			stmt.setInt(2, getUnitsPerPack());
			stmt.setInt(3, getDepositUnit_ID());
			ResultSet rs = stmt.executeQuery();
			rs.next();
			if(rs.isAfterLast()){
				throw new RuntimeException("Fehler in meinem SQL Code");
			}
			setDepositValue(rs.getBigDecimal(1));
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Setzt das Beschreibungsfeld aus den anderen Feldern zusammen.
	 */
	private void rebuildDescription(){
		String erg="";
		if(getC_UOM_ID()>0){
		String masseinheit=getC_UOM().getName();
		erg+=masseinheit;
		}
		String besonderheit=getBesonderheit();
		if(besonderheit!=null && besonderheit.length()>0){
			if(erg.length()>0)
				erg+=" ";
			erg+=besonderheit;
		}
		if(erg.length()>0)
			erg+=" ";
		erg+=getInhalt();
		setDescription(erg);
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		rebuildDepositValue();
		rebuildDescription();
		return super.beforeSave(newRecord);
	}

	/**
	 * Füllt das angegebene Produkt mit den Werten, die aus diesem Gebinde übernommen werden können.
	 * 
	 * @param product
	 */
	public void initProduct(I_M_Product product){
		// Falls keine Kurzbeschreibung existiert, kopiere ich den Namen
		String oldDesc=product.getDescription();
		if(StringUtils.isEmpty(oldDesc)){
			product.setDescription(product.getName());
		}
		//log.warning(Integer.toString(get_ID()));		
		((PO)product).set_ValueOfColumn("BAY_TradingUnit_ID", get_ID());
		product.setName(product.getDescription()+" "+getName());
		product.setVolume(getVolume());
		product.setWeight(getVolume().add(getWeight()));
		int uom = getC_UOM_ID();
		if(uom>0)
			product.setC_UOM_ID(uom);
	}
}
