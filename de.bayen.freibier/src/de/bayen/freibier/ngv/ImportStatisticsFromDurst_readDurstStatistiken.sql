
SELECT
  C_BPartner.C_BPartner_ID,
  M_Product.M_Product_ID,
  round("StueckGeliefert"::numeric,2) AS stueck,
  round("UmsatzDM"::numeric,2) AS umsatz,
  round("EKSummeDM"::numeric,2) AS ekwert,
  "AnzLieferungen" AS lieferungen,
  round("StueckGratis"::numeric,2) AS stueckgratis,
  "Wgst"."NumArtikel", /* nur für eine qualifizierte Fehlermeldung, falls die Nummer nicht existiert */
  "Wgst"."NumKunde"
FROM durst."Wgst" 
LEFT JOIN M_Product 
  ON(M_Product.AD_Client_ID=1000000 
    AND M_Product.value="Wgst"."NumArtikel"
  )
LEFT JOIN C_BPartner 
  ON(C_BPartner.AD_Client_ID=1000000 
    AND ( 
      C_BPartner.value = "Wgst"."NumKunde"
      OR (
        length(TRIM("Wgst"."NumKunde")) <= 3 
        AND C_BPartner.value = 'L'||LPAD(TRIM("Wgst"."NumKunde"),3,'0')
      )
    )
  )
WHERE joker=? AND CAST("NumKunde" AS numeric) > 0
;
