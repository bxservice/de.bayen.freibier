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

/** Generated Model for BAY_Contract
 *  @author iDempiere (generated) 
 *  @version Release 2.0 - $Id$ */
public class X_BAY_Contract extends PO implements I_BAY_Contract, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140129L;

    /** Standard Constructor */
    public X_BAY_Contract (Properties ctx, int BAY_Contract_ID, String trxName)
    {
      super (ctx, BAY_Contract_ID, trxName);
      /** if (BAY_Contract_ID == 0)
        {
			setBAY_Contract_ID (0);
			setIsSOTrx (true);
// Y
			setLoanActive (true);
// 'Y'
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_BAY_Contract (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_BAY_Contract[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Vertrag.
		@param BAY_Contract_ID Vertrag	  */
	public void setBAY_Contract_ID (int BAY_Contract_ID)
	{
		if (BAY_Contract_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BAY_Contract_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BAY_Contract_ID, Integer.valueOf(BAY_Contract_ID));
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

	/** Set Category.
		@param Category Category	  */
	public void setCategory (String Category)
	{
		set_Value (COLUMNNAME_Category, Category);
	}

	/** Get Category.
		@return Category	  */
	public String getCategory () 
	{
		return (String)get_Value(COLUMNNAME_Category);
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

	/** Set Create Interest Calculation.
		@param CreateInterestCalculation Create Interest Calculation	  */
	public void setCreateInterestCalculation (String CreateInterestCalculation)
	{
		set_Value (COLUMNNAME_CreateInterestCalculation, CreateInterestCalculation);
	}

	/** Get Create Interest Calculation.
		@return Create Interest Calculation	  */
	public String getCreateInterestCalculation () 
	{
		return (String)get_Value(COLUMNNAME_CreateInterestCalculation);
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

	/** InvoiceFrequency AD_Reference_ID=196 */
	public static final int INVOICEFREQUENCY_AD_Reference_ID=196;
	/** Month = M */
	public static final String INVOICEFREQUENCY_Month = "M";
	/** Quarter = Q */
	public static final String INVOICEFREQUENCY_Quarter = "Q";
	/** Year = Y */
	public static final String INVOICEFREQUENCY_Year = "Y";
	/** Set Invoice Frequency.
		@param InvoiceFrequency 
		How often invoices will be generated
	  */
	public void setInvoiceFrequency (String InvoiceFrequency)
	{

		set_Value (COLUMNNAME_InvoiceFrequency, InvoiceFrequency);
	}

	/** Get Invoice Frequency.
		@return How often invoices will be generated
	  */
	public String getInvoiceFrequency () 
	{
		return (String)get_Value(COLUMNNAME_InvoiceFrequency);
	}

	/** Set Sales Transaction.
		@param IsSOTrx 
		This is a Sales Transaction
	  */
	public void setIsSOTrx (boolean IsSOTrx)
	{
		set_Value (COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
	}

	/** Get Sales Transaction.
		@return This is a Sales Transaction
	  */
	public boolean isSOTrx () 
	{
		Object oo = get_Value(COLUMNNAME_IsSOTrx);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Loan Active.
		@param LoanActive Loan Active	  */
	public void setLoanActive (boolean LoanActive)
	{
		set_Value (COLUMNNAME_LoanActive, Boolean.valueOf(LoanActive));
	}

	/** Get Loan Active.
		@return Loan Active	  */
	public boolean isLoanActive () 
	{
		Object oo = get_Value(COLUMNNAME_LoanActive);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	/** vorbereitet = 1 */
	public static final String STATUSCODE_Vorbereitet = "1";
	/** aktiv = 2 */
	public static final String STATUSCODE_Aktiv = "2";
	/** gestört = 3 */
	public static final String STATUSCODE_Gestört = "3";
	/** Bonikonto = 8 */
	public static final String STATUSCODE_Bonikonto = "8";
	/** beendet = 9 */
	public static final String STATUSCODE_Beendet = "9";
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

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_ValueNoCheck (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getValue());
    }
}