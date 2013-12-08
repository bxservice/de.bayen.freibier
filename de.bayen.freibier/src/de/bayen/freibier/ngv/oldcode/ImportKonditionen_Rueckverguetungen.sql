SELECT
  /*substring("Key",1,1),                      immer K */
  LPAD(trim(substring("Key",2,5)),5,'0') 
    AS Empfaenger,                          /* Empfänger */
  bpe.C_BPartner_ID AS Empfaenger_ID,
  /*substring("Key",7,1),                      immer K */
  LPAD(trim(substring("Key",8,5)),5,'0') 
    AS Bezieher,                            /* Bezieher */
  bpb.C_BPartner_ID AS Bezieher_ID,
  substring("Key",13,5) AS Artikelauswahl,  /* Artikelauswahl */
  M_Product.M_Product_ID AS Artikel_ID,
  grp_ueb.nachher AS Artikelgruppe,
  BAY_Gruppe.BAY_Gruppe_ID AS Artikelgruppe_ID,
  /*substring("Key",18,1),                     immer ' ' */
  substring("Key",19,1) AS UOM,  /* (H)ektoliter, (S)tueck, (U)msatz, (F)üllung */
  /* Unknown1 enthält den Wert in Euro, Zero den Wert in Prozenten */
  CASE WHEN "Unknown1"!=0
       THEN "Unknown1"
       ELSE "Zero"
  END AS Betrag,
  "Unknown2" AS MwStKz,   /* 0: ohne, 1 plus MwSt, 2 inklusive, ist aber 0,1, oder leer(!?) */
  "Unknown3" AS AbMenge
  /* "Spaces" ist wirklich immer leer */
FROM
  "Provisio"
LEFT JOIN C_BPartner AS bpe 
  ON(LPAD(bpe.Value,5,'0') = LPAD(trim(substring("Key",2,5)),5,'0'))
LEFT JOIN C_BPartner AS bpb 
  ON(LPAD(bpb.Value,5,'0') = LPAD(trim(substring("Key",8,5)),5,'0'))
LEFT JOIN M_Product
  ON(M_Product.Value = substring("Key",13,5))
LEFT JOIN gruppen_uebersetzung AS grp_ueb
  ON(grp_ueb.vorher = TRIM(substring("Key",13,5)))
LEFT JOIN BAY_Gruppe ON(grp_ueb.nachher = BAY_Gruppe.Value)
ORDER BY substring("Key",2,5)
