
# Diagrama de arquitectura — Peluditos Pet Chile

```mermaid
flowchart TD
    A["Flowable Enterprise<br/>Proceso BPMN TO-BE"]
    B["External Worker<br/>Ejecuta la tarea externa"]
    C["Web Service Java<br/>Procesa la solicitud"]

    A -->|"Entrega trabajo"| B
    B -->|"Invoca servicio"| C
    C -->|"Respuesta"| B
    B -->|"Continúa el proceso"| A
```
## Estado de los componentes

- **Flowable Enterprise:** [ En desarrollo]
- **External Worker:** [Planificado]
- **Web Service Java:** [planificado]
