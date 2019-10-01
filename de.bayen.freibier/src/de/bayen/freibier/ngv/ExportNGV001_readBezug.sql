SELECT /* NGV Bezugsstatistik */
  '001' AS "Versionsnummer",  /* Schnittstellenversion 001 vom 2.4.2013 */
  9076 AS "Partner", /* NGV-Nummer Jakob Bayen KG */
  '' AS "PartnerBez",  /* laut Herr Schütt leerlassen */
  /**/
  c_bpartner.value AS "RgStNr",
  c_bpartner.name AS "RgSt Name1",
  c_bpartner.name2 AS "RgSt Name2",
  c_bpartner.taxid AS "RgSt UST-ID", /* lt. Schütt ggf. weglassen */
  c_bpartner.gln AS "RgSt GLN", /* lt. Schütt ggf. weglassen */
  c_location.address1 AS "RgSt Straße",
  c_location.postal AS "RgSt PLZ",
  c_location.city AS "RgSt Ort",
  c_country.name AS "RgSt Land",
  /**/
  m_product.value AS "ArtNr",
  m_product.name AS "Artbez1",
  '' AS "Artbez2",
  bay_tradingunit.unitsperpack AS "Inhalt",
  bay_tradingunit.einheit AS "Packart",
  bay_tradingunit.inhalt AS "MEH",
  /**/
  c_bpartner.value AS "Lft Nr",
  c_bpartner.name AS "Lft Name1",
  c_bpartner.name2 AS "Lft Name2",
  c_bpartner.taxid AS "Lft UST-ID", /* lt. Schütt ggf. weglassen */
  c_bpartner.gln AS "Lft GLN", /* lt. Schütt ggf. weglassen */
  c_location.address1 AS "Lft Straße",
  c_location.postal AS "Lft PLZ",
  c_location.city AS "Lft Ort",
  c_country.name AS "Lft Land",
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
  bay_statistikperiode.startdate AS "von",
  bay_statistikperiode.enddate AS "bis",
  m_product.upc AS "EAN1",
  '' AS "EAN2",
  '' AS "EAN3",
  '' AS "EAN4",
  '' AS "EAN5",
  /**/
  bay_umsatzstatistik.umsatz AS "EkUms"
  /* Gratis ist "" oder "x" */
  
FROM bay_statistikperiode
LEFT JOIN bay_umsatzstatistik ON (bay_umsatzstatistik.bay_statistikperiode_id = bay_statistikperiode.bay_statistikperiode_id AND bay_umsatzstatistik.isactive='Y')
LEFT JOIN C_BPartner ON (C_BPartner.C_BPartner_ID = BAY_Umsatzstatistik.C_BPartner_ID)
LEFT JOIN M_Product ON (M_Product.M_Product_ID = BAY_Umsatzstatistik.m_product_id)
LEFT JOIN BAY_TradingUnit ON (M_Product.BAY_TradingUnit_ID = BAY_TradingUnit.bay_tradingunit_id)
LEFT JOIN c_bpartner_location ON(
  c_bpartner.c_bpartner_id=c_bpartner_location.c_bpartner_id 
  AND c_bpartner_location.isbillto='Y'  /* Rechnungsadresse */
  AND c_bpartner_location.isactive='Y'
  )
LEFT JOIN c_location ON (c_location.c_location_id = c_bpartner_location.c_location_id)
LEFT JOIN c_country ON (c_country.c_country_id = c_location.c_country_id)
WHERE bay_statistikperiode.bay_statistikperiode_id=?
/* WHERE bay_statistikperiode.value='lm1309' */
/* Nullen will die NGV nicht haben (entstehen, wenn Artikel gekauft und wieder zurückgegeben werden) */
AND (bay_umsatzstatistik.stueck+bay_umsatzstatistik.stueckgratis)!=0  
ORDER BY 
  c_bpartner.value,
  m_product.value
;
