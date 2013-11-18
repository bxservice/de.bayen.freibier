/*
 * Stellt die Umsatzstatistik-Datensaetze fest, die es nicht gibt. Moechte ich also z.B., das
 * bestimmte Kunden einen bestimmten Artikel mindestens einmal bezogen haben, kann ich hiermit
 * herausfinden, welche das nicht getan haben.
 * 
 * Das Ergebnis ist eine Liste mit Kunden und Artikeln, ergibt also keine fertigen Datensätze
 * (wie auch?).
 * 
 * Dieser Code hier wurde nach *_sucheZeilen.sql erzeugt und arbeitet ganz anders. Das Problem
 * der nicht vorhandenen Datensätze war mit der anderen Methode nicht zu lösen und außerdem
 * ging es nicht performant, ohne temporäre Tabellen zu erzeugen (mit WITH). Ich hoffe, die
 * beiden ergänzen sich dennoch...
 */
WITH Parameter AS (
  /* Parameter in einer temporaeren Tabelle vorbereiten, damit diese garantiert nur einmal
     und in einer definierten Reihenfolge angegeben werden muessen.
  */
  SELECT
    ? AS BAY_Statistikperiode_ID,
    ? AS BAY_StatAdjust_ID
  /*
    1000146 AS BAY_Statistikperiode_ID,
    1000000 AS BAY_StatAdjust_ID
  */
), 

BP AS (
SELECT C_BPartner_ID
FROM C_BPartner, Parameter
JOIN BAY_StatAdjust ON(BAY_StatAdjust.BAY_StatAdjust_ID = Parameter.BAY_StatAdjust_ID)
WHERE
  C_BPartner.isActive='Y'
  AND(
    /* Der Kunde, den ich extra genannt habe */
    BAY_StatAdjust.Customer_ID = C_BPartner.C_BPartner_ID
    /* sowie Alle Kunden in der genannten Kundengruppe */
    OR BAY_StatAdjust.C_BP_Group_ID = C_BPartner.C_BP_Group_ID 
    /* sowie ueberhaupt alle Kunden, falls ich gar nichts genannt habe */
    OR (BAY_StatAdjust.Customer_ID IS NULL AND BAY_StatAdjust.C_BP_Group_ID IS NULL)
  )
),

PR AS(
SELECT
  M_Product.M_Product_ID
FROM Parameter 
CROSS JOIN M_Product
LEFT JOIN C_BPartner_Product ON (C_BPartner_Product.M_Product_ID = M_Product.M_Product_ID) /* Produkt und Hersteller verbinden */
JOIN BAY_StatAdjust ON(BAY_StatAdjust.BAY_StatAdjust_ID = Parameter.BAY_StatAdjust_ID)
WHERE
  M_Product.isActive='Y'
  AND (
    /* Den Artikel, den ich genannt habe */
    BAY_StatAdjust.M_Product_ID = M_Product.M_Product_ID
    /* sowie alle Artikel in der genannten Artikelkategorie */
    OR BAY_StatAdjust.M_Product_Category_ID = M_Product.M_Product_Category_ID
    /* oder ueberhaupt alle Artikel, falls ich gar nichts genannt habe */
    OR (BAY_StatAdjust.M_Product_ID IS NULL AND BAY_StatAdjust.M_Product_Category_ID IS NULL)
  ) AND (
    /* nur die Artikel eines Herstellers, falls einer angegeben ist */
    BAY_StatAdjust.Vendor_ID = C_BPartner_Product.C_BPartner_ID
    /* sonst alle vorher ausgewählten, wenn kein Hersteller angegeben ist */
    OR BAY_StatAdjust.Vendor_ID IS NULL
  )
)

SELECT
  BP.C_BPartner_ID,
  PR.M_Product_ID
FROM Parameter
CROSS JOIN BP
CROSS JOIN PR
LEFT JOIN BAY_Umsatzstatistik
  ON (
    BAY_Umsatzstatistik.BAY_Statistikperiode_ID = Parameter.BAY_Statistikperiode_ID
    AND BAY_Umsatzstatistik.C_BPartner_ID = BP.C_BPartner_ID
    AND BAY_Umsatzstatistik.M_Product_ID = PR.M_Product_ID
  )
WHERE BAY_Umsatzstatistik.BAY_Umsatzstatistik_ID IS NULL /* Zeilen, die in der Umsatzstatistik nicht da sind */
;
