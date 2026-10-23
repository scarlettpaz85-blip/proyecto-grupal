
# Diagrama de arquitectura — Peluditos Pet Chile



```mermaid id="7v1m2p"
flowchart TB
    F["Flowable Work<br/>Proceso BPMN"]
    W["External Worker<br/>Integración implementada"]
    S["Web Service Java REST<br/>Servicio integrado"]

    F -->|"Tarea disponible"| W
    W -->|"Llamada REST"| S
    S -->|"Respuesta REST"| W
    W -->|"Completar tarea"| F
```




## Estado de implementación

- Flowable Work: proceso conectado al External Worker.
- External Worker: integración con Flowable y Web Service implementada.
- Web Service Java REST: integrado con el External Worker.
- Integración completa: probada de extremo a extremo.


