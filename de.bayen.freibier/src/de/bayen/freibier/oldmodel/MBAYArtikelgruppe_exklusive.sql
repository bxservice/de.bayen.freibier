/**
 * Ermittelt zu einer neuen Artikelgruppe alle anderen Gruppen, die dafür 
 * gelöscht werden müssen.
 */

SELECT zuloeschen.* 
/* das bin ich selbst */
FROM BAY_Artikelgruppe AS ich
/* und das ist meine Gruppe (und mich selbst lege ich auch hier mit "?" fest) */
JOIN BAY_Gruppe meinegruppe 
  ON(meinegruppe.BAY_Gruppe_ID=ich.BAY_Gruppe_ID AND ich.BAY_Artikelgruppe_ID=?)
/* das ist meine Gruppenkategorie, wenn diese exklusiv ist (sonst ergibt sich ab hier nichts mehr) */
INNER JOIN BAY_Gruppenkategorie AS kat 
  ON(kat.BAY_Gruppenkategorie_ID=meinegruppe.BAY_Gruppenkategorie_ID AND kat.exklusiv='Y')
/* das ergibt alle anderen Gruppen dieser Kategorie */
INNER JOIN BAY_Gruppe AS anderegruppen
  ON(anderegruppen.BAY_Gruppenkategorie_ID=kat.BAY_Gruppenkategorie_ID)
/* Andere Artikelgruppen-Objekte, die eine dieser Gruppen haben */
INNER JOIN BAY_Artikelgruppe AS zuloeschen
  ON(
    zuloeschen.BAY_Gruppe_ID = anderegruppen.BAY_Gruppe_ID 
    /* nur die, die dem gleichen Produkt zugeordnet sind */
    AND zuloeschen.M_Product_ID = ich.M_Product_ID 
    /* aber nicht ich selber */
    AND zuloeschen.BAY_Artikelgruppe_ID != ich.BAY_Artikelgruppe_ID
  )
;
