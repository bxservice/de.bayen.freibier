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

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Model for RV_BAY_Contract
 *  @author iDempiere (generated) 
 *  @version Release 1.0c - $Id$ */
public class X_RV_BAY_Contract extends PO implements I_RV_BAY_Contract, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20131107L;

    /** Standard Constructor */
    public X_RV_BAY_Contract (Properties ctx, int RV_BAY_Contract_ID, String trxName)
    {
      super (ctx, RV_BAY_Contract_ID, trxName);
      /** if (RV_BAY_Contract_ID == 0)
        {
        } */
    }

    /** Load Constructor */
    public X_RV_BAY_Contract (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_RV_BAY_Contract[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_BAY_Contract getBAY_Contract() throws RuntimeException
    {
		return (I_BAY_Contract)MTable.get(getCtx(), I_BAY_Contract.Table_Name)
			.getPO(getBAY_Contract_ID(), get_TrxName());	}

	/** Set Vertrag.
		@param BAY_Contract_ID Vertrag	  */
	public void setBAY_Contract_ID (int BAY_Contract_ID)
	{
		if (BAY_Contract_ID < 1) 
			set_Value (COLUMNNAME_BAY_Contract_ID, null);
		else 
			set_Value (COLUMNNAME_BAY_Contract_ID, Integer.valueOf(BAY_Contract_ID));
	}

	/** Get Vertrag.
		@return Vertrag	  */
	public int getBAY_Contract_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BAY_Contract_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set BAY_Contract_UU.
		@param BAY_Contract_UU BAY_Contract_UU	  */
	public void setBAY_Contract_UU (String BAY_Contract_UU)
	{
		set_Value (COLUMNNAME_BAY_Contract_UU, BAY_Contract_UU);
	}

	/** Get BAY_Contract_UU.
		@return BAY_Contract_UU	  */
	public String getBAY_Contract_UU () 
	{
		return (String)get_Value(COLUMNNAME_BAY_Contract_UU);
	}

	/** Set bp_name.
		@param bp_name bp_name	  */
	public void setbp_name (String bp_name)
	{
		set_Value (COLUMNNAME_bp_name, bp_name);
	}

	/** Get bp_name.
		@return bp_name	  */
	public String getbp_name () 
	{
		return (String)get_Value(COLUMNNAME_bp_name);
	}

	/** Set bp_value.
		@param bp_value bp_value	  */
	public void setbp_value (String bp_value)
	{
		set_Value (COLUMNNAME_bp_value, bp_value);
	}

	/** Get bp_value.
		@return bp_value	  */
	public String getbp_value () 
	{
		return (String)get_Value(COLUMNNAME_bp_value);
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
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
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

	public org.compiere.model.I_C_SalesRegion getC_SalesRegion() throws RuntimeException
    {
		return (org.compiere.model.I_C_SalesRegion)MTable.get(getCtx(), org.compiere.model.I_C_SalesRegion.Table_Name)
			.getPO(getC_SalesRegion_ID(), get_TrxName());	}

	/** Set Sales Region.
		@param C_SalesRegion_ID 
		Sales coverage region
	  */
	public void setC_SalesRegion_ID (int C_SalesRegion_ID)
	{
		if (C_SalesRegion_ID < 1) 
			set_Value (COLUMNNAME_C_SalesRegion_ID, null);
		else 
			set_Value (COLUMNNAME_C_SalesRegion_ID, Integer.valueOf(C_SalesRegion_ID));
	}

	/** Get Sales Region.
		@return Sales coverage region
	  */
	public int getC_SalesRegion_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_SalesRegion_ID);
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

	/** Set Contract Report View.
		@param RV_BAY_Contract_ID Contract Report View	  */
	public void setRV_BAY_Contract_ID (int RV_BAY_Contract_ID)
	{
		if (RV_BAY_Contract_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_RV_BAY_Contract_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_RV_BAY_Contract_ID, Integer.valueOf(RV_BAY_Contract_ID));
	}

	/** Get Contract Report View.
		@return Contract Report View	  */
	public int getRV_BAY_Contract_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_RV_BAY_Contract_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set sr_name.
		@param sr_name sr_name	  */
	public void setsr_name (String sr_name)
	{
		set_Value (COLUMNNAME_sr_name, sr_name);
	}

	/** Get sr_name.
		@return sr_name	  */
	public String getsr_name () 
	{
		return (String)get_Value(COLUMNNAME_sr_name);
	}

	/** Set sr_value.
		@param sr_value sr_value	  */
	public void setsr_value (String sr_value)
	{
		set_Value (COLUMNNAME_sr_value, sr_value);
	}

	/** Get sr_value.
		@return sr_value	  */
	public String getsr_value () 
	{
		return (String)get_Value(COLUMNNAME_sr_value);
	}

	/** Set Status Code.
		@param StatusCode Status Code	  */
	public void setStatusCode (String StatusCode)
	{
		set_Value (COLUMNNAME_StatusCode, StatusCode);
	}

	/** Get Status Code.
		@return Status Code	  */
	public String getStatusCode () 
	{
		return (String)get_Value(COLUMNNAME_StatusCode);
	}

	/** Set status_name.
		@param status_name status_name	  */
	public void setstatus_name (String status_name)
	{
		set_Value (COLUMNNAME_status_name, status_name);
	}

	/** Get status_name.
		@return status_name	  */
	public String getstatus_name () 
	{
		return (String)get_Value(COLUMNNAME_status_name);
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
}