/*
 * Auswahl aller Datensaetze z.B. für die Anweisung "umbuchenKunde".
 * 
 * Es werden alle Datensaetze "BAY_Umsatzstatistik" gesucht, die
 * der Auswahl von Kunden und Artikeln entsprechen.
*/

WITH Parameter AS (
  /* Parameter in einer temporären Tabelle vorbereiten, damit diese garantiert nur einmal
     und in einer definierten Reihenfolge angegeben werden müssen.
  */
  SELECT
    ? AS BAY_Statistikperiode_ID,
    ? AS BAY_StatAdjust_ID
  /*
    1000146 AS BAY_Statistikperiode_ID,
    1000000 AS BAY_StatAdjust_ID
  */
)
SELECT
  BAY_Umsatzstatistik.*
FROM Parameter
CROSS JOIN BAY_Umsatzstatistik
/* Alle benötigten Informationen kombinieren, die an der Umsatzstatistik dranhängen */
LEFT JOIN BAY_Statistikperiode ON(BAY_Statistikperiode.BAY_Statistikperiode_ID = BAY_Umsatzstatistik.BAY_Statistikperiode_ID)
LEFT JOIN C_BPartner ON(C_BPartner.C_BPartner_ID = BAY_Umsatzstatistik.C_BPartner_ID)
LEFT JOIN M_Product ON(M_Product.M_Product_ID = BAY_Umsatzstatistik.M_Product_ID)
LEFT JOIN C_BPartner_Product ON(C_BPartner_Product.M_Product_ID = M_Product.M_Product_ID)
/* Achtung! Jeder Artikel darf nur einen einzigen Eintrag im Geschäftspartner-Register haben. Sonst funktioniert das hier nicht. */
LEFT JOIN C_BPartner AS Vendor ON(C_BPartner_Product.C_BPartner_ID = Vendor.C_Bpartner_ID)
/* und meine Auswahltabelle hinzunehmen, damit ich dann mit deren Werten filtern kann */
LEFT JOIN BAY_StatAdjust ON(BAY_StatAdjust.BAY_StatAdjust_ID = Parameter.BAY_StatAdjust_ID)
WHERE BAY_Statistikperiode.BAY_Statistikperiode_ID = Parameter.BAY_Statistikperiode_ID
  /* Die Auswahlkriterien werden alle einzeln AND-Verknüpft */
  AND (BAY_StatAdjust.Customer_ID = C_BPartner.C_BPartner_ID) IS DISTINCT FROM FALSE
  AND (BAY_StatAdjust.C_BP_Group_ID = C_BPartner.C_BP_Group_ID) IS DISTINCT FROM FALSE
  AND (BAY_StatAdjust.M_Product_ID = M_Product.M_Product_ID) IS DISTINCT FROM FALSE
  AND (BAY_StatAdjust.M_Product_Category_ID = M_Product.M_Product_Category_ID) IS DISTINCT FROM FALSE
  /* Die Lieferanten-Formel ist anders, weil der auch NULL sein kann. */
  AND (BAY_StatAdjust.Vendor_ID IS NULL OR BAY_StatAdjust.Vendor_ID = Vendor.C_BPartner_ID)
