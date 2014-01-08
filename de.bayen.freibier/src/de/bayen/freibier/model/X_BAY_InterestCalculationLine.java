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

/** Generated Model for BAY_InterestCalculationLine
 *  @author iDempiere (generated) 
 *  @version Release 2.0 - $Id$ */
public class X_BAY_InterestCalculationLine extends PO implements I_BAY_InterestCalculationLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140108L;

    /** Standard Constructor */
    public X_BAY_InterestCalculationLine (Properties ctx, int BAY_InterestCalculationLine_ID, String trxName)
    {
      super (ctx, BAY_InterestCalculationLine_ID, trxName);
      /** if (BAY_InterestCalculationLine_ID == 0)
        {
			setBAY_InterestCalculationLine_ID (0);
			setDateTrx (new Timestamp( System.currentTimeMillis() ));
			setInterestPercent (Env.ZERO);
// @InterestPercent@
			setLine (0);
// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM C_BAY_InterestCalculationLine WHERE BAY_InterestCalculation_ID=@BAY_InterestCalculation_ID@
        } */
    }

    /** Load Constructor */
    public X_BAY_InterestCalculationLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_BAY_InterestCalculationLine[")
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

	public I_BAY_InterestCalculation getBAY_InterestCalculation() throws RuntimeException
    {
		return (I_BAY_InterestCalculation)MTable.get(getCtx(), I_BAY_InterestCalculation.Table_Name)
			.getPO(getBAY_InterestCalculation_ID(), get_TrxName());	}

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

	/** Set Zinsabrechnung Zeile.
		@param BAY_InterestCalculationLine_ID Zinsabrechnung Zeile	  */
	public void setBAY_InterestCalculationLine_ID (int BAY_InterestCalculationLine_ID)
	{
		if (BAY_InterestCalculationLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BAY_InterestCalculationLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BAY_InterestCalculationLine_ID, Integer.valueOf(BAY_InterestCalculationLine_ID));
	}

	/** Get Zinsabrechnung Zeile.
		@return Zinsabrechnung Zeile	  */
	public int getBAY_InterestCalculationLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BAY_InterestCalculationLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set BAY_InterestCalculationLine_UU.
		@param BAY_InterestCalculationLine_UU BAY_InterestCalculationLine_UU	  */
	public void setBAY_InterestCalculationLine_UU (String BAY_InterestCalculationLine_UU)
	{
		set_Value (COLUMNNAME_BAY_InterestCalculationLine_UU, BAY_InterestCalculationLine_UU);
	}

	/** Get BAY_InterestCalculationLine_UU.
		@return BAY_InterestCalculationLine_UU	  */
	public String getBAY_InterestCalculationLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_BAY_InterestCalculationLine_UU);
	}

	/** Set Transaction Date.
		@param DateTrx 
		Transaction Date
	  */
	public void setDateTrx (Timestamp DateTrx)
	{
		set_ValueNoCheck (COLUMNNAME_DateTrx, DateTrx);
	}

	/** Get Transaction Date.
		@return Transaction Date
	  */
	public Timestamp getDateTrx () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateTrx);
	}

	/** Set Days.
		@param Days Days	  */
	public void setDays (int Days)
	{
		set_Value (COLUMNNAME_Days, Integer.valueOf(Days));
	}

	/** Get Days.
		@return Days	  */
	public int getDays () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Days);
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

	/** Set Interest in percent.
		@param InterestPercent 
		Percentage interest to charge on overdue invoices
	  */
	public void setInterestPercent (BigDecimal InterestPercent)
	{
		set_Value (COLUMNNAME_InterestPercent, InterestPercent);
	}

	/** Get Interest in percent.
		@return Percentage interest to charge on overdue invoices
	  */
	public BigDecimal getInterestPercent () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_InterestPercent);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Line No.
		@param Line 
		Unique line for this document
	  */
	public void setLine (int Line)
	{
		set_ValueNoCheck (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Line Total.
		@param LineTotalAmt 
		Total line amount incl. Tax
	  */
	public void setLineTotalAmt (BigDecimal LineTotalAmt)
	{
		set_Value (COLUMNNAME_LineTotalAmt, LineTotalAmt);
	}

	/** Get Line Total.
		@return Total line amount incl. Tax
	  */
	public BigDecimal getLineTotalAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LineTotalAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}