# Diagrama de arquitectura — Peluditos Pet Chile

> Versión: **Entrega 2** (Flowable OSS, interfaz propia y persistencia). Fuente editable: este bloque Mermaid. Imagen: [`modelo-arquitectura.png`](modelo-arquitectura.png).

```mermaid
flowchart TB
    U["Emprendedora<br/>(navegador)"]
    UI["Interfaz propia<br/>Proyecto-UI<br/>(tecnología definida por Belén)"]
    F["Flowable OSS 7.2<br/>Tomcat 10 + flowable-rest<br/>:8080"]
    W["External worker<br/>Proyecto-Worker · Java 21"]
    S["Web service REST<br/>Proyecto-WS · Java 21 + Jersey<br/>:8081"]
    DBF[("PostgreSQL<br/>BD del motor: flowable")]
    DBN[("PostgreSQL<br/>BD de negocio: peluditos")]

    U --> UI
    UI -->|"REST: iniciar proceso,<br/>bandeja de tareas, completar tareas"| F
    UI -.->|"REST: consultar leads<br/>(planificado)"| S
    W <-->|"external-job-api:<br/>adquirir y completar trabajos<br/>topics peluditos-lead-topic / peluditos-cierre-topic"| F
    W -->|"REST: POST /api/leads<br/>PUT /api/leads/{id}/cierre"| S
    F -->|"JDBC"| DBF
    S -->|"JDBC"| DBN

    classDef implementado fill:#d4edda,stroke:#2e7d32,color:#1b3d1f;
    classDef desarrollo fill:#fff3cd,stroke:#b8860b,color:#4d3800;
    classDef actor fill:#e8eef5,stroke:#2f5d8a,color:#12263a;
    class S,W implementado;
    class UI,F,DBF,DBN desarrollo;
    class U actor;
```

**Leyenda:** verde = implementado · amarillo = en desarrollo (Entrega 2) · línea punteada = planificado.

## Componentes

| Componente | Responsabilidad | Tecnología | Estado |
|---|---|---|---|
| Interfaz propia | Registrar leads, bandeja de tareas, seguimiento y cierre | (definida por Belén) | En desarrollo |
| Flowable OSS | Ejecutar el proceso `peluditosLead` (TO-BE v2) y gestionar las tareas | Flowable 7.2 + Tomcat 10 | En desarrollo |
| BD del motor | Persistencia del motor de procesos | PostgreSQL (base `flowable`) | En desarrollo |
| External worker | Tomar los trabajos de las External Worker Tasks, llamar al web service y completar el trabajo | Java 21 | Implementado (Entrega 1); integración con Flowable OSS en desarrollo |
| Web service | Operaciones de negocio sobre leads (registro, consulta, seguimiento, cierre) | Java 21 + Jakarta REST (Jersey) | Implementado y probado en Entrega 1 (en memoria); conexión a BD en desarrollo |
| BD de negocio | Persistencia de leads, seguimientos y clientes | PostgreSQL (base `peluditos`) | En desarrollo |

## Comunicaciones principales

1. La **interfaz** inicia el proceso y completa las tareas de usuario mediante la API REST de Flowable (`/flowable-rest/service/...`).
2. El **worker** consulta a Flowable por trabajos (`/flowable-rest/external-job-api/...`), llama al **web service** y completa el trabajo con el resultado (`leadId`, `registrado`).
3. **Flowable** guarda su estado en la BD del motor; el **web service** guarda los datos de negocio en la BD de negocio.

Detalle de puertos, variables y contratos en [`ACUERDOS-INTEGRACION.md`](../ACUERDOS-INTEGRACION.md).

## Historial

| Entrega | Arquitectura |
|---|---|
| Entrega 1 | Flowable Cloud (Iplacex) + external worker + web service en memoria. La integración de punta a punta quedó pendiente por la falla del Sandbox y la caída de la plataforma (incidente de seguridad informado por el profesor). |
| Entrega 2 | Migración a Flowable OSS local con PostgreSQL, interfaz propia y base de datos de negocio. |
