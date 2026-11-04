/* ============================================================
   PROYECTO: PELUDITOS PET CHILE
   ARCHIVO: 01_Creacion_DB_PeluditosPetChile.sql

   GESTOR: Microsoft SQL Server
   HERRAMIENTA: SQL Server Management Studio 20
   LENGUAJE: Transact-SQL (T-SQL)

   DESCRIPCION:
   Creacion de la base de datos para gestionar leads,
   seguimientos, clientes, mascotas, fechas relevantes
   y oportunidades comerciales.
   ============================================================ */


/* ============================================================
   PASO 1. CREAR LA BASE DE DATOS
   ============================================================ */

USE master;
GO

IF DB_ID(N'DB_PeluditosPetChile') IS NULL
BEGIN
    CREATE DATABASE DB_PeluditosPetChile;
END;
GO

USE DB_PeluditosPetChile;
GO


/* ============================================================
   PASO 2. CREAR TABLA LEADS
   Almacena las personas que realizan consultas.
   ============================================================ */

IF OBJECT_ID(N'dbo.Leads', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Leads
    (
        id_lead INT IDENTITY(1,1) PRIMARY KEY,

        nombre NVARCHAR(100) NOT NULL,
        telefono NVARCHAR(30) NULL,
        correo NVARCHAR(150) NULL,
        canal NVARCHAR(50) NOT NULL,
        producto_interes NVARCHAR(255) NOT NULL,
        fecha_requerida DATE NULL,

        estado NVARCHAR(30) NOT NULL
            CONSTRAINT DF_Leads_Estado DEFAULT 'Nuevo',

        motivo_no_compra NVARCHAR(255) NULL,

        fecha_registro DATETIME2(0) NOT NULL
            CONSTRAINT DF_Leads_FechaRegistro
            DEFAULT SYSDATETIME(),

        process_instance_id NVARCHAR(100) NULL,

        CONSTRAINT CK_Leads_Estado
            CHECK (estado IN
            ('Nuevo', 'En seguimiento', 'Convertido', 'No convertido'))
    );
END;
GO


/* ============================================================
   PASO 3. CREAR TABLA SEGUIMIENTOS
   Registra los contactos realizados con cada lead.
   ============================================================ */

IF OBJECT_ID(N'dbo.Seguimientos', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Seguimientos
    (
        id_seguimiento INT IDENTITY(1,1) PRIMARY KEY,

        id_lead INT NOT NULL,

        fecha DATETIME2(0) NOT NULL
            CONSTRAINT DF_Seguimientos_Fecha
            DEFAULT SYSDATETIME(),

        medio NVARCHAR(50) NOT NULL,
        comentario NVARCHAR(500) NULL,
        resultado NVARCHAR(150) NULL,

        CONSTRAINT FK_Seguimientos_Leads
            FOREIGN KEY (id_lead)
            REFERENCES dbo.Leads(id_lead)
    );
END;
GO


/* ============================================================
   PASO 4. CREAR TABLA CLIENTES
   Almacena las personas que se convierten en clientes.
   ============================================================ */

IF OBJECT_ID(N'dbo.Clientes', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Clientes
    (
        id_cliente INT IDENTITY(1,1) PRIMARY KEY,

        nombre NVARCHAR(100) NOT NULL,
        telefono NVARCHAR(30) NULL,
        correo NVARCHAR(150) NULL,
        comuna NVARCHAR(100) NULL,

        acepta_comunicaciones BIT NOT NULL
            CONSTRAINT DF_Clientes_AceptaComunicaciones
            DEFAULT 0,

        fecha_registro DATETIME2(0) NOT NULL
            CONSTRAINT DF_Clientes_FechaRegistro
            DEFAULT SYSDATETIME(),

        lead_origen_id INT NULL,

        CONSTRAINT FK_Clientes_Leads
            FOREIGN KEY (lead_origen_id)
            REFERENCES dbo.Leads(id_lead)
    );
END;
GO


/* ============================================================
   PASO 5. CREAR TABLA MASCOTAS
   Almacena las mascotas asociadas a cada cliente.
   ============================================================ */

IF OBJECT_ID(N'dbo.Mascotas', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Mascotas
    (
        id_mascota INT IDENTITY(1,1) PRIMARY KEY,

        id_cliente INT NOT NULL,

        nombre NVARCHAR(100) NOT NULL,
        especie NVARCHAR(50) NOT NULL,
        fecha_nacimiento DATE NULL,
        preferencias NVARCHAR(255) NULL,

        CONSTRAINT FK_Mascotas_Clientes
            FOREIGN KEY (id_cliente)
            REFERENCES dbo.Clientes(id_cliente)
    );
END;
GO


/* ============================================================
   PASO 6. CREAR TABLA FECHAS RELEVANTES
   Registra fechas utilizadas para campañas comerciales.
   ============================================================ */

IF OBJECT_ID(N'dbo.FechasRelevantes', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.FechasRelevantes
    (
        id_fecha_relevante INT IDENTITY(1,1) PRIMARY KEY,

        nombre NVARCHAR(100) NOT NULL,
        tipo NVARCHAR(50) NOT NULL,
        fecha DATE NOT NULL,

        dias_anticipacion INT NOT NULL
            CONSTRAINT DF_Fechas_DiasAnticipacion
            DEFAULT 7,

        segmento NVARCHAR(100) NULL,

        activa BIT NOT NULL
            CONSTRAINT DF_Fechas_Activa
            DEFAULT 1,

        CONSTRAINT CK_Fechas_DiasAnticipacion
            CHECK (dias_anticipacion >= 0)
    );
END;
GO


/* ============================================================
   PASO 7. CREAR TABLA OPORTUNIDADES COMERCIALES
   Registra oportunidades asociadas a clientes, fechas
   relevantes y, opcionalmente, mascotas.
   ============================================================ */

IF OBJECT_ID(N'dbo.OportunidadesComerciales', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.OportunidadesComerciales
    (
        id_oportunidad INT IDENTITY(1,1) PRIMARY KEY,

        id_cliente INT NOT NULL,
        id_fecha_relevante INT NOT NULL,
        id_mascota INT NULL,

        estado NVARCHAR(30) NOT NULL
            CONSTRAINT DF_Oportunidades_Estado
            DEFAULT 'Generada',

        fecha_generacion DATETIME2(0) NOT NULL
            CONSTRAINT DF_Oportunidades_Fecha
            DEFAULT SYSDATETIME(),

        resultado NVARCHAR(255) NULL,
        process_instance_id NVARCHAR(100) NULL,

        CONSTRAINT FK_Oportunidades_Clientes
            FOREIGN KEY (id_cliente)
            REFERENCES dbo.Clientes(id_cliente),

        CONSTRAINT FK_Oportunidades_Fechas
            FOREIGN KEY (id_fecha_relevante)
            REFERENCES dbo.FechasRelevantes(id_fecha_relevante),

        CONSTRAINT FK_Oportunidades_Mascotas
            FOREIGN KEY (id_mascota)
            REFERENCES dbo.Mascotas(id_mascota),

        CONSTRAINT CK_Oportunidades_Estado
            CHECK (estado IN
            ('Generada', 'Contactada', 'Ganada', 'Sin compra', 'Descartada'))
    );
END;
GO


/* ============================================================
   PASO 8. CREAR INDICES
   Facilitan las busquedas y consultas de informacion.
   ============================================================ */

-- Indice para buscar leads por estado.

IF NOT EXISTS
(
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_Leads_Estado'
      AND object_id = OBJECT_ID(N'dbo.Leads')
)
BEGIN
    CREATE INDEX IX_Leads_Estado
    ON dbo.Leads(estado);
END;
GO


-- Indice para buscar seguimientos por lead.

IF NOT EXISTS
(
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_Seguimientos_Lead'
      AND object_id = OBJECT_ID(N'dbo.Seguimientos')
)
BEGIN
    CREATE INDEX IX_Seguimientos_Lead
    ON dbo.Seguimientos(id_lead);
END;
GO


-- Indice para buscar mascotas por cliente.

IF NOT EXISTS
(
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_Mascotas_Cliente'
      AND object_id = OBJECT_ID(N'dbo.Mascotas')
)
BEGIN
    CREATE INDEX IX_Mascotas_Cliente
    ON dbo.Mascotas(id_cliente);
END;
GO


-- Indice para buscar oportunidades por cliente.

IF NOT EXISTS
(
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_Oportunidades_Cliente'
      AND object_id = OBJECT_ID(N'dbo.OportunidadesComerciales')
)
BEGIN
    CREATE INDEX IX_Oportunidades_Cliente
    ON dbo.OportunidadesComerciales(id_cliente);
END;
GO


-- Indice para consultar fechas activas.

IF NOT EXISTS
(
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_FechasRelevantes_Activa_Fecha'
      AND object_id = OBJECT_ID(N'dbo.FechasRelevantes')
)
BEGIN
    CREATE INDEX IX_FechasRelevantes_Activa_Fecha
    ON dbo.FechasRelevantes(activa, fecha);
END;
GO


-- Evita que un lead origine mas de un cliente.

IF NOT EXISTS
(
    SELECT 1 FROM sys.indexes
    WHERE name = N'UX_Clientes_LeadOrigen'
      AND object_id = OBJECT_ID(N'dbo.Clientes')
)
BEGIN
    CREATE UNIQUE INDEX UX_Clientes_LeadOrigen
    ON dbo.Clientes(lead_origen_id)
    WHERE lead_origen_id IS NOT NULL;
END;
GO


/* ============================================================
   PASO 9. RESUMEN DE TABLAS CREADAS
   ============================================================ */

SELECT
    TABLE_NAME AS NombreTabla
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;
GO


/* ============================================================
   FIN DEL SCRIPT
   BASE DE DATOS: DB_PeluditosPetChile
   ============================================================ */