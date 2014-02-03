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

/** Generated Model for BAY_Config
 *  @author iDempiere (generated) 
 *  @version Release 2.0 - $Id$ */
public class X_BAY_Config extends PO implements I_BAY_Config, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140129L;

    /** Standard Constructor */
    public X_BAY_Config (Properties ctx, int BAY_Config_ID, String trxName)
    {
      super (ctx, BAY_Config_ID, trxName);
      /** if (BAY_Config_ID == 0)
        {
			setBAY_Config_ID (0);
        } */
    }

    /** Load Constructor */
    public X_BAY_Config (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 2 - Client 
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
      StringBuffer sb = new StringBuffer ("X_BAY_Config[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set BAY_Config.
		@param BAY_Config_ID BAY_Config	  */
	public void setBAY_Config_ID (int BAY_Config_ID)
	{
		if (BAY_Config_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BAY_Config_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BAY_Config_ID, Integer.valueOf(BAY_Config_ID));
	}

	/** Get BAY_Config.
		@return BAY_Config	  */
	public int getBAY_Config_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BAY_Config_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set BAY_Config_UU.
		@param BAY_Config_UU BAY_Config_UU	  */
	public void setBAY_Config_UU (String BAY_Config_UU)
	{
		set_Value (COLUMNNAME_BAY_Config_UU, BAY_Config_UU);
	}

	/** Get BAY_Config_UU.
		@return BAY_Config_UU	  */
	public String getBAY_Config_UU () 
	{
		return (String)get_Value(COLUMNNAME_BAY_Config_UU);
	}

	public org.compiere.model.I_C_Charge getChargeCustomerLoan() throws RuntimeException
    {
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_Name)
			.getPO(getChargeCustomerLoan_ID(), get_TrxName());	}

	/** Set Charge Customer Loan_ID.
		@param ChargeCustomerLoan_ID Charge Customer Loan_ID	  */
	public void setChargeCustomerLoan_ID (int ChargeCustomerLoan_ID)
	{
		if (ChargeCustomerLoan_ID < 1) 
			set_Value (COLUMNNAME_ChargeCustomerLoan_ID, null);
		else 
			set_Value (COLUMNNAME_ChargeCustomerLoan_ID, Integer.valueOf(ChargeCustomerLoan_ID));
	}

	/** Get Charge Customer Loan_ID.
		@return Charge Customer Loan_ID	  */
	public int getChargeCustomerLoan_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ChargeCustomerLoan_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Charge getChargeInterestExpense() throws RuntimeException
    {
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_Name)
			.getPO(getChargeInterestExpense_ID(), get_TrxName());	}

	/** Set ChargeInterestExpense_ID.
		@param ChargeInterestExpense_ID ChargeInterestExpense_ID	  */
	public void setChargeInterestExpense_ID (int ChargeInterestExpense_ID)
	{
		if (ChargeInterestExpense_ID < 1) 
			set_Value (COLUMNNAME_ChargeInterestExpense_ID, null);
		else 
			set_Value (COLUMNNAME_ChargeInterestExpense_ID, Integer.valueOf(ChargeInterestExpense_ID));
	}

	/** Get ChargeInterestExpense_ID.
		@return ChargeInterestExpense_ID	  */
	public int getChargeInterestExpense_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ChargeInterestExpense_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Charge getChargeInterestRevenue() throws RuntimeException
    {
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_Name)
			.getPO(getChargeInterestRevenue_ID(), get_TrxName());	}

	/** Set ChargeInterestRevenue_ID.
		@param ChargeInterestRevenue_ID ChargeInterestRevenue_ID	  */
	public void setChargeInterestRevenue_ID (int ChargeInterestRevenue_ID)
	{
		if (ChargeInterestRevenue_ID < 1) 
			set_Value (COLUMNNAME_ChargeInterestRevenue_ID, null);
		else 
			set_Value (COLUMNNAME_ChargeInterestRevenue_ID, Integer.valueOf(ChargeInterestRevenue_ID));
	}

	/** Get ChargeInterestRevenue_ID.
		@return ChargeInterestRevenue_ID	  */
	public int getChargeInterestRevenue_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ChargeInterestRevenue_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Charge getChargeVendorLoan() throws RuntimeException
    {
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_Name)
			.getPO(getChargeVendorLoan_ID(), get_TrxName());	}

	/** Set Charge Vendor Loan_ID.
		@param ChargeVendorLoan_ID Charge Vendor Loan_ID	  */
	public void setChargeVendorLoan_ID (int ChargeVendorLoan_ID)
	{
		if (ChargeVendorLoan_ID < 1) 
			set_Value (COLUMNNAME_ChargeVendorLoan_ID, null);
		else 
			set_Value (COLUMNNAME_ChargeVendorLoan_ID, Integer.valueOf(ChargeVendorLoan_ID));
	}

	/** Get Charge Vendor Loan_ID.
		@return Charge Vendor Loan_ID	  */
	public int getChargeVendorLoan_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ChargeVendorLoan_ID);
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

	public org.compiere.model.I_C_DocType getDocType_InterestCustomer() throws RuntimeException
    {
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_Name)
			.getPO(getDocType_InterestCustomer_ID(), get_TrxName());	}

	/** Set DocType_InterestCustomer_ID.
		@param DocType_InterestCustomer_ID DocType_InterestCustomer_ID	  */
	public void setDocType_InterestCustomer_ID (int DocType_InterestCustomer_ID)
	{
		if (DocType_InterestCustomer_ID < 1) 
			set_Value (COLUMNNAME_DocType_InterestCustomer_ID, null);
		else 
			set_Value (COLUMNNAME_DocType_InterestCustomer_ID, Integer.valueOf(DocType_InterestCustomer_ID));
	}

	/** Get DocType_InterestCustomer_ID.
		@return DocType_InterestCustomer_ID	  */
	public int getDocType_InterestCustomer_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DocType_InterestCustomer_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_DocType getDocType_InterestVendor() throws RuntimeException
    {
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_Name)
			.getPO(getDocType_InterestVendor_ID(), get_TrxName());	}

	/** Set DocType_InterestVendor_ID.
		@param DocType_InterestVendor_ID DocType_InterestVendor_ID	  */
	public void setDocType_InterestVendor_ID (int DocType_InterestVendor_ID)
	{
		if (DocType_InterestVendor_ID < 1) 
			set_Value (COLUMNNAME_DocType_InterestVendor_ID, null);
		else 
			set_Value (COLUMNNAME_DocType_InterestVendor_ID, Integer.valueOf(DocType_InterestVendor_ID));
	}

	/** Get DocType_InterestVendor_ID.
		@return DocType_InterestVendor_ID	  */
	public int getDocType_InterestVendor_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DocType_InterestVendor_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_DocType getDocType_LoanCustomer() throws RuntimeException
    {
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_Name)
			.getPO(getDocType_LoanCustomer_ID(), get_TrxName());	}

	/** Set DocType_LoanCustomer_ID.
		@param DocType_LoanCustomer_ID DocType_LoanCustomer_ID	  */
	public void setDocType_LoanCustomer_ID (int DocType_LoanCustomer_ID)
	{
		if (DocType_LoanCustomer_ID < 1) 
			set_Value (COLUMNNAME_DocType_LoanCustomer_ID, null);
		else 
			set_Value (COLUMNNAME_DocType_LoanCustomer_ID, Integer.valueOf(DocType_LoanCustomer_ID));
	}

	/** Get DocType_LoanCustomer_ID.
		@return DocType_LoanCustomer_ID	  */
	public int getDocType_LoanCustomer_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DocType_LoanCustomer_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_DocType getDocType_LoanVendor() throws RuntimeException
    {
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_Name)
			.getPO(getDocType_LoanVendor_ID(), get_TrxName());	}

	/** Set DocType_LoanVendor_ID.
		@param DocType_LoanVendor_ID DocType_LoanVendor_ID	  */
	public void setDocType_LoanVendor_ID (int DocType_LoanVendor_ID)
	{
		if (DocType_LoanVendor_ID < 1) 
			set_Value (COLUMNNAME_DocType_LoanVendor_ID, null);
		else 
			set_Value (COLUMNNAME_DocType_LoanVendor_ID, Integer.valueOf(DocType_LoanVendor_ID));
	}

	/** Get DocType_LoanVendor_ID.
		@return DocType_LoanVendor_ID	  */
	public int getDocType_LoanVendor_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DocType_LoanVendor_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}