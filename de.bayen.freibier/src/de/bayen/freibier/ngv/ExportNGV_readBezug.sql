SELECT
  9076 AS "Partner", /* NGV-Nummer Jakob Bayen KG */
  c_bpartner.value AS "LiefNr",
  c_bpartner.name AS "Name",
  c_location.address1 AS "Strasse",
  c_location.postal AS "PLZ",
  c_location.city AS "Ort",
  /**/
  m_product.sku AS "ArtNr",
  m_product.name AS "Artbez",
  bay_tradingunit.unitsperpack AS "Inhalt",
  bay_tradingunit.einheit AS "Packart",
  bay_tradingunit.inhalt AS "MEH",
  /**/
  round(bay_umsatzstatistik.stueck+bay_umsatzstatistik.stueckgratis) AS "Menge",
  bay_statistikperiode.startdate AS "VON",
  bay_statistikperiode.enddate AS "BIS",
  m_product.upc AS "EAN1",
  '' AS "EAN2",
  '' AS "EAN3",
  '' AS "EAN4",
  '' AS "EAN5"
  
FROM bay_statistikperiode
LEFT JOIN bay_umsatzstatistik USING(bay_statistikperiode_id)
LEFT JOIN c_bpartner USING(c_bpartner_id)
LEFT JOIN m_product USING(m_product_id)
LEFT JOIN bay_tradingunit USING(bay_tradingunit_id)
LEFT JOIN c_bpartner_location ON(
  c_bpartner.c_bpartner_id=c_bpartner_location.c_bpartner_id 
  AND c_bpartner_location.isbillto='Y'  /* Rechnungsadresse */
  )
LEFT JOIN c_location USING(c_location_id)
WHERE bay_statistikperiode.bay_statistikperiode_id=?
/* Nullen will die NGV nicht haben (entstehen, wenn Artikel gekauft und wieder zurückgegeben werden) */
AND (bay_umsatzstatistik.stueck+bay_umsatzstatistik.stueckgratis)!=0  
ORDER BY 
  c_bpartner.value,
  m_product.sku
;
