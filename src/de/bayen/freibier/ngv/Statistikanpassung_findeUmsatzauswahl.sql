/*
  Auswahl aller Datensätze für die Anweisung "umbuchenKunde"
*/
/*
WITH Parameter AS (
  ? AS BAY_Statistikperiode_ID,
  ? AS AD_Client_ID
)
*/
SELECT
  BAY_StatAdjust.BAY_StatAdjust_ID
FROM BAY_Statistikperiode
INNER JOIN BAY_StatAdjust
  ON(
    /* hier die Periode einsetzen */
/*    BAY_Statistikperiode.BAY_Statistikperiode_ID = Parameter.BAY_Statistikperiode_ID*/
    BAY_Statistikperiode.BAY_Statistikperiode_ID = ?
    /* Finde heraus, ob der Zeitraum der Auswahl in der angewählten Periode ist */
    AND coalesce(BAY_StatAdjust.startdate < BAY_Statistikperiode.enddate, true)
    AND coalesce(BAY_StatAdjust.enddate > BAY_Statistikperiode.startdate, true)
    AND BAY_StatAdjust.isActive='Y'
  )
ORDER BY BAY_StatAdjust.Value
;
