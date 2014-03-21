SELECT  /* NGV Absatzstatistik */
  '001' AS "Versionsnummer",  /* Schnittstellenversion 001 vom 2.4.2013 */
  9076 AS "Partner", /* NGV-Nummer Jakob Bayen KG */
  '' AS "PartnerAuslief", /* Laut Herr Schütt: Ist für uns immer leer */
  /**/
  '' AS "LpNr",
  '' AS "Lp Name1",
  '' AS "Lp Name2",
  '' AS "Lp UST-ID",
  '' AS "Lp GLN",
  '' AS "Lp Straße",
  '' AS "Lp PLZ",
  '' AS "Lp Ort",
  '' AS "Lp Land",
  /**/
  C_BPartner.Value AS "WE KdNr",
  CASE 
    WHEN C_SalesRegion.Name IS NULL
    THEN C_BPartner.Name
    ELSE C_SalesRegion.Name
  END AS "WE Name1",
  CASE
    WHEN C_SalesRegion.Name IS NULL
    THEN C_BPartner.Name2
    ELSE C_BPartner.Name
  END AS "WE Name2",
  c_bpartner.taxid AS "WE UST-ID",
  c_bpartner.gln AS "WE GLN",  
  c_location.address1 AS "WE Straße",
  c_location.postal AS "WE PLZ",
  c_location.city AS "WE Ort",
  c_country.name AS "WE Land",
  /**/
  C_BPartner.Value AS "RGEmpf Nr",
  CASE 
    WHEN C_SalesRegion.Name IS NULL
    THEN C_BPartner.Name
    ELSE C_SalesRegion.Name
  END AS "RGEmpf Name1",
  CASE
    WHEN C_SalesRegion.Name IS NULL
    THEN C_BPartner.Name2
    ELSE C_BPartner.Name
  END AS "RGEmpf Name2",
  c_bpartner.taxid AS "RGEmpf UST-ID",
  c_bpartner.gln AS "RGEmpf GLN",  
  c_location.address1 AS "RGEmpf Straße",
  c_location.postal AS "RGEmpf PLZ",
  c_location.city AS "RGEmpf Ort",
  c_country.name AS "RGEmpf Land",
  /**/  
  m_product.value AS "ArtNr",
  m_product.name AS "Artbez1",
  '' AS "Artbez2",
  bay_tradingunit.unitsperpack AS "Inhalt",
  bay_tradingunit.einheit AS "Packart",
  bay_tradingunit.inhalt AS "MEH",
  /**/
  round(bay_umsatzstatistik.stueck) AS "Menge",
  round(bay_umsatzstatistik.stueckgratis) AS "Gratis",  /* kommt in eigene Zeile */
  bay_statistikperiode.startdate AS "von",
  bay_statistikperiode.enddate AS "bis",
  /* Gratis ist "" oder "x" */
  m_product.upc AS "EAN1",
  '' AS "EAN2",
  '' AS "EAN3",
  '' AS "EAN4",
  '' AS "EAN5",
  /**/
  'L' AS "LiefVerrAbs",
  /**/
  c_bpartner.NGVChannel AS "CH",
  c_bpartner.NGVBetriebstyp AS "BT",
  CASE WHEN c_bpartner.BetriebstypSaal = 'Y' THEN 'x' ELSE '' END AS "BT Saal",
  CASE WHEN c_bpartner.BetriebstypTerrasse = 'Y' THEN 'x' ELSE '' END AS "BT Terrasse",
  CASE WHEN c_bpartner.BetriebstypBiergarten = 'Y' THEN 'x' ELSE '' END AS "BT Biergarten",
  C_BP_Group.Value AS "GT"
    
FROM BAY_Statistikperiode
JOIN BAY_Umsatzstatistik ON (
  BAY_Umsatzstatistik.BAY_Statistikperiode_ID = BAY_Statistikperiode.BAY_Statistikperiode_ID
  AND BAY_Statistikperiode.BAY_Statistikperiode_ID=?
/*  AND BAY_Statistikperiode.Value='st1309' */
)
LEFT JOIN M_Product USING(M_Product_ID)
LEFT JOIN BAY_TradingUnit USING(BAY_TradingUnit_ID)
LEFT JOIN C_BPartner USING (C_BPartner_ID)
LEFT JOIN c_bpartner_location ON(
  c_bpartner.c_bpartner_id=c_bpartner_location.c_bpartner_id 
  AND c_bpartner_location.isbillto='Y'  /* Rechnungsadresse */
  AND c_bpartner_location.isActive='Y'
  )
LEFT JOIN c_location USING(c_location_id)
LEFT JOIN c_country USING(c_country_id)
LEFT JOIN C_SalesRegion USING (C_SalesRegion_ID)
LEFT JOIN C_BP_Group USING(C_BP_Group_ID)
ORDER BY 
  c_bpartner.value,
  m_product.value
;
