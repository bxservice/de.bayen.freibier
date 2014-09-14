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
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

/** Generated Model for BAY_HBCILog
 *  @author iDempiere (generated) 
 *  @version Release 2.0 - $Id$ */
public class X_BAY_HBCILog extends PO implements I_BAY_HBCILog, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140914L;

    /** Standard Constructor */
    public X_BAY_HBCILog (Properties ctx, int BAY_HBCILog_ID, String trxName)
    {
      super (ctx, BAY_HBCILog_ID, trxName);
      /** if (BAY_HBCILog_ID == 0)
        {
			setBAY_HBCILog_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_BAY_HBCILog (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 4 - System 
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
      StringBuffer sb = new StringBuffer ("X_BAY_HBCILog[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set HBCI Log.
		@param BAY_HBCILog_ID HBCI Log	  */
	public void setBAY_HBCILog_ID (int BAY_HBCILog_ID)
	{
		if (BAY_HBCILog_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BAY_HBCILog_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BAY_HBCILog_ID, Integer.valueOf(BAY_HBCILog_ID));
	}

	/** Get HBCI Log.
		@return HBCI Log	  */
	public int getBAY_HBCILog_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BAY_HBCILog_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set BAY_HBCILog_UU.
		@param BAY_HBCILog_UU BAY_HBCILog_UU	  */
	public void setBAY_HBCILog_UU (String BAY_HBCILog_UU)
	{
		set_Value (COLUMNNAME_BAY_HBCILog_UU, BAY_HBCILog_UU);
	}

	/** Get BAY_HBCILog_UU.
		@return BAY_HBCILog_UU	  */
	public String getBAY_HBCILog_UU () 
	{
		return (String)get_Value(COLUMNNAME_BAY_HBCILog_UU);
	}

	/** Set Binary Data.
		@param BinaryData 
		Binary Data
	  */
	public void setBinaryData (byte[] BinaryData)
	{
		set_Value (COLUMNNAME_BinaryData, BinaryData);
	}

	/** Get Binary Data.
		@return Binary Data
	  */
	public byte[] getBinaryData () 
	{
		return (byte[])get_Value(COLUMNNAME_BinaryData);
	}

	/** Set Counter.
		@param Counter 
		Count Value
	  */
	public void setCounter (int Counter)
	{
		set_ValueNoCheck (COLUMNNAME_Counter, Integer.valueOf(Counter));
	}

	/** Get Counter.
		@return Count Value
	  */
	public int getCounter () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Counter);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_PaymentBatch getC_PaymentBatch() throws RuntimeException
    {
		return (org.compiere.model.I_C_PaymentBatch)MTable.get(getCtx(), org.compiere.model.I_C_PaymentBatch.Table_Name)
			.getPO(getC_PaymentBatch_ID(), get_TrxName());	}

	/** Set Payment Batch.
		@param C_PaymentBatch_ID 
		Payment batch for EFT
	  */
	public void setC_PaymentBatch_ID (int C_PaymentBatch_ID)
	{
		if (C_PaymentBatch_ID < 1) 
			set_Value (COLUMNNAME_C_PaymentBatch_ID, null);
		else 
			set_Value (COLUMNNAME_C_PaymentBatch_ID, Integer.valueOf(C_PaymentBatch_ID));
	}

	/** Get Payment Batch.
		@return Payment batch for EFT
	  */
	public int getC_PaymentBatch_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_PaymentBatch_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_PaySelection getC_PaySelection() throws RuntimeException
    {
		return (org.compiere.model.I_C_PaySelection)MTable.get(getCtx(), org.compiere.model.I_C_PaySelection.Table_Name)
			.getPO(getC_PaySelection_ID(), get_TrxName());	}

	/** Set Payment Selection.
		@param C_PaySelection_ID 
		Payment Selection
	  */
	public void setC_PaySelection_ID (int C_PaySelection_ID)
	{
		if (C_PaySelection_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_PaySelection_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_PaySelection_ID, Integer.valueOf(C_PaySelection_ID));
	}

	/** Get Payment Selection.
		@return Payment Selection
	  */
	public int getC_PaySelection_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_PaySelection_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Error.
		@param IsError 
		An Error occurred in the execution
	  */
	public void setIsError (boolean IsError)
	{
		set_Value (COLUMNNAME_IsError, Boolean.valueOf(IsError));
	}

	/** Get Error.
		@return An Error occurred in the execution
	  */
	public boolean isError () 
	{
		Object oo = get_Value(COLUMNNAME_IsError);
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getName());
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

	/** Set Protocol.
		@param Protocol 
		Protocol
	  */
	public void setProtocol (String Protocol)
	{
		set_Value (COLUMNNAME_Protocol, Protocol);
	}

	/** Get Protocol.
		@return Protocol
	  */
	public String getProtocol () 
	{
		return (String)get_Value(COLUMNNAME_Protocol);
	}

	/** Set Status.
		@param Status 
		Status of the currently running check
	  */
	public void setStatus (String Status)
	{
		set_Value (COLUMNNAME_Status, Status);
	}

	/** Get Status.
		@return Status of the currently running check
	  */
	public String getStatus () 
	{
		return (String)get_Value(COLUMNNAME_Status);
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
}