package de.bayen.freibier.ngv;

import java.sql.ResultSet;

import org.compiere.process.SvrProcess;
import org.compiere.util.CPreparedStatement;
import org.compiere.util.DB;

import de.bayen.freibier.model.I_BAY_Umsatzstatistik;
import de.bayen.freibier.model.X_BAY_Statistikperiode;
import de.bayen.freibier.model.X_BAY_Umsatzstatistik;

/**
 * Im Grunde wird die Statistik hier nur kopiert. Später kann man auf die
 * Kopie dann die Meldungsregeln anwenden. Es bleibt zu überlegen, ob man 
 * das in einem macht, aber bisher hatte ich noch nicht den Mut dazu.
 * 
 * @author tbayen
 */
public class ErzeugeAbsatzMeldungen extends SvrProcess {

	int ich;
	
	@Override
	protected void prepare() {
		ich = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		X_BAY_Statistikperiode myPeriode = new X_BAY_Statistikperiode(getCtx(),ich,get_TrxName());
		X_BAY_Statistikperiode neuePeriode = new X_BAY_Statistikperiode(getCtx(), 0, get_TrxName());
		neuePeriode.setAD_Org_ID(myPeriode.getAD_Org_ID());
		neuePeriode.setValue(myPeriode.getValue()+"M");
		neuePeriode.setName(myPeriode.getName()+" Meldung");
		String desc=myPeriode.getDescription();
		desc=desc==null?"":desc;
		neuePeriode.setDescription("Meldung "+desc);
		neuePeriode.setStartDate(myPeriode.getStartDate());
		neuePeriode.setEndDate(myPeriode.getEndDate());
		String myType = myPeriode.getStatistiktyp();
		if("MD".equals(myType) || "JD".equals(myType))
			neuePeriode.setStatistiktyp("NGVA");  // Absatz
		else if("MED".equals(myType) || "JED".equals(myType))
			neuePeriode.setStatistiktyp("NGVB");  // Beschaffung
		else 
			throw new RuntimeException("unbekannter Statistiktyp: "+myType);
		neuePeriode.saveEx(get_TrxName());
		//
		String sql="SELECT * FROM bay_umsatzstatistik WHERE bay_statistikperiode_id=?";
		CPreparedStatement stat = DB.prepareStatement(sql, get_TrxName());
		stat.setInt(1, ich);
		ResultSet rs = stat.executeQuery();
		while(rs.next()){
			X_BAY_Umsatzstatistik record = new X_BAY_Umsatzstatistik(getCtx(), 0, get_TrxName());
			record.setAD_Org_ID(neuePeriode.getAD_Org_ID());
			record.setC_BPartner_ID(rs.getInt(I_BAY_Umsatzstatistik.COLUMNNAME_C_BPartner_ID));
			record.setM_Product_ID(rs.getInt(I_BAY_Umsatzstatistik.COLUMNNAME_M_Product_ID));
			record.setStueck(rs.getBigDecimal(I_BAY_Umsatzstatistik.COLUMNNAME_Stueck));
			record.setStueckGratis(rs.getBigDecimal(I_BAY_Umsatzstatistik.COLUMNNAME_StueckGratis));
			record.setLieferungen(rs.getInt(I_BAY_Umsatzstatistik.COLUMNNAME_Lieferungen));
			record.setUmsatz(rs.getBigDecimal(I_BAY_Umsatzstatistik.COLUMNNAME_Umsatz));
			record.setUmsatzEK(rs.getBigDecimal(I_BAY_Umsatzstatistik.COLUMNNAME_UmsatzEK));
			record.setBAY_Statistikperiode_ID(neuePeriode.get_ID());
			record.saveEx();
		}
		stat.close();
		rs.close();
		commitEx();
		return null;
	}
}
