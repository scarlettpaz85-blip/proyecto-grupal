# Integración de Plataformas (INP) – Servicio Disciplinar

## Proyecto: Peluditos Pet Chile

Proyecto grupal para el emprendimiento **Peluditos Pet Chile**, dedicado a la repostería para mascotas y a la venta de baños ecológicos y repuestos.

**Docente:** Santiago Neira · **Bimestre:** 4 – 2026

## Equipo

| Integrante | Usuario GitHub | Rol Entrega 1 | Rol Entregas 2 y 3 |
|---|---|---|---|
| Christopher Comas Celis | `christopher-comas` | Modelo AS-IS y web service Java | Coordinación, BPMN, arquitectura, pruebas y manuales |
| Jennifer Igor España | `JenniferGor` | Modelo TO-BE y Flowable | Base de datos de negocio y web services |
| Scarlett Paz Araya | `scarlettpaz85-blip` | Coordinación y análisis | Flowable OSS y external worker |
| Eunices Belén Trujillo | `Eunices-TMEB` | Video para la emprendedora | Interfaz propia |

> En la Entrega 3 el equipo decidió mantener los roles de la Entrega 2 por el plazo de una semana y la cercanía de la Entrega 4 (ver [acta del 17-11](Actas/Acta-2026-11-17.md)). El detalle de las tareas queda registrado en las [actas](Actas).

## Problema y solución

**Problema (AS-IS):** Peluditos Pet Chile recibe consultas por redes sociales, teléfono y tienda web, pero no las registra ni les hace seguimiento formal. No sabe cuántos interesados terminan comprando y pierde oportunidades de recompra en fechas relevantes (cumpleaños de mascotas, Navidad, Día del Perro).

**Solución (TO-BE):** un sistema basado en **dos procesos BPMN** ejecutados en Flowable OSS, con una interfaz propia para la emprendedora:
1. **Gestión de leads** (`peluditosLead`): registrar a cada interesado, hacer seguimiento, registrar si compró y, si no, el motivo. El lead que compra queda registrado como cliente.
2. **Oportunidades comerciales** (`peluditosOportunidad`): iniciar una campaña en una fecha relevante (por ejemplo, Navidad), generar una oportunidad por cada cliente que acepta comunicaciones, contactarlo y registrar el resultado (ganada o sin compra).

El MVP considera registro manual de interesados, sin integración automática con redes sociales. Detalle en [`Analisis/ANALISIS-CASO.md`](Analisis/ANALISIS-CASO.md).

## Documentos clave

- [`ACUERDOS-INTEGRACION.md`](ACUERDOS-INTEGRACION.md): configuración, variables de los procesos, tópicos y contratos REST que todos los componentes deben respetar.
- [`BPMN/`](BPMN): modelos AS-IS y TO-BE de los dos procesos, con imágenes.
- [`Arquitectura/`](Arquitectura): diagramas de componentes y de entornos (desarrollo y destino).
- [`Docs/Manual-Usuario.md`](Docs/Manual-Usuario.md): manual para la emprendedora.
- [`Docs/Manual-Tecnico.md`](Docs/Manual-Tecnico.md): instalación, configuración y mantenimiento.
- [`Pruebas/Entrega-3/`](Pruebas/Entrega-3): pruebas de servicios, errores y recorridos de punta a punta.

## Arquitectura

[Ver diagrama de arquitectura](Arquitectura/Arquitectura.md)

| Componente | Carpeta | Tecnología | Estado Entrega 3 |
|---|---|---|---|
| Interfaz propia | `Proyecto-UI/` | TypeScript + React + Vite | Implementada para los dos procesos (registro, bandeja, campañas, lista) |
| Motor de procesos | – | Flowable OSS 7.2 (Tomcat 10 + flowable-rest) | Los dos procesos desplegados y ejecutados |
| BD del motor | – | PostgreSQL | Implementada |
| External worker | `Proyecto-Worker/` | Java 21 | Atiende los 4 tópicos; informa fallos a Flowable y se reintenta |
| Web service | `Proyecto-WS/` | Java 21 + Jakarta REST (Jersey) | Endpoints de leads, clientes, fechas relevantes y oportunidades, con validaciones |
| BD de negocio | `Proyecto-WS/BaseDatos-Java/` | Microsoft SQL Server 2017 | Implementada (base `DB_PeluditosPetChile`) |

> Recorridos de punta a punta de los dos procesos y pruebas de error con evidencias en [`Pruebas/Entrega-3/`](Pruebas/Entrega-3). Entrega 2: [`Pruebas/Entrega-2/E2E/`](Pruebas/Entrega-2/E2E).

## Estructura del repositorio

```text
├── ACUERDOS-INTEGRACION.md  # Reglas de integración entre componentes
├── Actas/                   # Actas de reuniones (Markdown)
├── Analisis/                # Análisis del caso, requerimientos y supuestos
├── Arquitectura/            # Diagramas de componentes y entornos
├── BPMN/                    # Modelos AS-IS y TO-BE (dos procesos)
├── Docs/                    # Manual de usuario y manual técnico
├── Prompts/                 # Registro del uso de IA
├── Pruebas/                 # Pruebas y evidencias de ejecución
├── Proyecto-UI/             # Interfaz propia
├── Proyecto-WS/             # Web service Java REST
├── Proyecto-Worker/         # External worker de Flowable
└── Videos/                  # Enlaces a los videos de cada entrega
```

## Cómo ejecutar (entorno local)

La guía completa (requisitos, configuración y problemas frecuentes) está en el [manual técnico](Docs/Manual-Tecnico.md). Resumen del orden de arranque:
1. **Bases de datos:** PostgreSQL con la base `flowable` (motor de Flowable) y SQL Server 2017 con la base `DB_PeluditosPetChile` (negocio; scripts en `Proyecto-WS/BaseDatos-Java/Scripts/`).
2. **Flowable OSS:** Tomcat 10 con `flowable-rest`, en `http://localhost:8080/flowable-rest` (credenciales de desarrollo `rest-admin` / `test`). Desplegar `BPMN/TO-BE.bpmn` y `BPMN/TO-BE-oportunidades.bpmn`.
3. **Web service:** `cd Proyecto-WS` → `gradlew.bat run` → `http://localhost:8081/api/health`.
4. **External worker:** ver [`Proyecto-Worker/README.md`](Proyecto-Worker/README.md).
5. **Interfaz:** `cd Proyecto-UI` → `npm install` → `npm run dev` → `http://localhost:3000`. Ver [`Proyecto-UI/README.md`](Proyecto-UI/README.md).

Las instrucciones detalladas de cada componente están en el README de su carpeta.

## Entregas

| Entrega | Fecha | Estado | Videos |
|---|---|---|---|
| Entrega 1 – Diagnóstico, propuesta y prototipo | 26-10-2026 | Enviada | [Videos/Entrega-1](Videos/Entrega-1) |
| Entrega 2 – Flowable OSS, interfaz propia y persistencia | 09-11-2026 | Enviada | [Videos/Entrega-2](Videos/Entrega-2) |
| Entrega 3 – Dos procesos integrados, configuración externa y documentación | 23-11-2026 | Enviada | [Videos/Entrega-3](Videos/Entrega-3) |
| Entrega 4 – Final (examen) | – | Pendiente | [Videos/Entrega-4](Videos/Entrega-4) |

## Uso de IA

Los prompts utilizados se registran en [`Prompts/`](Prompts). Cada integrante debe poder explicar el trabajo y el código que presenta.
