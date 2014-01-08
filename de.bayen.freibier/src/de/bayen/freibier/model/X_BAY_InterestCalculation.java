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

/** Generated Model for BAY_InterestCalculation
 *  @author iDempiere (generated) 
 *  @version Release 2.0 - $Id$ */
public class X_BAY_InterestCalculation extends PO implements I_BAY_InterestCalculation, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140108L;

    /** Standard Constructor */
    public X_BAY_InterestCalculation (Properties ctx, int BAY_InterestCalculation_ID, String trxName)
    {
      super (ctx, BAY_InterestCalculation_ID, trxName);
      /** if (BAY_InterestCalculation_ID == 0)
        {
			setBAY_InterestCalculation_ID (0);
			setDocumentNo (null);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_BAY_InterestCalculation (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_BAY_InterestCalculation[")
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

	/** Set Zinsberechnung.
		@param BAY_InterestCalculation_ID Zinsberechnung	  */
	public void setBAY_InterestCalculation_ID (int BAY_InterestCalculation_ID)
	{
		if (BAY_InterestCalculation_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BAY_InterestCalculation_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BAY_InterestCalculation_ID, Integer.valueOf(BAY_InterestCalculation_ID));
	}

	/** Get Zinsberechnung.
		@return Zinsberechnung	  */
	public int getBAY_InterestCalculation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BAY_InterestCalculation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set BAY_InterestCalculation_UU.
		@param BAY_InterestCalculation_UU BAY_InterestCalculation_UU	  */
	public void setBAY_InterestCalculation_UU (String BAY_InterestCalculation_UU)
	{
		set_Value (COLUMNNAME_BAY_InterestCalculation_UU, BAY_InterestCalculation_UU);
	}

	/** Get BAY_InterestCalculation_UU.
		@return BAY_InterestCalculation_UU	  */
	public String getBAY_InterestCalculation_UU () 
	{
		return (String)get_Value(COLUMNNAME_BAY_InterestCalculation_UU);
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

	/** Set Document Date.
		@param DateDoc 
		Date of the Document
	  */
	public void setDateDoc (Timestamp DateDoc)
	{
		set_Value (COLUMNNAME_DateDoc, DateDoc);
	}

	/** Get Document Date.
		@return Date of the Document
	  */
	public Timestamp getDateDoc () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateDoc);
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

	/** Set Document No.
		@param DocumentNo 
		Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo)
	{
		set_ValueNoCheck (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
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

	/** Set Interest Amount.
		@param InterestAmt 
		Interest Amount
	  */
	public void setInterestAmt (BigDecimal InterestAmt)
	{
		set_Value (COLUMNNAME_InterestAmt, InterestAmt);
	}

	/** Get Interest Amount.
		@return Interest Amount
	  */
	public BigDecimal getInterestAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_InterestAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Total Amount.
		@param TotalAmt 
		Total Amount
	  */
	public void setTotalAmt (BigDecimal TotalAmt)
	{
		set_ValueNoCheck (COLUMNNAME_TotalAmt, TotalAmt);
	}

	/** Get Total Amount.
		@return Total Amount
	  */
	public BigDecimal getTotalAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Total Lines.
		@param TotalLines 
		Total of all document lines
	  */
	public void setTotalLines (BigDecimal TotalLines)
	{
		set_ValueNoCheck (COLUMNNAME_TotalLines, TotalLines);
	}

	/** Get Total Lines.
		@return Total of all document lines
	  */
	public BigDecimal getTotalLines () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalLines);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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