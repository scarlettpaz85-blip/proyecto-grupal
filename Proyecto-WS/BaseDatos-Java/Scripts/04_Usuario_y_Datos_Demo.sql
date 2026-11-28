/* =========================================================================
   Peluditos Pet Chile – 04_Usuario_y_Datos_Demo.sql
   Ejecutar DESPUÉS de 01_Creacion_DB_PeluditosPetChile.sql y 02_Inserciones_Prueba.sql
   1) Crea el login y el usuario que usa el web service (peluditos_java).
   2) Agrega fechas de campaña y clientes de demostración que aceptan
      comunicaciones, para que las campañas generen tareas.
   Reemplace <CONTRASEÑA_SEGURA> antes de ejecutar. NO suba la contraseña real al repositorio.
   ========================================================================= */

USE master;
GO

IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = N'peluditos_java')
    CREATE LOGIN peluditos_java WITH PASSWORD = N'<12345>', CHECK_POLICY = OFF;
GO

USE DB_PeluditosPetChile;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = N'peluditos_java')
    CREATE USER peluditos_java FOR LOGIN peluditos_java;
GO
ALTER ROLE db_datareader ADD MEMBER peluditos_java;
ALTER ROLE db_datawriter ADD MEMBER peluditos_java;
GO

/* ---------- Fechas de campaña adicionales ---------- */
IF NOT EXISTS (SELECT 1 FROM dbo.FechasRelevantes WHERE nombre = N'Año Nuevo')
    INSERT INTO dbo.FechasRelevantes (nombre, tipo, fecha, dias_anticipacion, segmento, activa)
    VALUES (N'Año Nuevo', N'Festividad', '2027-01-01', 7, N'Todos los clientes', 1);

IF NOT EXISTS (SELECT 1 FROM dbo.FechasRelevantes WHERE nombre = N'Día del Perro')
    INSERT INTO dbo.FechasRelevantes (nombre, tipo, fecha, dias_anticipacion, segmento, activa)
    VALUES (N'Día del Perro', N'Campaña', '2027-07-21', 7, N'Todos los clientes', 1);
GO

/* ---------- Clientes de demostración (aceptan comunicaciones) ---------- */
IF NOT EXISTS (SELECT 1 FROM dbo.Clientes WHERE correo = N'camila.rojas@ejemplo.cl')
BEGIN
    INSERT INTO dbo.Clientes (nombre, telefono, correo, comuna, acepta_comunicaciones)
    VALUES (N'Camila Rojas', N'+56911112222', N'camila.rojas@ejemplo.cl', N'Puente Alto', 1);
    INSERT INTO dbo.Mascotas (id_cliente, nombre, especie)
    VALUES (SCOPE_IDENTITY(), N'Luna', N'Perro');
END

IF NOT EXISTS (SELECT 1 FROM dbo.Clientes WHERE correo = N'diego.munoz@ejemplo.cl')
BEGIN
    INSERT INTO dbo.Clientes (nombre, telefono, correo, comuna, acepta_comunicaciones)
    VALUES (N'Diego Muñoz', N'+56933334444', N'diego.munoz@ejemplo.cl', N'Maipú', 1);
    INSERT INTO dbo.Mascotas (id_cliente, nombre, especie)
    VALUES (SCOPE_IDENTITY(), N'Toby', N'Perro');
END
GO

/* ---------- Verificación ---------- */
SELECT id_fecha_relevante, nombre, fecha, activa FROM dbo.FechasRelevantes;
SELECT c.id_cliente, c.nombre, c.acepta_comunicaciones, m.nombre AS mascota
FROM dbo.Clientes c LEFT JOIN dbo.Mascotas m ON m.id_cliente = c.id_cliente;
GO