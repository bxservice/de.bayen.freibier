/*
  Auswahl aller Datensätze für die Anweisung "umbuchenKunde"
*/

SELECT
  BAY_StatAdjust.BAY_StatAdjust_ID
FROM BAY_Statistikperiode
INNER JOIN BAY_StatAdjust
  ON(
    /* hier die Periode einsetzen */
    BAY_Statistikperiode.BAY_Statistikperiode_ID=?
    /* Finde heraus, ob der Zeitraum der Auswahl in der angewählten Periode ist */
    AND coalesce(BAY_Umsatzauswahl.startdate<BAY_Statistikperiode.enddate,true)
    AND coalesce(BAY_Umsatzauswahl.enddate>BAY_Statistikperiode.startdate,true)
    AND BAY_Umsatzauswahl.isActive='Y'
  )
ORDER BY BAY_Umsatzauswahl.Sequence
;
