# Acuerdos de integración – Entrega 3

> Documento vivo. Todos los componentes deben respetar estos acuerdos para que la solución funcione de punta a punta.
> Responsable: Christopher Comas Celis (coordinación). Cualquier cambio se acuerda en reunión y se registra en el acta.
> **Novedades de la Entrega 3:** segundo proceso (oportunidades comerciales), tarea propia para el motivo de no compra y configuración externa (sin `localhost` ni contraseñas en el código).

## 1. Componentes, responsables y configuración

| Componente | Carpeta | Tecnología | Valor por defecto (desarrollo) | Responsable |
|---|---|---|---|---|
| Interfaz propia | `Proyecto-UI/` | TypeScript + React + Vite | `http://localhost:3000` | Eunices Belén Trujillo |
| Motor de procesos | – | Flowable OSS 7.2 (Tomcat 10 + flowable-rest) | `http://localhost:8080/flowable-rest` | Scarlett Paz Araya |
| BD del motor | – | PostgreSQL | `localhost:5432` / base `flowable` | Scarlett Paz Araya |
| External worker | `Proyecto-Worker/` | Java 21 | (sin puerto, consulta a Flowable) | Scarlett Paz Araya |
| Web service | `Proyecto-WS/` | Java 21 + Jersey | `http://localhost:8081/api` | Jennifer Igor España |
| BD de negocio | `Proyecto-WS/BaseDatos-Java/` | Microsoft SQL Server 2017 | `localhost:1433` / base `DB_PeluditosPetChile` | Jennifer Igor España |
| BPMN, arquitectura, actas, pruebas, manuales | `BPMN/`, `Arquitectura/`, `Actas/`, `Pruebas/`, `Docs/` | – | – | Christopher Comas Celis |

### 1.1 Parámetros de configuración (variables de entorno)

Los valores de la tabla anterior son **solo valores por defecto**. Cada componente debe leerlos de variables de entorno (o de un `.env` no versionado) y traer un archivo de ejemplo **sin contraseñas reales** (`.env.example`).

| Componente | Variable | Ejemplo | Estado |
|---|---|---|---|
| Worker | `FLOWABLE_URL` | `http://localhost:8080/flowable-rest` | ✅ Implementado |
| Worker | `WS_URL` | `http://localhost:8081/api` | ⚠️ Hoy apunta a `/api/leads`; cambiar a la base `/api` para usar también `/api/oportunidades` |
| Worker | `FLOWABLE_USER` / `FLOWABLE_PASSWORD` (o `FLOWABLE_TOKEN`) | `rest-admin` / `********` | ✅ Implementado |
| WS | `PELUDITOS_WS_PORT` | `8081` | Pendiente (Jennifer) |
| WS | `PELUDITOS_DB_HOST` / `PELUDITOS_DB_PORT` | `localhost` / `1433` | Pendiente (Jennifer) |
| WS | `PELUDITOS_DB_NAME` / `PELUDITOS_DB_USER` | `DB_PeluditosPetChile` / `peluditos_java` | Pendiente (Jennifer) |
| WS | `PELUDITOS_DB_PASSWORD` | `********` | ✅ Implementado |
| Interfaz | `VITE_FLOWABLE_TARGET` | `http://localhost:8080` | Pendiente (Belén) |
| Interfaz | `VITE_WS_TARGET` | `http://localhost:8081` | Pendiente (Belén) |

**Nunca subir contraseñas reales ni tokens al repositorio.** Los `.env` reales van en `.gitignore`.

## 2. Procesos BPMN

| Proceso | Clave (`processDefinitionKey`) | Archivo | Imagen |
|---|---|---|---|
| 1. Gestión de leads (v3) | `peluditosLead` | `BPMN/TO-BE.bpmn` | `BPMN/TO-BE.png` |
| 2. Oportunidades comerciales | `peluditosOportunidad` | `BPMN/TO-BE-oportunidades.bpmn` | `BPMN/TO-BE-oportunidades.png` |

### 2.1 Proceso 1 – Gestión de leads (v3)

| Orden | Actividad (id) | Tipo | Quién la ejecuta |
|---|---|---|---|
| 1 | `registrarLead` – Registrar y clasificar lead | User task | Interfaz (datos del lead y `servicioRequerido`) |
| 2 | `procesarWS` – Registrar lead en el sistema | External worker (`peluditos-lead-topic`) | Worker → `POST /api/leads` → devuelve `leadId` |
| 3 | `realizarSeguimiento` – Realizar seguimiento | User task | Interfaz: **solo `compro`** (sin valor por defecto) |
| 4 | `gwCompro` – ¿Compró? | Exclusive gateway | `${compro == true}` → Sí; por defecto → No |
| 5a | `cerrarConvertido` – Registrar lead convertido | External worker (`peluditos-cierre-topic`) | Worker → `PUT /api/leads/{leadId}/cierre` con `compro=true` |
| 6a | `gestionarPedido` – Gestionar pedido | User task | Interfaz (nota opcional) |
| 5b | `registrarMotivo` – Registrar motivo de no compra | User task | Interfaz: `motivoNoCompra` (obligatorio) y `comentarioNoCompra` |
| 6b | `cerrarNoConvertido` – Registrar lead no convertido | External worker (`peluditos-cierre-topic`) | Worker → `PUT /api/leads/{leadId}/cierre` con `compro=false` y el motivo |

**Cambios respecto de la Entrega 2** (pedido de la profesora): el motivo ya no se pide en "Realizar seguimiento", sino en su propia tarea en la rama "No". El worker **no cambia**: las dos tareas de cierre usan el mismo tópico. La interfaz debe mostrar la nueva tarea `registrarMotivo`.

### 2.2 Proceso 2 – Oportunidades comerciales

| Orden | Actividad (id) | Tipo | Quién la ejecuta |
|---|---|---|---|
| 1 | `inicioCampana` – Campaña iniciada | Start event | Interfaz: inicia el proceso con `idFechaRelevante` y `nombreCampana` |
| 2 | `generarOportunidades` – Generar oportunidades de la campaña | External worker (`peluditos-oportunidades-topic`) | Worker → `POST /api/oportunidades/generar` → completa con `oportunidades` (JSON) y `totalOportunidades` |
| 3 | `gwHayClientes` – ¿Hay clientes para contactar? | Exclusive gateway | `${totalOportunidades > 0}`; si no → *Campaña sin clientes* |
| 4 | `contactarClientes` – Contactar a cada cliente | Subproceso **multi-instancia secuencial** sobre `${oportunidades}` | Una vuelta por cliente; variable de la vuelta: `oportunidad` |
| 4.1 | `contactarCliente` – Contactar cliente | User task | Interfaz: muestra `oportunidad` y completa con `interesado` y `comentarioContacto` |
| 4.2 | `registrarResultado` – Registrar resultado del contacto | External worker (`peluditos-resultado-oportunidad-topic`) | Worker → `PUT /api/oportunidades/{oportunidad.idOportunidad}/resultado` |
| 4.3 | `gwInteresado` – ¿Interesado? | Exclusive gateway | `${interesado == true}` → Gestionar pedido de campaña; si no → *Sin compra* |
| 4.4 | `gestionarPedidoCampana` – Gestionar pedido de campaña | User task | Interfaz (nota opcional) |

> **A verificar al desplegar (Scarlett):** el subproceso recorre la variable `oportunidades` de tipo JSON. Si Flowable OSS 7.2 no acepta el JSON como colección, la alternativa es que el worker la envíe como lista serializable; avisar en el grupo antes de cambiar el modelo.

## 3. Variables de los procesos (nombres EXACTOS)

### Proceso 1 – `peluditosLead`

| Variable | Tipo | La define | La usa |
|---|---|---|---|
| `nombre`, `telefono`, `correo` | string | Interfaz | Worker → WS (**los tres deben enviarse al WS**) |
| `canal` | string (`instagram`, `whatsapp`, `facebook`, `tiktok`, `web`, `telefono`) | Interfaz | WS |
| `interes` | string | Interfaz | WS |
| `servicioRequerido` | string (`reposteria`, `banos`, `repuestos`) | Interfaz | Gestionar pedido |
| `leadId` | integer | Worker (respuesta del WS) | Interfaz, cierre |
| `registrado` | boolean | Worker | – |
| `compro` | boolean | Interfaz (seguimiento) | Gateway ¿Compró?, cierre |
| `motivoNoCompra` | string (`Precio`, `Fecha no disponible`, `Sin respuesta`, `Compró en otro lugar`, `Otro`) | Interfaz (registrarMotivo) | Worker → WS |
| `comentarioNoCompra` | string (opcional) | Interfaz (registrarMotivo) | Worker → WS |
| `notaPedido` | string (opcional) | Interfaz (gestionarPedido) | – |

El worker envía además el `processInstanceId` al WS para guardar la trazabilidad en la columna `process_instance_id`.

### Proceso 2 – `peluditosOportunidad`

| Variable | Tipo | La define | La usa |
|---|---|---|---|
| `idFechaRelevante` | integer | Interfaz (al iniciar) | Worker → WS |
| `nombreCampana` | string | Interfaz (al iniciar) | Bandeja (para mostrar) |
| `oportunidades` | JSON (lista) | Worker (respuesta del WS) | Subproceso multi-instancia |
| `totalOportunidades` | integer | Worker | Gateway ¿Hay clientes? |
| `oportunidad` | JSON (un elemento de la lista) | Motor (en cada vuelta) | Interfaz, worker |
| `interesado` | boolean | Interfaz (contactarCliente) | Gateway ¿Interesado?, worker |
| `comentarioContacto` | string (opcional) | Interfaz (contactarCliente) | Worker → WS |
| `notaPedido` | string (opcional) | Interfaz (gestionarPedidoCampana) | – |

Formato de cada elemento de `oportunidades` (lo devuelve el WS):

```json
{ "idOportunidad": 12, "idCliente": 3, "cliente": "Camila Rojas", "telefono": "+569...", "mascota": "Luna", "fechaRelevante": "Navidad" }
```

## 4. Contratos REST del web service

| Método y ruta | Entrada (JSON) | Salida | Estado |
|---|---|---|---|
| `GET /api/health` | – | `{estado}` | ✅ Implementado |
| `POST /api/leads` | `nombre, telefono, correo, canal, interes, processInstanceId` | lead con `id` (201) / 400 si falta `nombre` | ✅ (falta recibir `telefono`, `correo` y `processInstanceId` desde el worker) |
| `GET /api/leads?page=&size=` | – | lista paginada | ✅ Implementado |
| `GET /api/leads/{id}` | – | lead (200) / 404 | ✅ Implementado |
| `PUT /api/leads/{id}/cierre` | `compro, motivoNoCompra, comentarioNoCompra` | lead actualizado (200) | ✅ Implementado (agregar `comentarioNoCompra`). **Si `compro=true`, crear el cliente** en `Clientes` con `lead_origen_id`, para que pueda recibir campañas |
| `POST /api/clientes` | datos del cliente | cliente (201) | ✅ Implementado |
| `GET /api/fechas-relevantes` | – | lista de fechas activas (`idFechaRelevante, nombre, tipo, fecha`) | **Nuevo** (para elegir la campaña en la interfaz) |
| `POST /api/oportunidades/generar` | `idFechaRelevante, processInstanceId` | `{ total, oportunidades: [...] }` (201). Crea una fila por cliente con `acepta_comunicaciones = 1`, estado `Generada` | **Nuevo** |
| `GET /api/oportunidades?idFechaRelevante=` | – | lista con cliente, mascota y estado | **Nuevo** (consulta desde la interfaz) |
| `PUT /api/oportunidades/{id}/resultado` | `interesado, comentarioContacto` | oportunidad actualizada (200): estado `Ganada` si `interesado`, `Sin compra` si no | **Nuevo** |

Errores: JSON `{"error": "mensaje"}` con 400 (datos inválidos) / 404 (no existe) / 500 (error de base de datos).
**CORS:** no es necesario configurarlo; la interfaz usa el proxy de Vite (`/flowable-rest` → Flowable, `/api` → WS), con los destinos tomados de `.env`.

## 5. Llamadas de la interfaz a Flowable OSS

| Acción | Llamada |
|---|---|
| Iniciar proceso de leads | `POST /flowable-rest/service/runtime/process-instances` con `processDefinitionKey: peluditosLead` y variables |
| Iniciar campaña | `POST /flowable-rest/service/runtime/process-instances` con `processDefinitionKey: peluditosOportunidad`, `idFechaRelevante` y `nombreCampana` |
| Bandeja de tareas | `GET /flowable-rest/service/runtime/tasks?processDefinitionKey=peluditosLead` y `...=peluditosOportunidad` |
| Ver variables de una tarea | `GET /flowable-rest/service/runtime/tasks/{id}/variables` (incluye `oportunidad` en el proceso 2) |
| Completar tarea | `POST /flowable-rest/service/runtime/tasks/{id}` con `{"action":"complete","variables":[...]}` |

## 6. Recorridos de prueba de punta a punta

**Proceso 1 – leads** (como en la Entrega 2, con la tarea nueva):
1. A – No compra: registrar lead → seguimiento "No" → **Registrar motivo** → lead "No convertido" con motivo y comentario en SQL Server.
2. B – Compra: registrar lead → seguimiento "Sí" → lead "Convertido" **y creado como cliente** → Gestionar pedido.

**Proceso 2 – oportunidades:**
3. C – Campaña: elegir "Navidad" en la interfaz → el worker genera las oportunidades → en la bandeja aparece "Contactar cliente" por cada cliente.
4. Cliente 1: "Interesado" → oportunidad `Ganada` → Gestionar pedido de campaña.
5. Cliente 2: "No interesado" con comentario → oportunidad `Sin compra`.
6. Consultar en la interfaz (o en SQL Server) el estado de las oportunidades de la campaña.
7. D – Campaña sin clientes: fecha sin clientes que acepten comunicaciones → el proceso termina en *Campaña sin clientes*.

**Pruebas de error** (las documenta Christopher en `Pruebas/Entrega-3/`): datos inválidos en la interfaz, WS apagado (el worker reporta el fallo a Flowable), SQL Server apagado (el WS responde 500), Flowable apagado (la interfaz muestra un mensaje claro).

## 7. Fechas internas

| Fecha | Hito |
|---|---|
| Miércoles 18-nov | Contrato acordado (este documento) |
| Jueves 19-nov | WS del proceso 2 y BPMN desplegados |
| Viernes 20-nov | Interfaz y worker conectados al proceso 2 |
| Sábado 21-nov | Reunión: prueba de punta a punta de los 2 procesos + pruebas de error |
| Domingo 22-nov | Manuales, arquitectura y videos |
| Lunes 23-nov | Revisión final; cada integrante sube el link al aula |
