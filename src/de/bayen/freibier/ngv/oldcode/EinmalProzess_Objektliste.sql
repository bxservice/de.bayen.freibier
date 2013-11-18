WITH objekte AS (
SELECT
  SUBSTRING(C_BPartner.Value FROM 3 FOR 3) AS Objekt,
  C_BPartner.name,
  C_BPartner.name2,
  C_Location.address1,
  C_Location.address2,
  C_Location.city,
  C_Location.postal,
  C_BPartner.isActive,
  max(BAY_Statistikperiode.startDate) AS startDate
FROM C_BPartner
JOIN C_BPartner_Location USING (C_BPartner_ID)
JOIN C_Location USING(C_Location_ID)
LEFT JOIN BAY_Umsatzstatistik USING (C_Bpartner_ID)
JOIN BAY_Statistikperiode USING (BAY_Statistikperiode_ID)
WHERE C_BPartner.AD_Client_ID = 1000000 
  AND C_BPartner.Value BETWEEN '10000' AND '39999'
GROUP BY
  SUBSTRING(C_BPartner.Value FROM 3 FOR 3),
  C_BPartner.name,
  C_BPartner.name2,
  C_Location.address1,
  C_Location.address2,
  C_Location.city,
  C_Location.postal,
  C_BPartner.isActive
ORDER BY
  SUBSTRING(C_BPartner.Value FROM 3 FOR 3)
)
SELECT
  *
FROM objekte AS o1
JOIN (
  SELECT 
    objekt,
    max(startDate) AS startDate
  FROM objekte
  GROUP BY objekt
) AS o2 USING (objekt, startDate)
;
