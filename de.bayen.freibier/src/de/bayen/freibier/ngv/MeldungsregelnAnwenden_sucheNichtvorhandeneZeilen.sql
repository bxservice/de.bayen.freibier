/*
 * Auswahl aller Datensaetze z.B. für die Anweisung "umbuchenKunde".
 * 
 * Dieser Query ist eine abgewandelte Version von "sucheZeilen". Hier
 * werden genau die Zeilen heruasgesucht, die es nicht gibt. Dadurch ist
 * es möglich, eine Addition oder eine Mindestmenge auf Artikel 
 * vorzunehmen, die in diesem Monat gar nicht bezogen wurden.
 *
 * Ein kleines Problem hat der Query, das ich dann aber in Java abfange:
 * Sind keine Artikel angegeben (gleich alle Artikel), aber der Kunde hat
 * überhaupt nichts bezogen, ergibt sich (fälschlicherweise) eine einzige 
 * leere Zeile.
*/

SELECT
  BAY_Umsatzstatistik.*,
  myBPartner.C_BPartner_ID AS myBPartner_ID,
  myProduct.M_Product_ID AS myProduct_ID
/* Im folgenden werden alle betroffenen Geschaeftspartner durch ein
   UNION herausgesucht. Da das hier am Ende per RIGHT JOIN mit "eins"
   verknüpft wird, ergibt sich im Zweifelsfall NULL, was dann 
   bedeutet, das es keine Einschraenkung gibt, das hier also für alle 
   Geschaeftspartner gilt.
*/
FROM (
  /* Der erste Select holt alle Kunden, die direkt ausgewaehlt wurden */
  SELECT
    C_BPartner.C_BPartner_ID
  FROM BAY_Auswahlkunde
  INNER JOIN C_BPartner USING(C_BPartner_ID)
  WHERE BAY_Auswahlkunde.isActive='Y'
        AND BAY_Auswahlkunde.BAY_Umsatzauswahl_ID=?
  UNION
  /* der zweite Select holt alle Kunden, die durch eine Gruppe definiert sind */
  SELECT
    C_BPartner.C_BPartner_ID
  FROM BAY_Auswahlkundengruppe
  INNER JOIN BAY_Gruppe USING(BAY_Gruppe_ID)
  INNER JOIN BAY_BPGruppe USING(BAY_Gruppe_ID)
  INNER JOIN C_BPartner USING(C_BPartner_ID)
  WHERE BAY_Auswahlkundengruppe.isActive='Y'
        AND BAY_Auswahlkundengruppe.BAY_Umsatzauswahl_ID=?
) AS myBPartner
RIGHT JOIN (SELECT ? AS BAY_Umsatzauswahl_ID) AS eins ON(true)

/* Im folgenden werden alle betroffenen Artikel durch ein
   UNION herausgesucht. Da das hier per LEFT JOIN verknüpft wird,
   ergibt sich im Zweifelsfall NULL, was dann bedeutet, das es
   keine Einschränkung gibt, das hier also für alle Artikel
   gilt.
*/
LEFT JOIN (
  /* Der erste Select holt alle Artikel, die direkt ausgewählt wurden */
  SELECT
    M_Product.M_Product_ID,
    BAY_Auswahlartikel.BAY_Umsatzauswahl_ID
  FROM BAY_Auswahlartikel
  INNER JOIN M_Product USING(M_Product_ID)
  WHERE BAY_Auswahlartikel.isActive='Y'
        AND BAY_Auswahlartikel.BAY_Umsatzauswahl_ID=?
  UNION
  /* der zweite Select holt alle Artikel, die durch eine Gruppe definiert sind */
  SELECT
    M_Product.M_Product_ID,
    BAY_Auswahlartikelgruppe.BAY_Umsatzauswahl_ID
  FROM BAY_Auswahlartikelgruppe
  INNER JOIN BAY_Gruppe USING(BAY_Gruppe_ID)
  INNER JOIN BAY_Artikelgruppe USING(BAY_Gruppe_ID)
  INNER JOIN M_Product USING(M_Product_ID)
  WHERE BAY_Auswahlartikelgruppe.isActive='Y'
        AND BAY_Auswahlartikelgruppe.BAY_Umsatzauswahl_ID=?
)
AS myProduct ON (true)

/* Bis hierhin habe ich eine Liste mit allen Kunden und allen Artikeln, 
 * die für diese Regel in Frage kommen.
 *  
 * Jetzt hole ich als naechstes alle passenden Eintraege aus der Statistik.
 */
/* Ich möchte nur die raussuchen, für die es keinen Eintrag in der Statistik gibt (deshalb LEFT JOIN) */
LEFT JOIN (
  SELECT * FROM BAY_Umsatzstatistik
  /* natürlich nur aus der Periode, um die es hier geht */
  WHERE BAY_Umsatzstatistik.BAY_Statistikperiode_ID=?
) AS BAY_Umsatzstatistik
ON (
  /* ist gar kein Artikel angegeben, nehme ich alle */
  coalesce(BAY_Umsatzstatistik.M_Product_ID = myProduct.M_Product_ID,true)
  AND
  /* ist gar kein Kunde angegeben, nehme ich alle */
  coalesce(BAY_Umsatzstatistik.C_BPartner_ID = myBPartner.C_BPartner_ID,true)
)
WHERE BAY_Umsatzstatistik.BAY_Umsatzstatistik_ID IS NULL
;
