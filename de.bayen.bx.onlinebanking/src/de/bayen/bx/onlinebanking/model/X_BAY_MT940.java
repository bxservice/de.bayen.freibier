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
package de.bayen.bx.onlinebanking.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

/** Generated Model for BAY_MT940
 *  @author iDempiere (generated) 
 *  @version Release 2.0 - $Id$ */
public class X_BAY_MT940 extends PO implements I_BAY_MT940, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140911L;

    /** Standard Constructor */
    public X_BAY_MT940 (Properties ctx, int BAY_MT940_ID, String trxName)
    {
      super (ctx, BAY_MT940_ID, trxName);
      /** if (BAY_MT940_ID == 0)
        {
			setAmount (Env.ZERO);
			setBAY_MT940_ID (0);
			setC_BankAccount_ID (0);
			setEndAmt (Env.ZERO);
			setGVCode (null);
			setIsReversal (false);
// 'N'
			setIsSepa (false);
// 'N'
			setStartAmt (Env.ZERO);
			setStatementDate (new Timestamp( System.currentTimeMillis() ));
			setTotalAmt (Env.ZERO);
			setValutaDate (new Timestamp( System.currentTimeMillis() ));
        } */
    }

    /** Load Constructor */
    public X_BAY_MT940 (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 1 - Org 
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
      StringBuffer sb = new StringBuffer ("X_BAY_MT940[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Additional.
		@param Additional Additional	  */
	public void setAdditional (String Additional)
	{
		set_ValueNoCheck (COLUMNNAME_Additional, Additional);
	}

	/** Get Additional.
		@return Additional	  */
	public String getAdditional () 
	{
		return (String)get_Value(COLUMNNAME_Additional);
	}

	/** Set AddKey.
		@param AddKey AddKey	  */
	public void setAddKey (String AddKey)
	{
		set_ValueNoCheck (COLUMNNAME_AddKey, AddKey);
	}

	/** Get AddKey.
		@return AddKey	  */
	public String getAddKey () 
	{
		return (String)get_Value(COLUMNNAME_AddKey);
	}

	/** Set Amount.
		@param Amount 
		Amount in a defined currency
	  */
	public void setAmount (BigDecimal Amount)
	{
		set_ValueNoCheck (COLUMNNAME_Amount, Amount);
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

	/** Set Source Amount.
		@param AmtSource 
		Amount Balance in Source Currency
	  */
	public void setAmtSource (BigDecimal AmtSource)
	{
		set_ValueNoCheck (COLUMNNAME_AmtSource, AmtSource);
	}

	/** Get Source Amount.
		@return Amount Balance in Source Currency
	  */
	public BigDecimal getAmtSource () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtSource);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set MT940.
		@param BAY_MT940_ID MT940	  */
	public void setBAY_MT940_ID (int BAY_MT940_ID)
	{
		if (BAY_MT940_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BAY_MT940_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BAY_MT940_ID, Integer.valueOf(BAY_MT940_ID));
	}

	/** Get MT940.
		@return MT940	  */
	public int getBAY_MT940_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BAY_MT940_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set BAY_MT940_UU.
		@param BAY_MT940_UU BAY_MT940_UU	  */
	public void setBAY_MT940_UU (String BAY_MT940_UU)
	{
		set_ValueNoCheck (COLUMNNAME_BAY_MT940_UU, BAY_MT940_UU);
	}

	/** Get BAY_MT940_UU.
		@return BAY_MT940_UU	  */
	public String getBAY_MT940_UU () 
	{
		return (String)get_Value(COLUMNNAME_BAY_MT940_UU);
	}

	/** Set Businesspartner Account No.
		@param BPAccountNo Businesspartner Account No	  */
	public void setBPAccountNo (String BPAccountNo)
	{
		set_ValueNoCheck (COLUMNNAME_BPAccountNo, BPAccountNo);
	}

	/** Get Businesspartner Account No.
		@return Businesspartner Account No	  */
	public String getBPAccountNo () 
	{
		return (String)get_Value(COLUMNNAME_BPAccountNo);
	}

	/** Set BP Name.
		@param BPName BP Name	  */
	public void setBPName (String BPName)
	{
		set_ValueNoCheck (COLUMNNAME_BPName, BPName);
	}

	/** Get BP Name.
		@return BP Name	  */
	public String getBPName () 
	{
		return (String)get_Value(COLUMNNAME_BPName);
	}

	/** Set Businesspartner Routiing No.
		@param BPRoutingNo Businesspartner Routiing No	  */
	public void setBPRoutingNo (String BPRoutingNo)
	{
		set_ValueNoCheck (COLUMNNAME_BPRoutingNo, BPRoutingNo);
	}

	/** Get Businesspartner Routiing No.
		@return Businesspartner Routiing No	  */
	public String getBPRoutingNo () 
	{
		return (String)get_Value(COLUMNNAME_BPRoutingNo);
	}

	public org.compiere.model.I_C_BankAccount getC_BankAccount() throws RuntimeException
    {
		return (org.compiere.model.I_C_BankAccount)MTable.get(getCtx(), org.compiere.model.I_C_BankAccount.Table_Name)
			.getPO(getC_BankAccount_ID(), get_TrxName());	}

	/** Set Bank Account.
		@param C_BankAccount_ID 
		Account at the Bank
	  */
	public void setC_BankAccount_ID (int C_BankAccount_ID)
	{
		if (C_BankAccount_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_BankAccount_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_BankAccount_ID, Integer.valueOf(C_BankAccount_ID));
	}

	/** Get Bank Account.
		@return Account at the Bank
	  */
	public int getC_BankAccount_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BankAccount_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Charge amount.
		@param ChargeAmt 
		Charge Amount
	  */
	public void setChargeAmt (BigDecimal ChargeAmt)
	{
		set_ValueNoCheck (COLUMNNAME_ChargeAmt, ChargeAmt);
	}

	/** Get Charge amount.
		@return Charge Amount
	  */
	public BigDecimal getChargeAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ChargeAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set CRED.
		@param CRED CRED	  */
	public void setCRED (String CRED)
	{
		set_ValueNoCheck (COLUMNNAME_CRED, CRED);
	}

	/** Get CRED.
		@return CRED	  */
	public String getCRED () 
	{
		return (String)get_Value(COLUMNNAME_CRED);
	}

	/** Set End Amount.
		@param EndAmt End Amount	  */
	public void setEndAmt (BigDecimal EndAmt)
	{
		set_ValueNoCheck (COLUMNNAME_EndAmt, EndAmt);
	}

	/** Get End Amount.
		@return End Amount	  */
	public BigDecimal getEndAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_EndAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set EREF.
		@param EREF EREF	  */
	public void setEREF (String EREF)
	{
		set_ValueNoCheck (COLUMNNAME_EREF, EREF);
	}

	/** Get EREF.
		@return EREF	  */
	public String getEREF () 
	{
		return (String)get_Value(COLUMNNAME_EREF);
	}

	/** Set GV Code.
		@param GVCode 
		Geschäftsvorfall Code
	  */
	public void setGVCode (String GVCode)
	{
		set_ValueNoCheck (COLUMNNAME_GVCode, GVCode);
	}

	/** Get GV Code.
		@return Geschäftsvorfall Code
	  */
	public String getGVCode () 
	{
		return (String)get_Value(COLUMNNAME_GVCode);
	}

	/** Set GV Text.
		@param GVText GV Text	  */
	public void setGVText (String GVText)
	{
		set_ValueNoCheck (COLUMNNAME_GVText, GVText);
	}

	/** Get GV Text.
		@return GV Text	  */
	public String getGVText () 
	{
		return (String)get_Value(COLUMNNAME_GVText);
	}

	/** Set InstRef.
		@param InstRef 
		Institute Reference
	  */
	public void setInstRef (String InstRef)
	{
		set_ValueNoCheck (COLUMNNAME_InstRef, InstRef);
	}

	/** Get InstRef.
		@return Institute Reference
	  */
	public String getInstRef () 
	{
		return (String)get_Value(COLUMNNAME_InstRef);
	}

	/** Set Reversal.
		@param IsReversal 
		This is a reversing transaction
	  */
	public void setIsReversal (boolean IsReversal)
	{
		set_ValueNoCheck (COLUMNNAME_IsReversal, Boolean.valueOf(IsReversal));
	}

	/** Get Reversal.
		@return This is a reversing transaction
	  */
	public boolean isReversal () 
	{
		Object oo = get_Value(COLUMNNAME_IsReversal);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is SEPA.
		@param IsSepa Is SEPA	  */
	public void setIsSepa (boolean IsSepa)
	{
		set_ValueNoCheck (COLUMNNAME_IsSepa, Boolean.valueOf(IsSepa));
	}

	/** Get Is SEPA.
		@return Is SEPA	  */
	public boolean isSepa () 
	{
		Object oo = get_Value(COLUMNNAME_IsSepa);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set KREF.
		@param KREF KREF	  */
	public void setKREF (String KREF)
	{
		set_ValueNoCheck (COLUMNNAME_KREF, KREF);
	}

	/** Get KREF.
		@return KREF	  */
	public String getKREF () 
	{
		return (String)get_Value(COLUMNNAME_KREF);
	}

	/** Set MREF.
		@param MREF MREF	  */
	public void setMREF (String MREF)
	{
		set_ValueNoCheck (COLUMNNAME_MREF, MREF);
	}

	/** Get MREF.
		@return MREF	  */
	public String getMREF () 
	{
		return (String)get_Value(COLUMNNAME_MREF);
	}

	/** Set Primanota.
		@param Primanota Primanota	  */
	public void setPrimanota (String Primanota)
	{
		set_ValueNoCheck (COLUMNNAME_Primanota, Primanota);
	}

	/** Get Primanota.
		@return Primanota	  */
	public String getPrimanota () 
	{
		return (String)get_Value(COLUMNNAME_Primanota);
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Reference.
		@param Reference 
		Reference for this record
	  */
	public void setReference (String Reference)
	{
		set_ValueNoCheck (COLUMNNAME_Reference, Reference);
	}

	/** Get Reference.
		@return Reference for this record
	  */
	public String getReference () 
	{
		return (String)get_Value(COLUMNNAME_Reference);
	}

	/** Set Start Amount.
		@param StartAmt Start Amount	  */
	public void setStartAmt (BigDecimal StartAmt)
	{
		set_ValueNoCheck (COLUMNNAME_StartAmt, StartAmt);
	}

	/** Get Start Amount.
		@return Start Amount	  */
	public BigDecimal getStartAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_StartAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Statement date.
		@param StatementDate 
		Date of the statement
	  */
	public void setStatementDate (Timestamp StatementDate)
	{
		set_ValueNoCheck (COLUMNNAME_StatementDate, StatementDate);
	}

	/** Get Statement date.
		@return Date of the statement
	  */
	public Timestamp getStatementDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_StatementDate);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getStatementDate()));
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

	/** Set Effective date.
		@param ValutaDate 
		Date when money is available
	  */
	public void setValutaDate (Timestamp ValutaDate)
	{
		set_ValueNoCheck (COLUMNNAME_ValutaDate, ValutaDate);
	}

	/** Get Effective date.
		@return Date when money is available
	  */
	public Timestamp getValutaDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_ValutaDate);
	}

	/** Set VWZ.
		@param VWZ VWZ	  */
	public void setVWZ (String VWZ)
	{
		set_ValueNoCheck (COLUMNNAME_VWZ, VWZ);
	}

	/** Get VWZ.
		@return VWZ	  */
	public String getVWZ () 
	{
		return (String)get_Value(COLUMNNAME_VWZ);
	}
}