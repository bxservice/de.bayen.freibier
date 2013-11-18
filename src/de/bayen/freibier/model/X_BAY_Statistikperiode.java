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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Model for BAY_Statistikperiode
 *  @author iDempiere (generated) 
 *  @version Release 1.0c - $Id$ */
public class X_BAY_Statistikperiode extends PO implements I_BAY_Statistikperiode, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20131109L;

    /** Standard Constructor */
    public X_BAY_Statistikperiode (Properties ctx, int BAY_Statistikperiode_ID, String trxName)
    {
      super (ctx, BAY_Statistikperiode_ID, trxName);
      /** if (BAY_Statistikperiode_ID == 0)
        {
			setBAY_Statistikperiode_ID (0);
			setEndDate (new Timestamp( System.currentTimeMillis() ));
			setName (null);
			setStartDate (new Timestamp( System.currentTimeMillis() ));
			setStatistiktyp (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_BAY_Statistikperiode (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_BAY_Statistikperiode[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

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

	/** Set BAY_Statistikperiode_UU.
		@param BAY_Statistikperiode_UU BAY_Statistikperiode_UU	  */
	public void setBAY_Statistikperiode_UU (String BAY_Statistikperiode_UU)
	{
		set_Value (COLUMNNAME_BAY_Statistikperiode_UU, BAY_Statistikperiode_UU);
	}

	/** Get BAY_Statistikperiode_UU.
		@return BAY_Statistikperiode_UU	  */
	public String getBAY_Statistikperiode_UU () 
	{
		return (String)get_Value(COLUMNNAME_BAY_Statistikperiode_UU);
	}

	/** Set erzeuge NGV Export.
		@param createNGVExport erzeuge NGV Export	  */
	public void setcreateNGVExport (String createNGVExport)
	{
		set_Value (COLUMNNAME_createNGVExport, createNGVExport);
	}

	/** Get erzeuge NGV Export.
		@return erzeuge NGV Export	  */
	public String getcreateNGVExport () 
	{
		return (String)get_Value(COLUMNNAME_createNGVExport);
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

	/** Set erzeuge Meldungen.
		@param erzeugeMeldungen erzeuge Meldungen	  */
	public void seterzeugeMeldungen (String erzeugeMeldungen)
	{
		set_Value (COLUMNNAME_erzeugeMeldungen, erzeugeMeldungen);
	}

	/** Get erzeuge Meldungen.
		@return erzeuge Meldungen	  */
	public String geterzeugeMeldungen () 
	{
		return (String)get_Value(COLUMNNAME_erzeugeMeldungen);
	}

	/** Set Import Table.
		@param ImportTable 
		Import Table Columns from Database
	  */
	public void setImportTable (String ImportTable)
	{
		set_Value (COLUMNNAME_ImportTable, ImportTable);
	}

	/** Get Import Table.
		@return Import Table Columns from Database
	  */
	public String getImportTable () 
	{
		return (String)get_Value(COLUMNNAME_ImportTable);
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

	/** jährlich aus Durst = JD */
	public static final String STATISTIKTYP_JährlichAusDurst = "JD";
	/** jährlich Einkauf aus Durst = JED */
	public static final String STATISTIKTYP_JährlichEinkaufAusDurst = "JED";
	/** monatlich aus Durst = MD */
	public static final String STATISTIKTYP_MonatlichAusDurst = "MD";
	/** monatlich Einkauf aus Durst = MED */
	public static final String STATISTIKTYP_MonatlichEinkaufAusDurst = "MED";
	/** NGV Absatzstatistik = NGVA */
	public static final String STATISTIKTYP_NGVAbsatzstatistik = "NGVA";
	/** NGV Bezugsstatistik = NGVB */
	public static final String STATISTIKTYP_NGVBezugsstatistik = "NGVB";
	/** Set Statistiktyp.
		@param Statistiktyp Statistiktyp	  */
	public void setStatistiktyp (String Statistiktyp)
	{

		set_Value (COLUMNNAME_Statistiktyp, Statistiktyp);
	}

	/** Get Statistiktyp.
		@return Statistiktyp	  */
	public String getStatistiktyp () 
	{
		return (String)get_Value(COLUMNNAME_Statistiktyp);
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