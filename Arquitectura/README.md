# Arquitectura

- Diagrama y estado de cada componente: [`Arquitectura.md`](Arquitectura.md)
- Imagen del diagrama: [`modelo-arquitectura.png`](modelo-arquitectura.png)

## Decisiones de arquitectura

| Fecha | Decisión | Motivo |
|---|---|---|
| 20-10-2026 | External worker para invocar servicios desde el proceso | Desacopla Flowable de la lógica de negocio y permite usar cualquier lenguaje |
| 20-10-2026 | Web service Java REST (Jakarta REST + Jersey) | Especificación del curso; simple de consumir desde el worker y la interfaz |
| 27-10-2026 | Flowable OSS 7.2 local en lugar de Flowable Cloud | Requisito de la Entrega 2; la nube presentó fallas con external workers y luego quedó fuera de servicio por un incidente de seguridad |
| 27-10-2026 | PostgreSQL para la base de datos del motor de Flowable | Base relacional compatible con Flowable 7.2 |
| 03-11-2026 | Microsoft SQL Server 2017 para la base de datos de negocio | La responsable ya construyó el esquema (6 tablas) en SQL Server; el documento de la Entrega 2 lo admite y permite usar tecnologías distintas para el motor y el negocio. Se accede desde el web service mediante JDBC (driver mssql-jdbc) |
| 03-11-2026 | Interfaz propia en TypeScript + React + Vite | Reemplaza los formularios de Flowable Enterprise; TypeScript ayuda a respetar las variables del proceso y el proxy de Vite evita configurar CORS |
