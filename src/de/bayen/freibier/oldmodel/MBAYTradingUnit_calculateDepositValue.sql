SELECT
  COALESCE((
    SELECT bay_preis.pricelist 
    FROM m_productprice AS bay_preis 
    LEFT JOIN m_pricelist_version AS bay_pv 
      USING(m_pricelist_version_id) 
    WHERE 
      bay_pv.name='Pfandwerte' 
      AND bay_preis.m_product_id = ?
  ),0)
  + ? *
  COALESCE((
    SELECT bay_preis.pricelist 
    FROM m_productprice AS bay_preis 
    LEFT JOIN m_pricelist_version AS bay_pv 
      USING(m_pricelist_version_id) 
    WHERE 
      bay_pv.name='Pfandwerte' 
      AND bay_preis.m_product_id = ?
  ),0)
;
