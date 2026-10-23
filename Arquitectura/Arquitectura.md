
# Diagrama de arquitectura — Peluditos Pet Chile


```mermaid
flowchart TB
    F["Flowable Cloud<br/>Proceso BPMN"]
    W["External Worker<br/>Integración pendiente"]
    S["Web Service Java REST<br/>Prueba local realizada"]

    F -.->|"Conexión por confirmar"| W
    W -.->|"Llamada REST por confirmar"| S
    S -.->|"Respuesta REST"| W
    W -.->|"Continuación del proceso por confirmar"| F

    F --- NF["Estado: configuración por confirmar"]
    W --- NW["Estado: conexión pendiente"]
    S --- NS["Estado: prueba local evidenciada"]
```


## Estado de implementación

- Web Service Java REST: prueba local evidenciada por Christopher.
- External Worker: conexión e integración pendientes de confirmar.
- Integración completa del proceso: pendiente de prueba.


