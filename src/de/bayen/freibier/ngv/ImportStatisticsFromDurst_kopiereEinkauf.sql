WITH params AS(
  /*
   * Parameter, die diesem SQL-Ausruck übergeben werden, werden in dieser Tabelle
   * zentral gesammelt und brauchen dadurch auch garantiert nur einmal angegeben
   * zu werden.
   *
   * SELECT 'lm1201'::text AS periode
   */
  SELECT ?::text AS periode
), vergangeneMonate AS (
  /* alle Monats-Einkaufsstatistiken aus diesem Jahr, die vor dem aktuellen Monat liegen */
  SELECT *
  FROM params,
    BAY_Statistikperiode
  WHERE 
    BAY_Statistikperiode.Value LIKE 
      'lm' || substring(params.periode from 3 for 2) || '__%'
    AND 
      substring(BAY_Statistikperiode.Value from 5 for 2)::numeric
      <
      substring(params.periode from 5 for 2)::numeric
), monatsSumme AS(
  /* Die Summen aller Umsätze der letzten Monate */
  SELECT
    us.AD_Client_ID,
    us.AD_Org_ID,
    us.C_BPartner_ID,
    us.M_Product_ID,
    sum(us.Lieferungen) AS Lieferungen,
    sum(us.Stueck) AS Stueck,
    sum(us.StueckGratis) AS StueckGratis,
    sum(us.Umsatz) AS Umsatz,
    sum(us.UmsatzEK) AS UmsatzEK
  FROM
    BAY_Umsatzstatistik AS us
    INNER JOIN vergangeneMonate USING (BAY_Statistikperiode_ID)
  GROUP BY
    us.AD_Client_ID,
    us.AD_Org_ID,
    us.C_BPartner_ID,
    us.M_Product_ID
), jahresSumme AS (
  /* Die Jahressumme, die ja (aus DURST importiert) in einer einzelnen 
   * Periodentabelle vorliegt 
   */
  SELECT BAY_Umsatzstatistik.*
  FROM params,
    BAY_Umsatzstatistik
    INNER JOIN BAY_Statistikperiode USING (BAY_Statistikperiode_ID)
  WHERE BAY_Statistikperiode.Value='lk' || substring(params.periode from 3 for 2) || '00'
)
/* Die Monatssummen der vergangenen Monate werden von der Jahressumme abgezogen.
 * Die Differenz muss dann die Summe des aktuellen Monats sein.
 */
SELECT
  AD_Client_ID,
  AD_Org_ID,
  C_BPartner_ID,
  M_Product_ID,
  coalesce(jahresSumme.Lieferungen,0)  - coalesce(monatsSumme.Lieferungen,0) 
    AS Lieferungen,
  coalesce(jahresSumme.Stueck,0)       - coalesce(monatsSumme.Stueck,0)
    AS Stueck,
  coalesce(jahresSumme.StueckGratis,0) - coalesce(monatsSumme.StueckGratis,0)
    AS StueckGratis,
  coalesce(jahresSumme.Umsatz,0)       - coalesce(monatsSumme.Umsatz,0)
    AS Umsatz,
  coalesce(jahresSumme.UmsatzEK,0)     - coalesce(monatsSumme.UmsatzEK,0)
    AS UmsatzEK
FROM 
  monatsSumme
  FULL OUTER JOIN jahresSumme USING(
    AD_Client_ID,
    AD_Org_ID,
    C_BPartner_ID,
    M_Product_ID
  )
  WHERE
    abs(coalesce(jahresSumme.Stueck,0)-coalesce(monatsSumme.Stueck,0))>=1 
    OR abs(coalesce(jahresSumme.StueckGratis,0)-coalesce(monatsSumme.StueckGratis,0))>=1
;
