# Arquitectura

- Diagrama y estado de cada componente: [`Arquitectura.md`](Arquitectura.md)
- Imagen del diagrama: [`modelo-arquitectura.png`](modelo-arquitectura.png)

## Decisiones de arquitectura

| Fecha | Decisión | Motivo |
|---|---|---|
| 20-10-2026 | External worker para invocar servicios desde el proceso | Desacopla Flowable de la lógica de negocio y permite usar cualquier lenguaje |
| 20-10-2026 | Web service Java REST (Jakarta REST + Jersey) | Especificación del curso; simple de consumir desde el worker y la interfaz |
| 27-10-2026 | Flowable OSS 7.2 local en lugar de Flowable Cloud | Requisito de la Entrega 2; la nube presentó fallas con external workers y luego quedó fuera de servicio por un incidente de seguridad |
| 27-10-2026 | PostgreSQL para el motor y para el negocio, en bases separadas | Compatible con Flowable 7.2; separar responsabilidades permite gestionar y respaldar cada una por separado |
| 27-10-2026 | Interfaz propia (tecnología a cargo de Belén) | Reemplaza los formularios de Flowable Enterprise, según la Entrega 2 |
