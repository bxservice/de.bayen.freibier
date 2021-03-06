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
package de.bayen.freibier.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for BAY_Config
 *  @author iDempiere (generated) 
 *  @version Release 2.0
 */
@SuppressWarnings("all")
public interface I_BAY_Config 
{

    /** TableName=BAY_Config */
    public static final String Table_Name = "BAY_Config";

    /** AD_Table_ID=1000011 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 2 - Client 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(2);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name BAY_Config_ID */
    public static final String COLUMNNAME_BAY_Config_ID = "BAY_Config_ID";

	/** Set BAY_Config	  */
	public void setBAY_Config_ID (int BAY_Config_ID);

	/** Get BAY_Config	  */
	public int getBAY_Config_ID();

    /** Column name BAY_Config_UU */
    public static final String COLUMNNAME_BAY_Config_UU = "BAY_Config_UU";

	/** Set BAY_Config_UU	  */
	public void setBAY_Config_UU (String BAY_Config_UU);

	/** Get BAY_Config_UU	  */
	public String getBAY_Config_UU();

    /** Column name ChargeCustomerLoan_ID */
    public static final String COLUMNNAME_ChargeCustomerLoan_ID = "ChargeCustomerLoan_ID";

	/** Set Charge Customer Loan_ID	  */
	public void setChargeCustomerLoan_ID (int ChargeCustomerLoan_ID);

	/** Get Charge Customer Loan_ID	  */
	public int getChargeCustomerLoan_ID();

	public org.compiere.model.I_C_Charge getChargeCustomerLoan() throws RuntimeException;

    /** Column name ChargeInterestExpense_ID */
    public static final String COLUMNNAME_ChargeInterestExpense_ID = "ChargeInterestExpense_ID";

	/** Set ChargeInterestExpense_ID	  */
	public void setChargeInterestExpense_ID (int ChargeInterestExpense_ID);

	/** Get ChargeInterestExpense_ID	  */
	public int getChargeInterestExpense_ID();

	public org.compiere.model.I_C_Charge getChargeInterestExpense() throws RuntimeException;

    /** Column name ChargeInterestRevenue_ID */
    public static final String COLUMNNAME_ChargeInterestRevenue_ID = "ChargeInterestRevenue_ID";

	/** Set ChargeInterestRevenue_ID	  */
	public void setChargeInterestRevenue_ID (int ChargeInterestRevenue_ID);

	/** Get ChargeInterestRevenue_ID	  */
	public int getChargeInterestRevenue_ID();

	public org.compiere.model.I_C_Charge getChargeInterestRevenue() throws RuntimeException;

    /** Column name ChargeVendorLoan_ID */
    public static final String COLUMNNAME_ChargeVendorLoan_ID = "ChargeVendorLoan_ID";

	/** Set Charge Vendor Loan_ID	  */
	public void setChargeVendorLoan_ID (int ChargeVendorLoan_ID);

	/** Get Charge Vendor Loan_ID	  */
	public int getChargeVendorLoan_ID();

	public org.compiere.model.I_C_Charge getChargeVendorLoan() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name DocType_InterestCustomer_ID */
    public static final String COLUMNNAME_DocType_InterestCustomer_ID = "DocType_InterestCustomer_ID";

	/** Set DocType_InterestCustomer_ID	  */
	public void setDocType_InterestCustomer_ID (int DocType_InterestCustomer_ID);

	/** Get DocType_InterestCustomer_ID	  */
	public int getDocType_InterestCustomer_ID();

	public org.compiere.model.I_C_DocType getDocType_InterestCustomer() throws RuntimeException;

    /** Column name DocType_InterestVendor_ID */
    public static final String COLUMNNAME_DocType_InterestVendor_ID = "DocType_InterestVendor_ID";

	/** Set DocType_InterestVendor_ID	  */
	public void setDocType_InterestVendor_ID (int DocType_InterestVendor_ID);

	/** Get DocType_InterestVendor_ID	  */
	public int getDocType_InterestVendor_ID();

	public org.compiere.model.I_C_DocType getDocType_InterestVendor() throws RuntimeException;

    /** Column name DocType_LoanCustomer_ID */
    public static final String COLUMNNAME_DocType_LoanCustomer_ID = "DocType_LoanCustomer_ID";

	/** Set DocType_LoanCustomer_ID	  */
	public void setDocType_LoanCustomer_ID (int DocType_LoanCustomer_ID);

	/** Get DocType_LoanCustomer_ID	  */
	public int getDocType_LoanCustomer_ID();

	public org.compiere.model.I_C_DocType getDocType_LoanCustomer() throws RuntimeException;

    /** Column name DocType_LoanVendor_ID */
    public static final String COLUMNNAME_DocType_LoanVendor_ID = "DocType_LoanVendor_ID";

	/** Set DocType_LoanVendor_ID	  */
	public void setDocType_LoanVendor_ID (int DocType_LoanVendor_ID);

	/** Get DocType_LoanVendor_ID	  */
	public int getDocType_LoanVendor_ID();

	public org.compiere.model.I_C_DocType getDocType_LoanVendor() throws RuntimeException;

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
}
