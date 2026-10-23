
## Arquitectura

El sistema utiliza Flowable Work para gestionar el proceso BPMN. El External Worker ejecuta las tareas externas y se comunica con el Web Service Java REST.

## Flujo general

```text
[Flowable Work]
       |
       | Tarea disponible
       v
[External Worker]
       |
       | Llamada REST
       v
[Web Service Java REST]
       |
       | Respuesta REST
       v
[External Worker]
       |
       | Completar tarea
       v
[Flowable Work]
```

## Decisiones de arquitectura

| Fecha | Decisión | Motivo |
|---|---|---|
| 20-10-2026 | Flowable Work | Plataforma utilizada para el prototipo BPMN |
| 20-10-2026 | External Worker | Ejecutar tareas externas y comunicarse con el Web Service |
| 20-10-2026 | Web Service Java REST | Registrar leads y responder solicitudes del worker |
```
