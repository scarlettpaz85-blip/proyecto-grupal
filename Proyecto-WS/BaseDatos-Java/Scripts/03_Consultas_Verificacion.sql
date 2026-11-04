
/* =========================================================
   PROYECTO: PELUDITOS PET CHILE
   SCRIPT 03: CONSULTAS DE VERIFICACION
   BASE DE DATOS: DB_PeluditosPetChile

   OBJETIVO:
   Revisar tablas, columnas, relaciones, indices,
   restricciones y cantidad de registros.

   IMPORTANTE:
   Este script solo realiza consultas SELECT.
   No modifica ni elimina informacion.
   ========================================================= */


/* =========================================================
   1. SELECCIONAR LA BASE DE DATOS
   ========================================================= */

USE DB_PeluditosPetChile;
GO


/* =========================================================
   2. VERIFICAR QUE EXISTAN LAS SEIS TABLAS
   ========================================================= */

PRINT '1. VERIFICACION DE TABLAS';

SELECT
    TABLE_SCHEMA AS Esquema,
    TABLE_NAME AS NombreTabla
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;
GO


/* =========================================================
   3. VERIFICAR LAS COLUMNAS Y SUS TIPOS DE DATOS
   ========================================================= */

PRINT '2. VERIFICACION DE COLUMNAS';

SELECT
    TABLE_NAME AS NombreTabla,
    COLUMN_NAME AS NombreColumna,
    DATA_TYPE AS TipoDato,
    CHARACTER_MAXIMUM_LENGTH AS Longitud,
    IS_NULLABLE AS PermiteNulos
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME IN (
    'Leads',
    'Seguimientos',
    'Clientes',
    'Mascotas',
    'FechasRelevantes',
    'OportunidadesComerciales'
)
ORDER BY TABLE_NAME, ORDINAL_POSITION;
GO


/* =========================================================
   4. VERIFICAR LAS RELACIONES Y CLAVES FORANEAS
   ========================================================= */

PRINT '3. VERIFICACION DE RELACIONES';

SELECT
    fk.name AS NombreRelacion,
    OBJECT_NAME(fk.parent_object_id) AS TablaOrigen,
    COL_NAME(
        fkc.parent_object_id,
        fkc.parent_column_id
    ) AS ColumnaOrigen,
    OBJECT_NAME(fk.referenced_object_id) AS TablaRelacionada,
    COL_NAME(
        fkc.referenced_object_id,
        fkc.referenced_column_id
    ) AS ColumnaRelacionada
FROM sys.foreign_keys AS fk
INNER JOIN sys.foreign_key_columns AS fkc
    ON fk.object_id = fkc.constraint_object_id
ORDER BY TablaOrigen, NombreRelacion;
GO


/* =========================================================
   5. VERIFICAR LOS INDICES
   ========================================================= */

PRINT '4. VERIFICACION DE INDICES';

SELECT
    t.name AS NombreTabla,
    i.name AS NombreIndice,
    i.type_desc AS TipoIndice,
    i.is_unique AS EsUnico,
    i.is_primary_key AS EsClavePrimaria,
    i.has_filter AS TieneFiltro
FROM sys.indexes AS i
INNER JOIN sys.tables AS t
    ON i.object_id = t.object_id
WHERE t.name IN (
    'Leads',
    'Seguimientos',
    'Clientes',
    'Mascotas',
    'FechasRelevantes',
    'OportunidadesComerciales'
)
AND i.name IS NOT NULL
ORDER BY t.name, i.name;
GO


/* =========================================================
   6. VERIFICAR LAS RESTRICCIONES
   ========================================================= */

PRINT '5. VERIFICACION DE RESTRICCIONES';

SELECT
    t.name AS NombreTabla,
    cc.name AS NombreRestriccion,
    'CHECK' AS TipoRestriccion,
    cc.definition AS Definicion
FROM sys.check_constraints AS cc
INNER JOIN sys.tables AS t
    ON cc.parent_object_id = t.object_id

UNION ALL

SELECT
    t.name AS NombreTabla,
    dc.name AS NombreRestriccion,
    'DEFAULT' AS TipoRestriccion,
    dc.definition AS Definicion
FROM sys.default_constraints AS dc
INNER JOIN sys.tables AS t
    ON dc.parent_object_id = t.object_id

ORDER BY NombreTabla, TipoRestriccion;
GO


/* =========================================================
   7. VERIFICAR LA CANTIDAD DE REGISTROS DE CADA TABLA
   ========================================================= */

PRINT '6. VERIFICACION DE REGISTROS';

SELECT 'Leads' AS NombreTabla, COUNT(*) AS TotalRegistros
FROM dbo.Leads

UNION ALL

SELECT 'Seguimientos', COUNT(*)
FROM dbo.Seguimientos

UNION ALL

SELECT 'Clientes', COUNT(*)
FROM dbo.Clientes

UNION ALL

SELECT 'Mascotas', COUNT(*)
FROM dbo.Mascotas

UNION ALL

SELECT 'FechasRelevantes', COUNT(*)
FROM dbo.FechasRelevantes

UNION ALL

SELECT 'OportunidadesComerciales', COUNT(*)
FROM dbo.OportunidadesComerciales;
GO


/* =========================================================
   8. MENSAJE FINAL
   ========================================================= */

PRINT 'VERIFICACION FINALIZADA CORRECTAMENTE.';
GO
