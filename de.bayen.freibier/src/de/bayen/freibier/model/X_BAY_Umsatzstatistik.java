/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package de.bayen.freibier.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for BAY_Umsatzstatistik
 *  @author iDempiere (generated) 
 *  @version Release 2.0 - $Id$ */
public class X_BAY_Umsatzstatistik extends PO implements I_BAY_Umsatzstatistik, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140117L;

    /** Standard Constructor */
    public X_BAY_Umsatzstatistik (Properties ctx, int BAY_Umsatzstatistik_ID, String trxName)
    {
      super (ctx, BAY_Umsatzstatistik_ID, trxName);
      /** if (BAY_Umsatzstatistik_ID == 0)
        {
			setBAY_Umsatzstatistik_ID (0);
        } */
    }

    /** Load Constructor */
    public X_BAY_Umsatzstatistik (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_BAY_Umsatzstatistik[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_BAY_Statistikperiode getBAY_Statistikperiode() throws RuntimeException
    {
		return (I_BAY_Statistikperiode)MTable.get(getCtx(), I_BAY_Statistikperiode.Table_Name)
			.getPO(getBAY_Statistikperiode_ID(), get_TrxName());	}

	/** Set Statistikperiode.
		@param BAY_Statistikperiode_ID Statistikperiode	  */
	public void setBAY_Statistikperiode_ID (int BAY_Statistikperiode_ID)
	{
		if (BAY_Statistikperiode_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BAY_Statistikperiode_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BAY_Statistikperiode_ID, Integer.valueOf(BAY_Statistikperiode_ID));
	}

	/** Get Statistikperiode.
		@return Statistikperiode	  */
	public int getBAY_Statistikperiode_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BAY_Statistikperiode_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Umsatzstatistik.
		@param BAY_Umsatzstatistik_ID Umsatzstatistik	  */
	public void setBAY_Umsatzstatistik_ID (int BAY_Umsatzstatistik_ID)
	{
		if (BAY_Umsatzstatistik_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BAY_Umsatzstatistik_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BAY_Umsatzstatistik_ID, Integer.valueOf(BAY_Umsatzstatistik_ID));
	}

	/** Get Umsatzstatistik.
		@return Umsatzstatistik	  */
	public int getBAY_Umsatzstatistik_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BAY_Umsatzstatistik_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set BAY_Umsatzstatistik_UU.
		@param BAY_Umsatzstatistik_UU BAY_Umsatzstatistik_UU	  */
	public void setBAY_Umsatzstatistik_UU (String BAY_Umsatzstatistik_UU)
	{
		set_Value (COLUMNNAME_BAY_Umsatzstatistik_UU, BAY_Umsatzstatistik_UU);
	}

	/** Get BAY_Umsatzstatistik_UU.
		@return BAY_Umsatzstatistik_UU	  */
	public String getBAY_Umsatzstatistik_UU () 
	{
		return (String)get_Value(COLUMNNAME_BAY_Umsatzstatistik_UU);
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_Name)
			.getPO(getC_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner .
		@param C_BPartner_ID 
		Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner .
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Lieferungen.
		@param Lieferungen Lieferungen	  */
	public void setLieferungen (int Lieferungen)
	{
		set_ValueNoCheck (COLUMNNAME_Lieferungen, Integer.valueOf(Lieferungen));
	}

	/** Get Lieferungen.
		@return Lieferungen	  */
	public int getLieferungen () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Lieferungen);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_Product_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Stück.
		@param Stueck Stück	  */
	public void setStueck (BigDecimal Stueck)
	{
		set_Value (COLUMNNAME_Stueck, Stueck);
	}

	/** Get Stück.
		@return Stück	  */
	public BigDecimal getStueck () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Stueck);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Stück gratis.
		@param StueckGratis Stück gratis	  */
	public void setStueckGratis (BigDecimal StueckGratis)
	{
		set_ValueNoCheck (COLUMNNAME_StueckGratis, StueckGratis);
	}

	/** Get Stück gratis.
		@return Stück gratis	  */
	public BigDecimal getStueckGratis () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_StueckGratis);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Umsatz.
		@param Umsatz Umsatz	  */
	public void setUmsatz (BigDecimal Umsatz)
	{
		set_ValueNoCheck (COLUMNNAME_Umsatz, Umsatz);
	}

	/** Get Umsatz.
		@return Umsatz	  */
	public BigDecimal getUmsatz () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Umsatz);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set UmsatzEK.
		@param UmsatzEK UmsatzEK	  */
	public void setUmsatzEK (BigDecimal UmsatzEK)
	{
		set_ValueNoCheck (COLUMNNAME_UmsatzEK, UmsatzEK);
	}

	/** Get UmsatzEK.
		@return UmsatzEK	  */
	public BigDecimal getUmsatzEK () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_UmsatzEK);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}