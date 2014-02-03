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
import org.compiere.util.KeyNamePair;

/** Generated Model for BAY_TradingUnit
 *  @author iDempiere (generated) 
 *  @version Release 2.0 - $Id$ */
public class X_BAY_TradingUnit extends PO implements I_BAY_TradingUnit, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140129L;

    /** Standard Constructor */
    public X_BAY_TradingUnit (Properties ctx, int BAY_TradingUnit_ID, String trxName)
    {
      super (ctx, BAY_TradingUnit_ID, trxName);
      /** if (BAY_TradingUnit_ID == 0)
        {
			setBAY_TradingUnit_ID (0);
			setC_UOM_ID (0);
			setName (null);
			setUnitsPerPack (0);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_BAY_TradingUnit (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_BAY_TradingUnit[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Gebinde.
		@param BAY_TradingUnit_ID Gebinde	  */
	public void setBAY_TradingUnit_ID (int BAY_TradingUnit_ID)
	{
		if (BAY_TradingUnit_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BAY_TradingUnit_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BAY_TradingUnit_ID, Integer.valueOf(BAY_TradingUnit_ID));
	}

	/** Get Gebinde.
		@return Gebinde	  */
	public int getBAY_TradingUnit_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BAY_TradingUnit_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set BAY_TradingUnit_UU.
		@param BAY_TradingUnit_UU BAY_TradingUnit_UU	  */
	public void setBAY_TradingUnit_UU (String BAY_TradingUnit_UU)
	{
		set_Value (COLUMNNAME_BAY_TradingUnit_UU, BAY_TradingUnit_UU);
	}

	/** Get BAY_TradingUnit_UU.
		@return BAY_TradingUnit_UU	  */
	public String getBAY_TradingUnit_UU () 
	{
		return (String)get_Value(COLUMNNAME_BAY_TradingUnit_UU);
	}

	/** Set Besonderheit.
		@param Besonderheit Besonderheit	  */
	public void setBesonderheit (String Besonderheit)
	{
		set_Value (COLUMNNAME_Besonderheit, Besonderheit);
	}

	/** Get Besonderheit.
		@return Besonderheit	  */
	public String getBesonderheit () 
	{
		return (String)get_Value(COLUMNNAME_Besonderheit);
	}

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException
    {
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_Name)
			.getPO(getC_UOM_ID(), get_TrxName());	}

	/** Set UOM.
		@param C_UOM_ID 
		Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID)
	{
		if (C_UOM_ID < 1) 
			set_Value (COLUMNNAME_C_UOM_ID, null);
		else 
			set_Value (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
	}

	/** Get UOM.
		@return Unit of Measure
	  */
	public int getC_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getDepositPackage() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getDepositPackage_ID(), get_TrxName());	}

	/** Set Deposit Package Product.
		@param DepositPackage_ID Deposit Package Product	  */
	public void setDepositPackage_ID (int DepositPackage_ID)
	{
		if (DepositPackage_ID < 1) 
			set_Value (COLUMNNAME_DepositPackage_ID, null);
		else 
			set_Value (COLUMNNAME_DepositPackage_ID, Integer.valueOf(DepositPackage_ID));
	}

	/** Get Deposit Package Product.
		@return Deposit Package Product	  */
	public int getDepositPackage_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DepositPackage_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getDepositUnit() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getDepositUnit_ID(), get_TrxName());	}

	/** Set Deposit Unit.
		@param DepositUnit_ID Deposit Unit	  */
	public void setDepositUnit_ID (int DepositUnit_ID)
	{
		if (DepositUnit_ID < 1) 
			set_Value (COLUMNNAME_DepositUnit_ID, null);
		else 
			set_Value (COLUMNNAME_DepositUnit_ID, Integer.valueOf(DepositUnit_ID));
	}

	/** Get Deposit Unit.
		@return Deposit Unit	  */
	public int getDepositUnit_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DepositUnit_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Deposit Value.
		@param DepositValue Deposit Value	  */
	public void setDepositValue (BigDecimal DepositValue)
	{
		set_Value (COLUMNNAME_DepositValue, DepositValue);
	}

	/** Get Deposit Value.
		@return Deposit Value	  */
	public BigDecimal getDepositValue () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_DepositValue);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Einheit.
		@param Einheit Einheit	  */
	public void setEinheit (String Einheit)
	{
		set_Value (COLUMNNAME_Einheit, Einheit);
	}

	/** Get Einheit.
		@return Einheit	  */
	public String getEinheit () 
	{
		return (String)get_Value(COLUMNNAME_Einheit);
	}

	/** Set Comment/Help.
		@param Help 
		Comment or Hint
	  */
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp () 
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** Set Inhalt.
		@param Inhalt Inhalt	  */
	public void setInhalt (String Inhalt)
	{
		set_Value (COLUMNNAME_Inhalt, Inhalt);
	}

	/** Get Inhalt.
		@return Inhalt	  */
	public String getInhalt () 
	{
		return (String)get_Value(COLUMNNAME_Inhalt);
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getName());
    }

	/** Set UnitsPerPack.
		@param UnitsPerPack 
		The Units Per Pack indicates the no of units of a product packed together.
	  */
	public void setUnitsPerPack (int UnitsPerPack)
	{
		set_ValueNoCheck (COLUMNNAME_UnitsPerPack, Integer.valueOf(UnitsPerPack));
	}

	/** Get UnitsPerPack.
		@return The Units Per Pack indicates the no of units of a product packed together.
	  */
	public int getUnitsPerPack () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_UnitsPerPack);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}

	/** Set Volume.
		@param Volume 
		Volume of a product
	  */
	public void setVolume (BigDecimal Volume)
	{
		set_ValueNoCheck (COLUMNNAME_Volume, Volume);
	}

	/** Get Volume.
		@return Volume of a product
	  */
	public BigDecimal getVolume () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Volume);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Weight.
		@param Weight 
		Weight of a product
	  */
	public void setWeight (BigDecimal Weight)
	{
		set_ValueNoCheck (COLUMNNAME_Weight, Weight);
	}

	/** Get Weight.
		@return Weight of a product
	  */
	public BigDecimal getWeight () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Weight);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}