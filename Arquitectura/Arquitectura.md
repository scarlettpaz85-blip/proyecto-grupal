
# Diagrama de arquitectura — Peluditos Pet Chile

flowchart LR
    F["Flowable Cloud<br/>Proceso BPMN"]
    W["External Worker<br/>Integración pendiente"]
    S["Web Service Java REST<br/>Prueba local realizada"]

    F -. "Conexión por confirmar" .-> W
    W -. "Llamada REST por confirmar" .-> S
    S -. "Respuesta REST" .-> W
    W -. "Continuación del proceso por confirmar" .-> F

    classDef probado fill:#e8f5e9,stroke:#2e7d32,color:#1b5e20;
    classDef pendiente fill:#fff3e0,stroke:#ef6c00,color:#7f3b00;

    class S probado;
    class F,W pendiente;
