# Integración de Plataformas (INP) – Servicio Disciplinar

## Proyecto: Peluditos Pet Chile

Proyecto grupal para el emprendimiento **Peluditos Pet Chile**, dedicado a la repostería para mascotas y a la venta de baños ecológicos y repuestos.

**Docente:** Santiago Neira · **Bimestre:** 4 – 2026

## Equipo

| Integrante | Usuario GitHub | Rol Entrega 1 | Rol Entrega 2 |
|---|---|---|---|
| Christopher Comas Celis | `christopher-comas` | Modelo AS-IS y web service Java | Coordinación, BPMN y pruebas de punta a punta |
| Jennifer Igor España | `JenniferGor` | Modelo TO-BE y Flowable | Base de datos de negocio y web services |
| Scarlett Paz Araya | `scarlettpaz85-blip` | Coordinación y análisis | Flowable OSS y external worker |
| Eunices Belén Trujillo | `Eunices-TMEB` | Video para la emprendedora | Interfaz propia completa |

> Los roles rotan por entrega. El detalle de la asignación queda registrado en las [actas](Actas).

## Problema y solución

**Problema (AS-IS):** Peluditos Pet Chile recibe consultas por redes sociales, teléfono y tienda web, pero no las registra ni les hace seguimiento formal. No sabe cuántos interesados terminan comprando y pierde oportunidades de recompra en fechas relevantes (cumpleaños de mascotas, Navidad, Día del Perro).

**Solución (TO-BE):** un sistema de gestión de interesados (*leads*) basado en un proceso BPMN ejecutado en Flowable, que permite:
1. **Gestión de leads:** registrar a cada interesado, hacer seguimiento y registrar si compró o no (con el motivo).
2. **Oportunidades comerciales** (próximas entregas): generar tareas de recontacto en fechas relevantes.

El MVP considera registro manual de interesados, sin integración automática con redes sociales. Detalle en [`Analisis/ANALISIS-CASO.md`](Analisis/ANALISIS-CASO.md).

## Documentos clave

- [`ACUERDOS-INTEGRACION.md`](ACUERDOS-INTEGRACION.md): puertos, variables del proceso, contratos REST y recorrido de prueba que todos los componentes deben respetar.
- [`BPMN/`](BPMN): modelos AS-IS y TO-BE.
- [`Arquitectura/`](Arquitectura): diagrama de arquitectura.

## Arquitectura

[Ver diagrama de arquitectura](Arquitectura/Arquitectura.md)

| Componente | Carpeta | Tecnología | Estado Entrega 2 |
|---|---|---|---|
| Interfaz propia | `Proyecto-UI/` | TypeScript + React + Vite | En desarrollo (pantallas de registro, bandeja y lista subidas) |
| Motor de procesos | – | Flowable OSS 7.2 (Tomcat 10 + flowable-rest) | En desarrollo |
| BD del motor | – | PostgreSQL | En desarrollo |
| External worker | `Proyecto-Worker/` | Java 21 | Implementado; integración con Flowable OSS en desarrollo |
| Web service | `Proyecto-WS/` | Java 21 + Jakarta REST (Jersey) | Implementado y probado (Entrega 1); conexión a BD en desarrollo |
| BD de negocio | `Proyecto-WS/sql/` | Microsoft SQL Server 2017 | En desarrollo (6 tablas creadas) |

> Esta tabla se actualiza a medida que cada componente se integra y prueba.

## Estructura del repositorio

```text
├── ACUERDOS-INTEGRACION.md  # Reglas de integración entre componentes
├── Actas/                   # Actas de reuniones (Markdown)
├── Analisis/                # Análisis del caso, requerimientos y supuestos
├── Arquitectura/            # Diagrama de arquitectura
├── BPMN/                    # Modelos AS-IS y TO-BE
├── Prompts/                 # Registro del uso de IA
├── Pruebas/                 # Pruebas y evidencias de ejecución
├── Proyecto-UI/             # Interfaz propia
├── Proyecto-WS/             # Web service Java REST
├── Proyecto-Worker/         # External worker de Flowable
└── Videos/                  # Enlaces a los videos de cada entrega
```

## Cómo ejecutar (entorno local)

Orden de arranque:
1. **Bases de datos:** PostgreSQL con la base `flowable` (motor de Flowable) y SQL Server 2017 con la base `peluditos` (negocio; script en `Proyecto-WS/sql/`).
2. **Flowable OSS:** Tomcat 10 con `flowable-rest`, en `http://localhost:8080/flowable-rest` (credenciales de desarrollo `rest-admin` / `test`). Desplegar `BPMN/TO-BE.bpmn`.
3. **Web service:** `cd Proyecto-WS` → `gradlew.bat run` → `http://localhost:8081/api/health`.
4. **External worker:** ver [`Proyecto-Worker/README.md`](Proyecto-Worker/README.md).
5. **Interfaz:** `cd Proyecto-UI` → `npm install` → `npm run dev` → `http://localhost:3000`. Ver [`Proyecto-UI/README.md`](Proyecto-UI/README.md).

Las instrucciones detalladas de cada componente están en el README de su carpeta.

## Entregas

| Entrega | Fecha | Estado | Videos |
|---|---|---|---|
| Entrega 1 – Diagnóstico, propuesta y prototipo | 26-10-2026 | Enviada | [Videos/Entrega-1](Videos/Entrega-1) |
| Entrega 2 – Flowable OSS, interfaz propia y persistencia | 09-11-2026 | En desarrollo | [Videos/Entrega-2](Videos/Entrega-2) |
| Entrega 3 | – | Pendiente | [Videos/Entrega-3](Videos/Entrega-3) |
| Entrega 4 – Final (examen) | – | Pendiente | [Videos/Entrega-4](Videos/Entrega-4) |

## Uso de IA

Los prompts utilizados se registran en [`Prompts/`](Prompts). Cada integrante debe poder explicar el trabajo y el código que presenta.
