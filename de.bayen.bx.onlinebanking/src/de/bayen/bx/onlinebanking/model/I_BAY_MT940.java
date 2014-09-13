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
package de.bayen.bx.onlinebanking.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for BAY_MT940
 *  @author iDempiere (generated) 
 *  @version Release 2.0
 */
@SuppressWarnings("all")
public interface I_BAY_MT940 
{

    /** TableName=BAY_MT940 */
    public static final String Table_Name = "BAY_MT940";

    /** AD_Table_ID=1000000 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 1 - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(1);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name Additional */
    public static final String COLUMNNAME_Additional = "Additional";

	/** Set Additional	  */
	public void setAdditional (String Additional);

	/** Get Additional	  */
	public String getAdditional();

    /** Column name AddKey */
    public static final String COLUMNNAME_AddKey = "AddKey";

	/** Set AddKey	  */
	public void setAddKey (String AddKey);

	/** Get AddKey	  */
	public String getAddKey();

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

    /** Column name Amount */
    public static final String COLUMNNAME_Amount = "Amount";

	/** Set Amount.
	  * Amount in a defined currency
	  */
	public void setAmount (BigDecimal Amount);

	/** Get Amount.
	  * Amount in a defined currency
	  */
	public BigDecimal getAmount();

    /** Column name AmtSource */
    public static final String COLUMNNAME_AmtSource = "AmtSource";

	/** Set Source Amount.
	  * Amount Balance in Source Currency
	  */
	public void setAmtSource (BigDecimal AmtSource);

	/** Get Source Amount.
	  * Amount Balance in Source Currency
	  */
	public BigDecimal getAmtSource();

    /** Column name BAY_MT940_ID */
    public static final String COLUMNNAME_BAY_MT940_ID = "BAY_MT940_ID";

	/** Set MT940	  */
	public void setBAY_MT940_ID (int BAY_MT940_ID);

	/** Get MT940	  */
	public int getBAY_MT940_ID();

    /** Column name BAY_MT940_UU */
    public static final String COLUMNNAME_BAY_MT940_UU = "BAY_MT940_UU";

	/** Set BAY_MT940_UU	  */
	public void setBAY_MT940_UU (String BAY_MT940_UU);

	/** Get BAY_MT940_UU	  */
	public String getBAY_MT940_UU();

    /** Column name BPAccountNo */
    public static final String COLUMNNAME_BPAccountNo = "BPAccountNo";

	/** Set Businesspartner Account No	  */
	public void setBPAccountNo (String BPAccountNo);

	/** Get Businesspartner Account No	  */
	public String getBPAccountNo();

    /** Column name BPName */
    public static final String COLUMNNAME_BPName = "BPName";

	/** Set BP Name	  */
	public void setBPName (String BPName);

	/** Get BP Name	  */
	public String getBPName();

    /** Column name BPRoutingNo */
    public static final String COLUMNNAME_BPRoutingNo = "BPRoutingNo";

	/** Set Businesspartner Routiing No	  */
	public void setBPRoutingNo (String BPRoutingNo);

	/** Get Businesspartner Routiing No	  */
	public String getBPRoutingNo();

    /** Column name C_BankAccount_ID */
    public static final String COLUMNNAME_C_BankAccount_ID = "C_BankAccount_ID";

	/** Set Bank Account.
	  * Account at the Bank
	  */
	public void setC_BankAccount_ID (int C_BankAccount_ID);

	/** Get Bank Account.
	  * Account at the Bank
	  */
	public int getC_BankAccount_ID();

	public org.compiere.model.I_C_BankAccount getC_BankAccount() throws RuntimeException;

    /** Column name ChargeAmt */
    public static final String COLUMNNAME_ChargeAmt = "ChargeAmt";

	/** Set Charge amount.
	  * Charge Amount
	  */
	public void setChargeAmt (BigDecimal ChargeAmt);

	/** Get Charge amount.
	  * Charge Amount
	  */
	public BigDecimal getChargeAmt();

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

    /** Column name CRED */
    public static final String COLUMNNAME_CRED = "CRED";

	/** Set CRED	  */
	public void setCRED (String CRED);

	/** Get CRED	  */
	public String getCRED();

    /** Column name EndAmt */
    public static final String COLUMNNAME_EndAmt = "EndAmt";

	/** Set End Amount	  */
	public void setEndAmt (BigDecimal EndAmt);

	/** Get End Amount	  */
	public BigDecimal getEndAmt();

    /** Column name EREF */
    public static final String COLUMNNAME_EREF = "EREF";

	/** Set EREF	  */
	public void setEREF (String EREF);

	/** Get EREF	  */
	public String getEREF();

    /** Column name GVCode */
    public static final String COLUMNNAME_GVCode = "GVCode";

	/** Set GV Code.
	  * Geschäftsvorfall Code
	  */
	public void setGVCode (String GVCode);

	/** Get GV Code.
	  * Geschäftsvorfall Code
	  */
	public String getGVCode();

    /** Column name GVText */
    public static final String COLUMNNAME_GVText = "GVText";

	/** Set GV Text	  */
	public void setGVText (String GVText);

	/** Get GV Text	  */
	public String getGVText();

    /** Column name InstRef */
    public static final String COLUMNNAME_InstRef = "InstRef";

	/** Set InstRef.
	  * Institute Reference
	  */
	public void setInstRef (String InstRef);

	/** Get InstRef.
	  * Institute Reference
	  */
	public String getInstRef();

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

    /** Column name IsReversal */
    public static final String COLUMNNAME_IsReversal = "IsReversal";

	/** Set Reversal.
	  * This is a reversing transaction
	  */
	public void setIsReversal (boolean IsReversal);

	/** Get Reversal.
	  * This is a reversing transaction
	  */
	public boolean isReversal();

    /** Column name IsSepa */
    public static final String COLUMNNAME_IsSepa = "IsSepa";

	/** Set Is SEPA	  */
	public void setIsSepa (boolean IsSepa);

	/** Get Is SEPA	  */
	public boolean isSepa();

    /** Column name KREF */
    public static final String COLUMNNAME_KREF = "KREF";

	/** Set KREF	  */
	public void setKREF (String KREF);

	/** Get KREF	  */
	public String getKREF();

    /** Column name MREF */
    public static final String COLUMNNAME_MREF = "MREF";

	/** Set MREF	  */
	public void setMREF (String MREF);

	/** Get MREF	  */
	public String getMREF();

    /** Column name Primanota */
    public static final String COLUMNNAME_Primanota = "Primanota";

	/** Set Primanota	  */
	public void setPrimanota (String Primanota);

	/** Get Primanota	  */
	public String getPrimanota();

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name Reference */
    public static final String COLUMNNAME_Reference = "Reference";

	/** Set Reference.
	  * Reference for this record
	  */
	public void setReference (String Reference);

	/** Get Reference.
	  * Reference for this record
	  */
	public String getReference();

    /** Column name StartAmt */
    public static final String COLUMNNAME_StartAmt = "StartAmt";

	/** Set Start Amount	  */
	public void setStartAmt (BigDecimal StartAmt);

	/** Get Start Amount	  */
	public BigDecimal getStartAmt();

    /** Column name StatementDate */
    public static final String COLUMNNAME_StatementDate = "StatementDate";

	/** Set Statement date.
	  * Date of the statement
	  */
	public void setStatementDate (Timestamp StatementDate);

	/** Get Statement date.
	  * Date of the statement
	  */
	public Timestamp getStatementDate();

    /** Column name TotalAmt */
    public static final String COLUMNNAME_TotalAmt = "TotalAmt";

	/** Set Total Amount.
	  * Total Amount
	  */
	public void setTotalAmt (BigDecimal TotalAmt);

	/** Get Total Amount.
	  * Total Amount
	  */
	public BigDecimal getTotalAmt();

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

    /** Column name ValutaDate */
    public static final String COLUMNNAME_ValutaDate = "ValutaDate";

	/** Set Effective date.
	  * Date when money is available
	  */
	public void setValutaDate (Timestamp ValutaDate);

	/** Get Effective date.
	  * Date when money is available
	  */
	public Timestamp getValutaDate();

    /** Column name VWZ */
    public static final String COLUMNNAME_VWZ = "VWZ";

	/** Set VWZ	  */
	public void setVWZ (String VWZ);

	/** Get VWZ	  */
	public String getVWZ();
}
