/* ============================================================
   PROYECTO: PELUDITOS PET CHILE
   ARCHIVO: 02_Inserciones_Prueba.sql

   OBJETIVO:
   Probar la insercion de datos en las seis tablas
   y verificar sus relaciones.

   IMPORTANTE:
   Los datos son ficticios.
   Se utiliza ROLLBACK para no conservar los registros.
   ============================================================ */

USE DB_PeluditosPetChile;
GO

SET XACT_ABORT ON;
GO

BEGIN TRY

    BEGIN TRANSACTION;

    /* ========================================================
       1. INSERTAR LEAD
       ======================================================== */

    INSERT INTO dbo.Leads
    (
        nombre,
        telefono,
        correo,
        canal,
        producto_interes,
        estado
    )
    VALUES
    (
        N'Cliente Prueba',
        N'900000000',
        N'prueba@ejemplo.cl',
        N'Instagram',
        N'Pastel para mascotas',
        N'Nuevo'
    );

    DECLARE @id_lead INT = SCOPE_IDENTITY();


    /* ========================================================
       2. INSERTAR SEGUIMIENTO
       ======================================================== */

    INSERT INTO dbo.Seguimientos
    (
        id_lead,
        medio,
        comentario,
        resultado
    )
    VALUES
    (
        @id_lead,
        N'WhatsApp',
        N'Se envio informacion del producto',
        N'Pendiente de respuesta'
    );


    /* ========================================================
       3. INSERTAR CLIENTE
       ======================================================== */

    INSERT INTO dbo.Clientes
    (
        nombre,
        telefono,
        correo,
        comuna,
        acepta_comunicaciones,
        lead_origen_id
    )
    VALUES
    (
        N'Cliente Prueba',
        N'900000000',
        N'prueba@ejemplo.cl',
        N'Santiago',
        0,
        @id_lead
    );

    DECLARE @id_cliente INT = SCOPE_IDENTITY();


    /* ========================================================
       4. INSERTAR MASCOTA
       ======================================================== */

    INSERT INTO dbo.Mascotas
    (
        id_cliente,
        nombre,
        especie,
        fecha_nacimiento,
        preferencias
    )
    VALUES
    (
        @id_cliente,
        N'Max',
        N'Perro',
        '2022-12-25',
        N'Pasteles de pollo'
    );

    DECLARE @id_mascota INT = SCOPE_IDENTITY();


    /* ========================================================
       5. INSERTAR FECHA RELEVANTE
       ======================================================== */

    INSERT INTO dbo.FechasRelevantes
    (
        nombre,
        tipo,
        fecha,
        dias_anticipacion,
        segmento,
        activa
    )
    VALUES
    (
        N'Navidad',
        N'Festividad',
        '2026-12-25',
        7,
        N'Todos los clientes',
        1
    );

    DECLARE @id_fecha INT = SCOPE_IDENTITY();


    /* ========================================================
       6. INSERTAR OPORTUNIDAD COMERCIAL
       ======================================================== */

    INSERT INTO dbo.OportunidadesComerciales
    (
        id_cliente,
        id_fecha_relevante,
        id_mascota,
        estado,
        resultado
    )
    VALUES
    (
        @id_cliente,
        @id_fecha,
        @id_mascota,
        N'Generada',
        N'Oportunidad de campaña de Navidad'
    );


    /* ========================================================
       7. CONSULTAR INFORMACION RELACIONADA
       ======================================================== */

    SELECT
        L.id_lead,
        L.nombre AS Lead,
        L.canal AS Canal,
        S.medio AS MedioSeguimiento,
        C.nombre AS Cliente,
        M.nombre AS Mascota,
        M.especie AS Especie,
        F.nombre AS FechaRelevante,
        O.estado AS EstadoOportunidad
    FROM dbo.Leads L
    INNER JOIN dbo.Seguimientos S
        ON L.id_lead = S.id_lead
    INNER JOIN dbo.Clientes C
        ON L.id_lead = C.lead_origen_id
    INNER JOIN dbo.Mascotas M
        ON C.id_cliente = M.id_cliente
    INNER JOIN dbo.OportunidadesComerciales O
        ON C.id_cliente = O.id_cliente
    INNER JOIN dbo.FechasRelevantes F
        ON O.id_fecha_relevante = F.id_fecha_relevante
    WHERE L.id_lead = @id_lead;


    /* ========================================================
       8. MOSTRAR MENSAJE DE PRUEBA
       ======================================================== */

    PRINT 'Prueba de insercion y relaciones completada.';

    /* ========================================================
       9. REVERTIR LOS DATOS DE PRUEBA
       ======================================================== */

    ROLLBACK TRANSACTION;

    PRINT 'Los datos ficticios fueron revertidos correctamente.';

END TRY

BEGIN CATCH

    IF XACT_STATE() <> 0
        ROLLBACK TRANSACTION;

    PRINT 'Error durante la prueba:';
    PRINT ERROR_MESSAGE();

    THROW;

END CATCH;
GO