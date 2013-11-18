SELECT
  9076 AS "Partner", /* NGV-Nummer Jakob Bayen KG */
  C_BPartner.Value AS "KdNr",
  CASE 
    WHEN BAY_Kundenobjekt.Name IS NULL
    THEN C_BPartner.Name
    ELSE BAY_Kundenobjekt.Name
  END AS "Name1",
  CASE
    WHEN BAY_Kundenobjekt.Name IS NULL
    THEN C_BPartner.Name2
    ELSE C_BPartner.Name
  END AS "Name2",
  c_location.address1 AS "Strasse",
  c_location.postal AS "PLZ",
  c_location.city AS "Ort",
  /**/
  m_product.sku AS "ArtNr",
  m_product.name AS "Artbez",
  '' AS "Artbez2",
  bay_tradingunit.unitsperpack AS "Inhalt",
  bay_tradingunit.einheit AS "Packart",
  bay_tradingunit.inhalt AS "MEH",
  /**/
  round(bay_umsatzstatistik.stueck) AS "Menge",
  round(bay_umsatzstatistik.stueckgratis) AS "Gratis",  /* kommt in eigene Zeile */
  bay_statistikperiode.startdate AS "VON",
  bay_statistikperiode.enddate AS "BIS",
  /* Gratis ist "" oder "x" */
  m_product.upc AS "EAN1",
  '' AS "EAN2",
  '' AS "EAN3",
  '' AS "EAN4",
  '' AS "EAN5"
  
FROM BAY_Statistikperiode
JOIN BAY_Umsatzstatistik ON (
  BAY_Umsatzstatistik.BAY_Statistikperiode_ID = BAY_Statistikperiode.BAY_Statistikperiode_ID
  AND BAY_Statistikperiode.BAY_Statistikperiode_ID=?
)
LEFT JOIN M_Product USING(M_Product_ID)
LEFT JOIN BAY_TradingUnit USING(BAY_TradingUnit_ID)
LEFT JOIN C_BPartner USING (C_BPartner_ID)
LEFT JOIN c_bpartner_location ON(
  c_bpartner.c_bpartner_id=c_bpartner_location.c_bpartner_id 
  AND c_bpartner_location.isbillto='Y'  /* Rechnungsadresse */
  )
LEFT JOIN c_location USING(c_location_id)
LEFT JOIN BAY_Kundenobjekt USING (BAY_Kundenobjekt_ID)
ORDER BY 
  c_bpartner.value,
  m_product.sku
;
