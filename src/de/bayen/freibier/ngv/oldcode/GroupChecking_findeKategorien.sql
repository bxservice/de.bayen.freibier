/*
 * Findet alle Gruppen-Kategorien, die einer bereits vorhandenen 
 * Tabellenspalte zugeordnet sind.
 */
SELECT
  AD_Table.tablename,
  AD_Column.columnname,
  AD_Column.ismandatory,
  BAY_Gruppenkategorie.*
FROM BAY_Gruppenkategorie
INNER JOIN AD_Table USING(AD_Table_ID)
INNER JOIN AD_Column USING(AD_Column_ID)
WHERE
  BAY_Gruppenkategorie.exklusiv='Y'
  AND BAY_Gruppenkategorie.AD_Client_ID=?
