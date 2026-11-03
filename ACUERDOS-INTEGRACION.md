# Acuerdos de integración – Entrega 2

> Documento vivo. Todos los componentes deben respetar estos acuerdos para que la solución funcione de punta a punta.
> Responsable: Christopher Comas Celis (coordinación). Cualquier cambio se acuerda en reunión y se registra en el acta.

## 1. Componentes, responsables y puertos

| Componente | Carpeta | Tecnología | Puerto / URL | Responsable |
|---|---|---|---|---|
| Interfaz propia | `Proyecto-UI/` | (a definir por Belén) | `http://localhost:3000` (o el que use) | Belén (Eunices) Trujillo |
| Motor de procesos | – | Flowable OSS 7.2 (Tomcat 10 + flowable-rest) | `http://localhost:8080/flowable-rest` | Scarlett Paz Araya |
| BD del motor | – | PostgreSQL | `localhost:5432` / base `flowable` | Scarlett Paz Araya |
| External worker | `Proyecto-Worker/` | Java 21 | (sin puerto, consulta a Flowable) | Scarlett Paz Araya |
| Web service | `Proyecto-WS/` | Java 21 + Jersey | `http://localhost:8081/api` | Jennifer Igor España |
| BD de negocio | `Proyecto-WS/sql/` | PostgreSQL | `localhost:5432` / base `peluditos` | Jennifer Igor España |
| BPMN, arquitectura, actas, pruebas E2E | `BPMN/`, `Arquitectura/`, `Actas/`, `Pruebas/` | – | – | Christopher Comas Celis |

Credenciales de Flowable OSS (solo desarrollo local): `rest-admin` / `test`.
**Nunca subir contraseñas reales ni tokens al repositorio.**

## 2. Proceso BPMN

- **Clave del proceso (processDefinitionKey):** `peluditosLead`
- **Archivo:** `BPMN/TO-BE.bpmn` (versión OSS, sin formularios de Enterprise)

| Orden | Actividad (id) | Tipo | Quién la ejecuta |
|---|---|---|---|
| 1 | `registrarLead` – Registrar y clasificar lead | User task | Interfaz (completa la tarea con los datos del lead y `servicioRequerido`) |
| 2 | `procesarWS` – Registrar lead en el sistema | External worker task (topic `peluditos-lead-topic`) | Worker → `POST /api/leads` → devuelve `leadId` |
| 3 | `realizarSeguimiento` – Realizar seguimiento | User task | Interfaz (completa con `compro` y `motivoNoCompra`) |
| 4 | `cerrarLead` – Registrar cierre del lead | External worker task (topic `peluditos-cierre-topic`) | Worker → `PUT /api/leads/{leadId}/cierre` |
| 5 | `gwCompro` – ¿Compró? | Exclusive gateway | `${compro == true}` → Gestionar pedido; si no → Lead no convertido |
| 6 | `gestionarPedido` – Gestionar pedido | User task | Interfaz |

## 3. Variables del proceso (nombres EXACTOS)

| Variable | Tipo | La define | La usa |
|---|---|---|---|
| `nombre` | string | Interfaz (al iniciar o registrar) | Worker, WS |
| `telefono` | string | Interfaz | WS |
| `correo` | string | Interfaz | WS |
| `canal` | string (`instagram`, `whatsapp`, `facebook`, `tiktok`, `web`, `telefono`) | Interfaz | WS, reportes |
| `interes` | string (producto consultado) | Interfaz | WS |
| `servicioRequerido` | string (`reposteria`, `banos`, `repuestos`) | Interfaz | Gateway del TO-BE |
| `leadId` | integer | Worker (respuesta del WS) | Interfaz, cierre |
| `registrado` | boolean | Worker | – |
| `compro` | boolean | Interfaz (seguimiento) | Gateway ¿Compró? |
| `motivoNoCompra` | string | Interfaz | WS (cierre) |

## 4. Contratos REST del web service

| Método y ruta | Entrada (JSON) | Salida | Estado |
|---|---|---|---|
| `GET /api/health` | – | `{estado}` | Implementado |
| `POST /api/leads` | `nombre, telefono, correo, canal, interes` | lead con `id` (201) | Implementado (migrar a BD) |
| `GET /api/leads?page=&size=` | – | lista paginada | Migrar a BD |
| `GET /api/leads/{id}` | – | lead (200) / 404 | Migrar a BD |
| `POST /api/leads/{id}/seguimientos` | `fecha, medio, comentario` | seguimiento (201) | Pendiente |
| `PUT /api/leads/{id}/cierre` | `compro, motivoNoCompra` | lead actualizado (200) | Pendiente |
| `POST /api/clientes` | datos del cliente y mascota | cliente (201) | Pendiente |

Errores: JSON `{"error": "mensaje"}` con 400 / 404 / 500.
**CORS:** el WS y Flowable deben permitir el origen de la interfaz.

## 5. Llamadas de la interfaz a Flowable OSS

| Acción | Llamada |
|---|---|
| Iniciar proceso | `POST /flowable-rest/service/runtime/process-instances` con `processDefinitionKey: peluditosLead` y variables |
| Bandeja de tareas | `GET /flowable-rest/service/runtime/tasks?processDefinitionKey=peluditosLead` |
| Ver variables de una tarea | `GET /flowable-rest/service/runtime/tasks/{id}/variables` |
| Completar tarea | `POST /flowable-rest/service/runtime/tasks/{id}` con `{"action":"complete","variables":[...]}` |

## 6. Recorrido de prueba de punta a punta (para la evidencia)

1. La interfaz registra un lead → se inicia una instancia en Flowable.
2. La interfaz completa "Registrar y Clasificar Lead".
3. El worker toma el trabajo → llama `POST /api/leads` → el lead queda en la BD de negocio → devuelve `leadId`.
4. La interfaz muestra la tarea "Realizar seguimiento" en la bandeja y la completa con `compro`.
5. El proceso termina por el camino correcto; el lead queda cerrado en la BD.
6. Se reinicia el WS y el lead sigue existiendo (prueba de persistencia).

Evidencias en `Pruebas/E2E/` (capturas de cada paso).

## 7. Fechas internas

| Fecha | Hito |
|---|---|
| Martes 3-nov | Cada componente funciona por separado |
| Miércoles 4 – jueves 5-nov | Integración completa (recorrido de la sección 6) |
| Viernes 6 – sábado 7-nov | Grabación de videos |
| Domingo 8-nov | Revisión final del repositorio |
| Lunes 9-nov 23:59 | Entrega (cada uno sube el link al aula) |
