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

/** Generated Interface for BAY_TradingUnit
 *  @author iDempiere (generated) 
 *  @version Release 2.0
 */
@SuppressWarnings("all")
public interface I_BAY_TradingUnit 
{

    /** TableName=BAY_TradingUnit */
    public static final String Table_Name = "BAY_TradingUnit";

    /** AD_Table_ID=1000006 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

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

    /** Column name BAY_TradingUnit_ID */
    public static final String COLUMNNAME_BAY_TradingUnit_ID = "BAY_TradingUnit_ID";

	/** Set Gebinde	  */
	public void setBAY_TradingUnit_ID (int BAY_TradingUnit_ID);

	/** Get Gebinde	  */
	public int getBAY_TradingUnit_ID();

    /** Column name BAY_TradingUnit_UU */
    public static final String COLUMNNAME_BAY_TradingUnit_UU = "BAY_TradingUnit_UU";

	/** Set BAY_TradingUnit_UU	  */
	public void setBAY_TradingUnit_UU (String BAY_TradingUnit_UU);

	/** Get BAY_TradingUnit_UU	  */
	public String getBAY_TradingUnit_UU();

    /** Column name Besonderheit */
    public static final String COLUMNNAME_Besonderheit = "Besonderheit";

	/** Set Besonderheit	  */
	public void setBesonderheit (String Besonderheit);

	/** Get Besonderheit	  */
	public String getBesonderheit();

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

    /** Column name C_UOM_ID */
    public static final String COLUMNNAME_C_UOM_ID = "C_UOM_ID";

	/** Set UOM.
	  * Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID);

	/** Get UOM.
	  * Unit of Measure
	  */
	public int getC_UOM_ID();

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException;

    /** Column name DepositPackage_ID */
    public static final String COLUMNNAME_DepositPackage_ID = "DepositPackage_ID";

	/** Set Deposit Package Product	  */
	public void setDepositPackage_ID (int DepositPackage_ID);

	/** Get Deposit Package Product	  */
	public int getDepositPackage_ID();

	public org.compiere.model.I_M_Product getDepositPackage() throws RuntimeException;

    /** Column name DepositUnit_ID */
    public static final String COLUMNNAME_DepositUnit_ID = "DepositUnit_ID";

	/** Set Deposit Unit	  */
	public void setDepositUnit_ID (int DepositUnit_ID);

	/** Get Deposit Unit	  */
	public int getDepositUnit_ID();

	public org.compiere.model.I_M_Product getDepositUnit() throws RuntimeException;

    /** Column name DepositValue */
    public static final String COLUMNNAME_DepositValue = "DepositValue";

	/** Set Deposit Value	  */
	public void setDepositValue (BigDecimal DepositValue);

	/** Get Deposit Value	  */
	public BigDecimal getDepositValue();

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

    /** Column name Einheit */
    public static final String COLUMNNAME_Einheit = "Einheit";

	/** Set Einheit	  */
	public void setEinheit (String Einheit);

	/** Get Einheit	  */
	public String getEinheit();

    /** Column name Help */
    public static final String COLUMNNAME_Help = "Help";

	/** Set Comment/Help.
	  * Comment or Hint
	  */
	public void setHelp (String Help);

	/** Get Comment/Help.
	  * Comment or Hint
	  */
	public String getHelp();

    /** Column name Inhalt */
    public static final String COLUMNNAME_Inhalt = "Inhalt";

	/** Set Inhalt	  */
	public void setInhalt (String Inhalt);

	/** Get Inhalt	  */
	public String getInhalt();

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

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name UnitsPerPack */
    public static final String COLUMNNAME_UnitsPerPack = "UnitsPerPack";

	/** Set UnitsPerPack.
	  * The Units Per Pack indicates the no of units of a product packed together.
	  */
	public void setUnitsPerPack (int UnitsPerPack);

	/** Get UnitsPerPack.
	  * The Units Per Pack indicates the no of units of a product packed together.
	  */
	public int getUnitsPerPack();

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

    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();

    /** Column name Volume */
    public static final String COLUMNNAME_Volume = "Volume";

	/** Set Volume.
	  * Volume of a product
	  */
	public void setVolume (BigDecimal Volume);

	/** Get Volume.
	  * Volume of a product
	  */
	public BigDecimal getVolume();

    /** Column name Weight */
    public static final String COLUMNNAME_Weight = "Weight";

	/** Set Weight.
	  * Weight of a product
	  */
	public void setWeight (BigDecimal Weight);

	/** Get Weight.
	  * Weight of a product
	  */
	public BigDecimal getWeight();
}
