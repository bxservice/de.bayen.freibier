SELECT
  M_Product.Value AS ObjectValue,
  M_Product.Name AS ObjectName,
  M_Product.isActive,
  M_Product_Category.Value AS Spaltenwert,
  M_Product_Category.Name AS Spaltenname,
  BAY_Gruppe.Value,
  BAY_Gruppe.Name
--  ,*
FROM
BAY_Gruppenkategorie
INNER JOIN M_Product USING (AD_Client_ID)
LEFT JOIN M_Product_Category USING (M_Product_Category_ID)
LEFT JOIN BAY_Artikelgruppe ON (
  BAY_Artikelgruppe.M_Product_ID=M_Product.M_Product_ID 
)
LEFT JOIN BAY_Gruppe USING (BAY_Gruppe_ID, BAY_Gruppenkategorie_ID)
WHERE
  BAY_Gruppenkategorie.BAY_Gruppenkategorie_ID=1000002
  AND M_Product_Category.Value != BAY_Gruppe.Value
ORDER BY
  M_Product.Value
