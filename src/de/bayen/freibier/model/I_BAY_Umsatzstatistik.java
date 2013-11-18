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

/** Generated Interface for BAY_Umsatzstatistik
 *  @author iDempiere (generated) 
 *  @version Release 1.0c
 */
@SuppressWarnings("all")
public interface I_BAY_Umsatzstatistik 
{

    /** TableName=BAY_Umsatzstatistik */
    public static final String Table_Name = "BAY_Umsatzstatistik";

    /** AD_Table_ID=1000005 */
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

    /** Column name BAY_Statistikperiode_ID */
    public static final String COLUMNNAME_BAY_Statistikperiode_ID = "BAY_Statistikperiode_ID";

	/** Set Statistikperiode	  */
	public void setBAY_Statistikperiode_ID (int BAY_Statistikperiode_ID);

	/** Get Statistikperiode	  */
	public int getBAY_Statistikperiode_ID();

	public I_BAY_Statistikperiode getBAY_Statistikperiode() throws RuntimeException;

    /** Column name BAY_Umsatzstatistik_ID */
    public static final String COLUMNNAME_BAY_Umsatzstatistik_ID = "BAY_Umsatzstatistik_ID";

	/** Set Umsatzstatistik	  */
	public void setBAY_Umsatzstatistik_ID (int BAY_Umsatzstatistik_ID);

	/** Get Umsatzstatistik	  */
	public int getBAY_Umsatzstatistik_ID();

    /** Column name BAY_Umsatzstatistik_UU */
    public static final String COLUMNNAME_BAY_Umsatzstatistik_UU = "BAY_Umsatzstatistik_UU";

	/** Set BAY_Umsatzstatistik_UU	  */
	public void setBAY_Umsatzstatistik_UU (String BAY_Umsatzstatistik_UU);

	/** Get BAY_Umsatzstatistik_UU	  */
	public String getBAY_Umsatzstatistik_UU();

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner .
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner .
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException;

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

    /** Column name Lieferungen */
    public static final String COLUMNNAME_Lieferungen = "Lieferungen";

	/** Set Lieferungen	  */
	public void setLieferungen (int Lieferungen);

	/** Get Lieferungen	  */
	public int getLieferungen();

    /** Column name M_Product_ID */
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";

	/** Set Product.
	  * Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID);

	/** Get Product.
	  * Product, Service, Item
	  */
	public int getM_Product_ID();

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException;

    /** Column name Stueck */
    public static final String COLUMNNAME_Stueck = "Stueck";

	/** Set Stück	  */
	public void setStueck (BigDecimal Stueck);

	/** Get Stück	  */
	public BigDecimal getStueck();

    /** Column name StueckGratis */
    public static final String COLUMNNAME_StueckGratis = "StueckGratis";

	/** Set Stück gratis	  */
	public void setStueckGratis (BigDecimal StueckGratis);

	/** Get Stück gratis	  */
	public BigDecimal getStueckGratis();

    /** Column name Umsatz */
    public static final String COLUMNNAME_Umsatz = "Umsatz";

	/** Set Umsatz	  */
	public void setUmsatz (BigDecimal Umsatz);

	/** Get Umsatz	  */
	public BigDecimal getUmsatz();

    /** Column name UmsatzEK */
    public static final String COLUMNNAME_UmsatzEK = "UmsatzEK";

	/** Set UmsatzEK	  */
	public void setUmsatzEK (BigDecimal UmsatzEK);

	/** Get UmsatzEK	  */
	public BigDecimal getUmsatzEK();

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
