# Arquitectura

Guardar aquí `modelo-arquitectura.png` (o `.puml` / `.drawio`).

Flujo general:

```
[Usuario] → [Proyecto-UI] → REST → [Flowable REST (motor = fuente de la verdad)]
                                         ↑ fetch-and-lock (topic)
                                   [Proyecto-Worker] → REST/SOAP → [Proyecto-WS] → [Base de datos]
```

## Decisiones de arquitectura

| Fecha | Decisión | Motivo |
|---|---|---|
| | Flowable OSS 7.2 | Material del curso basado en 7.2 |
| | | |
