# Integración de Plataformas (INP) – Servicio Disciplinar

> Proyecto grupal para el emprendimiento: **Peluditos Pet Chile** (pastelería para mascotas)
> Docente: [Nombre] · Bimestre: [X-2026]

## Integrantes

| Nombre | Usuario GitHub | Rol actual (semana N) |
|---|---|---|
| scarlett paz| scarlettpaz85-blip| Analista de procesos |
| Jennifer igor| | Modelador BPMN |
| | | Integrador (Web Services) |
| christopher comas|christopher1-01 | Implementador (Flowable) / QA |

> Los roles rotan semanalmente. El historial de la rotación queda en las actas.

## Problema y solución

**Problema (AS-IS):** Peluditos Pet Chile recibe consultas por Instagram, Facebook, TikTok y WhatsApp, pero no las registra ni les hace seguimiento formal. Por eso no sabe cuántos interesados terminan comprando, y pierde oportunidades de recompra en fechas relevantes (cumpleaños de mascotas, Navidad, Día del Perro, etc.).

**Solución (TO-BE / MVP):** dos procesos ejecutados en Flowable:
1. **Gestión de leads:** registrar a cada interesado, hacerle seguimiento con recordatorios y cerrarlo como convertido o no convertido.
2. **Oportunidades comerciales:** generar automáticamente tareas de recontacto cuando se acerca una fecha relevante de un cliente o una campaña.

Detalle completo en [`Analisis/ANALISIS-CASO.md`](Analisis/ANALISIS-CASO.md).

## Arquitectura

![Arquitectura](Arquitectura/modelo-arquitectura.png)

| Componente | Carpeta | Tecnología | Puerto |
|---|---|---|---|
| Motor de procesos | – | Flowable REST OSS 7.2 sobre Tomcat 10 | 8080 |
| Servicios web | `Proyecto-WS/` | Java 21 + Jakarta REST/SOAP | 8081 |
| External Worker(s) | `Proyecto-Worker/` | Java 21 + Spring Boot | – |
| Interfaz de usuario | `Proyecto-UI/` | [PHP / JS / otro] | 8000 |
| Base de datos | – | [PostgreSQL / MySQL / H2] | [ ] |

## Estructura del repositorio

```
├── Actas/            # Una acta .md por reunión (Acta-AAAA-MM-DD.md)
├── Analisis/         # Análisis del caso, requerimientos y supuestos
├── BPMN/             # AS-IS.bpmn, TO-BE.bpmn y exportaciones .png
├── Arquitectura/     # Diagramas de arquitectura
├── Prompts/          # Registro de prompts de IA utilizados
├── Pruebas/          # Archivos .http y evidencias de pruebas
├── Proyecto-WS/      # Servicios web Java
├── Proyecto-Worker/  # External Workers de Flowable
├── Proyecto-UI/      # Interfaz web (bandeja de tareas)
└── Videos/           # Enlaces a videos por entrega
```

## Cómo ejecutar

### 1. Flowable REST
1. Descargar Flowable OSS 7.2: https://github.com/flowable/flowable-engine/releases
2. Copiar `flowable-rest.war` en `TOMCAT/webapps/` e iniciar Tomcat 10.
3. Verificar: http://localhost:8080/flowable-rest/docs (usuario `rest-admin`, clave `test`).
4. Desplegar `BPMN/TO-BE.bpmn` (ver `Pruebas/flowable.http`).

### 2. Servicios web
```bash
cd Proyecto-WS && ./gradlew run
```

### 3. External Worker
```bash
cd Proyecto-Worker && ./gradlew bootRun
```

### 4. Interfaz
```bash
cd Proyecto-UI && [comando]
```
## Proceso TO-BE en Flowable Cloud

Enlace al proceso: https://iplacex.cloud.flowable.com/design/#/workspace/20264B/apps/primeraEntrega/editor/bpmn/peluditosPetChileTOBECompaneros

## Entregas

| Entrega | Semana | Estado | Videos |
|---|---|---|---|
| Entregable 1 – BPMN + proceso mínimo | 3 | ⬜ | [Videos/Entrega-1](Videos/Entrega-1) |
| Entregable 2 – Integración WS | 5 | ⬜ | [Videos/Entrega-2](Videos/Entrega-2) |
| Entregable 3 – Flujo casi completo + UI | 7 | ⬜ | [Videos/Entrega-3](Videos/Entrega-3) |
| Examen / Entrega final | 8–9 | ⬜ | [Videos/Entrega-4](Videos/Entrega-4) |

## Uso de IA
Los prompts utilizados se registran en [`Prompts/`](Prompts). Cada integrante debe poder explicar el código generado.
