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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

/** Generated Model for BAY_StatAdjust
 *  @author iDempiere (generated) 
 *  @version Release 2.0 - $Id$ */
public class X_BAY_StatAdjust extends PO implements I_BAY_StatAdjust, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140114L;

    /** Standard Constructor */
    public X_BAY_StatAdjust (Properties ctx, int BAY_StatAdjust_ID, String trxName)
    {
      super (ctx, BAY_StatAdjust_ID, trxName);
      /** if (BAY_StatAdjust_ID == 0)
        {
			setBAY_StatAdjust_ID (0);
			setCommand (null);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_BAY_StatAdjust (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_BAY_StatAdjust[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Amount.
		@param Amount 
		Amount in a defined currency
	  */
	public void setAmount (BigDecimal Amount)
	{
		set_Value (COLUMNNAME_Amount, Amount);
	}

	/** Get Amount.
		@return Amount in a defined currency
	  */
	public BigDecimal getAmount () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Amount);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Statistik-Anpassung.
		@param BAY_StatAdjust_ID Statistik-Anpassung	  */
	public void setBAY_StatAdjust_ID (int BAY_StatAdjust_ID)
	{
		if (BAY_StatAdjust_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BAY_StatAdjust_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BAY_StatAdjust_ID, Integer.valueOf(BAY_StatAdjust_ID));
	}

	/** Get Statistik-Anpassung.
		@return Statistik-Anpassung	  */
	public int getBAY_StatAdjust_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BAY_StatAdjust_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set BAY_StatAdjust_UU.
		@param BAY_StatAdjust_UU BAY_StatAdjust_UU	  */
	public void setBAY_StatAdjust_UU (String BAY_StatAdjust_UU)
	{
		set_Value (COLUMNNAME_BAY_StatAdjust_UU, BAY_StatAdjust_UU);
	}

	/** Get BAY_StatAdjust_UU.
		@return BAY_StatAdjust_UU	  */
	public String getBAY_StatAdjust_UU () 
	{
		return (String)get_Value(COLUMNNAME_BAY_StatAdjust_UU);
	}

	public org.compiere.model.I_C_BP_Group getC_BP_Group() throws RuntimeException
    {
		return (org.compiere.model.I_C_BP_Group)MTable.get(getCtx(), org.compiere.model.I_C_BP_Group.Table_Name)
			.getPO(getC_BP_Group_ID(), get_TrxName());	}

	/** Set Business Partner Group.
		@param C_BP_Group_ID 
		Business Partner Group
	  */
	public void setC_BP_Group_ID (int C_BP_Group_ID)
	{
		if (C_BP_Group_ID < 1) 
			set_Value (COLUMNNAME_C_BP_Group_ID, null);
		else 
			set_Value (COLUMNNAME_C_BP_Group_ID, Integer.valueOf(C_BP_Group_ID));
	}

	/** Get Business Partner Group.
		@return Business Partner Group
	  */
	public int getC_BP_Group_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BP_Group_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** addieren auf einen Artikel = addieren */
	public static final String COMMAND_AddierenAufEinenArtikel = "addieren";
	/** Artikelweise auf Mindestwert setzen = mindestwert */
	public static final String COMMAND_ArtikelweiseAufMindestwertSetzen = "mindestwert";
	/** löschen = löschen */
	public static final String COMMAND_Löschen = "löschen";
	/** umbuchen auf anderen Kunden = umbuchen */
	public static final String COMMAND_UmbuchenAufAnderenKunden = "umbuchen";
	/** Set Command.
		@param Command Command	  */
	public void setCommand (String Command)
	{

		set_Value (COLUMNNAME_Command, Command);
	}

	/** Get Command.
		@return Command	  */
	public String getCommand () 
	{
		return (String)get_Value(COLUMNNAME_Command);
	}

	/** Hektoliter = hl */
	public static final String COMMANDUOM_Hektoliter = "hl";
	/** Prozent = proz */
	public static final String COMMANDUOM_Prozent = "proz";
	/** Stück = stk */
	public static final String COMMANDUOM_Stück = "stk";
	/** Set Maßeinheit für Anweisung.
		@param CommandUom Maßeinheit für Anweisung	  */
	public void setCommandUom (String CommandUom)
	{

		set_Value (COLUMNNAME_CommandUom, CommandUom);
	}

	/** Get Maßeinheit für Anweisung.
		@return Maßeinheit für Anweisung	  */
	public String getCommandUom () 
	{
		return (String)get_Value(COLUMNNAME_CommandUom);
	}

	public org.compiere.model.I_C_BPartner getCustomer() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_Name)
			.getPO(getCustomer_ID(), get_TrxName());	}

	/** Set Customer.
		@param Customer_ID Customer	  */
	public void setCustomer_ID (int Customer_ID)
	{
		if (Customer_ID < 1) 
			set_Value (COLUMNNAME_Customer_ID, null);
		else 
			set_Value (COLUMNNAME_Customer_ID, Integer.valueOf(Customer_ID));
	}

	/** Get Customer.
		@return Customer	  */
	public int getCustomer_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Customer_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set End Date.
		@param EndDate 
		Last effective date (inclusive)
	  */
	public void setEndDate (Timestamp EndDate)
	{
		set_Value (COLUMNNAME_EndDate, EndDate);
	}

	/** Get End Date.
		@return Last effective date (inclusive)
	  */
	public Timestamp getEndDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_EndDate);
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

	public org.compiere.model.I_M_Product_Category getM_Product_Category() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product_Category)MTable.get(getCtx(), org.compiere.model.I_M_Product_Category.Table_Name)
			.getPO(getM_Product_Category_ID(), get_TrxName());	}

	/** Set Product Category.
		@param M_Product_Category_ID 
		Category of a Product
	  */
	public void setM_Product_Category_ID (int M_Product_Category_ID)
	{
		if (M_Product_Category_ID < 1) 
			set_Value (COLUMNNAME_M_Product_Category_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_Category_ID, Integer.valueOf(M_Product_Category_ID));
	}

	/** Get Product Category.
		@return Category of a Product
	  */
	public int getM_Product_Category_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_Category_ID);
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
			set_Value (COLUMNNAME_M_Product_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
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

	/** Set Start Date.
		@param StartDate 
		First effective day (inclusive)
	  */
	public void setStartDate (Timestamp StartDate)
	{
		set_Value (COLUMNNAME_StartDate, StartDate);
	}

	/** Get Start Date.
		@return First effective day (inclusive)
	  */
	public Timestamp getStartDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_StartDate);
	}

	public org.compiere.model.I_C_BPartner getTargetCustomer() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_Name)
			.getPO(getTargetCustomer_ID(), get_TrxName());	}

	/** Set Target Customer.
		@param TargetCustomer_ID Target Customer	  */
	public void setTargetCustomer_ID (int TargetCustomer_ID)
	{
		if (TargetCustomer_ID < 1) 
			set_Value (COLUMNNAME_TargetCustomer_ID, null);
		else 
			set_Value (COLUMNNAME_TargetCustomer_ID, Integer.valueOf(TargetCustomer_ID));
	}

	/** Get Target Customer.
		@return Target Customer	  */
	public int getTargetCustomer_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TargetCustomer_ID);
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

	public org.compiere.model.I_C_BPartner getVendor() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_Name)
			.getPO(getVendor_ID(), get_TrxName());	}

	/** Set Vendor.
		@param Vendor_ID 
		The Vendor of the product/service
	  */
	public void setVendor_ID (int Vendor_ID)
	{
		if (Vendor_ID < 1) 
			set_Value (COLUMNNAME_Vendor_ID, null);
		else 
			set_Value (COLUMNNAME_Vendor_ID, Integer.valueOf(Vendor_ID));
	}

	/** Get Vendor.
		@return The Vendor of the product/service
	  */
	public int getVendor_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Vendor_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}