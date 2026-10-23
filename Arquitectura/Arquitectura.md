
# Diagrama de arquitectura — Peluditos Pet Chile

```mermaid
flowchart LR
    F["Flowable Cloud - Proceso BPMN"]
    W["External Worker - Integración pendiente"]
    S["Web Service Java REST - Prueba local realizada"]

    F -. "Conexión por confirmar" .-> W
    W -. "Llamada REST por confirmar" .-> S
    S -. "Respuesta REST" .-> W
    W -. "Continuación del proceso por confirmar" .-> F
```

## Estado de implementación

- Web Service Java REST: prueba local evidenciada por Christopher.
- External Worker: conexión e integración pendientes de confirmar.
- Integración completa del proceso: pendiente de prueba.


