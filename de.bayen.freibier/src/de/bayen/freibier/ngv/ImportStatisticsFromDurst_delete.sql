DELETE FROM BAY_Umsatzstatistik BAY_Umsatzstatistik USING BAY_Statistikperiode
WHERE 
  BAY_Statistikperiode.BAY_Statistikperiode_ID=BAY_Umsatzstatistik.BAY_Statistikperiode_ID
  AND BAY_Statistikperiode.Value=?
;
