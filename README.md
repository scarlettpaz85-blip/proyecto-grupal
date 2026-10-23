# Integración de Plataformas (INP) – Servicio Disciplinar

## Proyecto: Peluditos Pet Chile

Proyecto grupal desarrollado para el emprendimiento Peluditos Pet Chile, una pastelería especializada en productos para mascotas.

**Docente:** [Santiago Neira]  
**Bimestre:** 4– 2026  

| Nombre | Usuario GitHub | Rol actual (semana N) |
|---|---|---|
| scarlett paz| scarlettpaz85-blip| Analista de procesos y coordinadora |
| Jennifer igor|JenniferGor |Modeladora BPMN TO-BE e integradora Flowable|
| christopher comas|christopher-comas |Analista BPMN AS-IS y desarrollador de Web Services Java  |> Los roles rotan semanalmente. El historial de la rotación queda en las actas.
|Belen Eunices| Eunices-TMEB|
## Problema y solución

**Problema (AS-IS):** Peluditos Pet Chile recibe consultas por Instagram, Facebook, TikTok y WhatsApp, pero no cuenta con un registro centralizado ni un seguimiento formal de los interesados. Esto dificulta conocer cuántas consultas se convierten en compras y aprovechar oportunidades comerciales en fechas relevantes, como cumpleaños de mascotas, Navidad y el Día del Perro.

**Solución propuesta (TO-BE / MVP):** Desarrollar un sistema de gestión de interesados (*leads*) mediante procesos BPMN en Flowable, que permita:

1. **Gestión de leads:** registrar a cada interesado, realizar seguimiento de sus consultas y registrar si se convierte o no en cliente.
2. **Oportunidades comerciales:** identificar fechas relevantes para los clientes y generar tareas de seguimiento o recontacto.

El MVP contempla el registro manual de interesados, sin integración automática con redes sociales.

**Estado del proyecto:** La solución se encuentra en desarrollo. Las funcionalidades se implementarán y validarán progresivamente durante las entregas.



Detalle completo en [`Analisis/ANALISIS-CASO.md`](Analisis/ANALISIS-CASO.md).

## Arquitectura

[Ver diagrama de arquitectura](Arquitectura/Arquitectura.md)

| Componente | Carpeta | Tecnología | Estado |
|---|---|---|---|
| Motor de procesos | `BPMN/` | Flowable Work | Prototipo en desarrollo |
| Servicios web | `Proyecto-WS/` | Java REST | Prueba local evidenciada |
| External Worker | `Proyecto-Worker/` | Java | Integración pendiente |
| Interfaz de usuario | `Proyecto-UI/` | Por definir | Pendiente |
| Base de datos | Por definir | Por definir | Pendiente |



##estructura

├── Actas/            # Actas de reuniones y acuerdos del equipo
├── Analisis/          # Análisis del caso, requerimientos y supuestos
├── BPMN/              # Modelos BPMN AS-IS y TO-BE y sus exportaciones
├── Arquitectura/      # Diagrama de arquitectura y estado de integración
├── Prompts/           # Registro de prompts de IA utilizados
├── Pruebas/           # Archivos de prueba y evidencias de ejecución
├── Proyecto-WS/       # Código del Web Service Java REST
├── Proyecto-Worker/   # Código del External Worker de Flowable
├── Proyecto-UI/       # Interfaz web y bandeja de tareas (si se implementa)
└── Videos/             # Videos de presentación por entrega


```

## Cómo ejecutar

### 1. Flowable Work
El prototipo BPMN se desarrolla en Flowable Work. Las instrucciones de despliegue y ejecución se actualizarán cuando se confirme el entorno definitivo.

### 2. Servicios web
El proyecto Java REST se encuentra en `Proyecto-WS/`.
Christopher compartió evidencia de una prueba local del servicio de registro de leads.

### 3. External Worker
El worker se encuentra en `Proyecto-Worker/`.
Su conexión con Flowable Work y la integración con el Web Service están pendientes de completar y validar.

### 4. Interfaz
La interfaz de usuario se encuentra pendiente de definición e implementación.


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
