# Pruebas – Entrega 3

**Responsable del documento:** Christopher Comas Celis (coordinación y pruebas)
**Objetivo:** demostrar que lo implementado funciona en distintos escenarios: servicios, integración entre componentes, recorridos de punta a punta de los **dos procesos** y comportamiento ante errores.

Estado de cada prueba: ✅ realizada con evidencia · ⏳ pendiente (prueba integrada del domingo 22-11).

## 1. Configuración externa

| # | Prueba | Resultado esperado | Resultado obtenido | Evidencia | Estado |
|---|---|---|---|---|---|
| C1 | El WS lee la conexión a SQL Server desde variables de entorno | Conecta sin credenciales en el código | Conexión exitosa con `PELUDITOS_DB_*` | [02](../../Proyecto-WS/Evidencias-EV-3/02_Variables_Entorno.png) · [07](../../Proyecto-WS/Evidencias-EV-3/07_Conexion_Java_SQL_Server.png) | ✅ |
| C2 | Cambiar el puerto del WS solo con configuración (`PELUDITOS_WS_PORT=8082`) | El servicio queda activo en el puerto 8082 sin modificar el código | Servicio activo en `http://localhost:8082/api` | [24](../../Proyecto-WS/Evidencias-EV-3/24_Configuracion_Variable_PELUDITOS_WS_PORT_8082.png) · [25](../../Proyecto-WS/Evidencias-EV-3/25_Verificacion_Servicio_Activo_Puerto_8082.png) | ✅ |
| C3 | El worker usa `FLOWABLE_URL` y `WS_URL` | Se conecta a Flowable y al WS indicados | Se probó en la Entrega 2 con el WS en otro equipo (Tailscale) | [`Pruebas/Entrega-2/E2E/`](../Entrega-2/E2E/) | ✅ |
| C4 | La interfaz toma las direcciones de Flowable y del WS desde `.env` | La interfaz funciona cambiando solo el `.env` | – | – | ⏳ |

## 2. Servicios web (pruebas por componente)

Realizadas por Jennifer Igor España con PowerShell (`Invoke-RestMethod`) y verificadas en SQL Server.

### Proceso 1 – Leads

| # | Prueba | Resultado esperado | Resultado obtenido | Evidencia | Estado |
|---|---|---|---|---|---|
| S1 | Registrar un lead (`POST /api/leads`) | 201 y el lead guardado en `dbo.Leads` | Lead registrado y visible en SQL Server | [16](../../Proyecto-WS/Evidencias-EV-3/16%20%E2%80%93%20Registro%20desde%20Java.png) · [17](../../Proyecto-WS/Evidencias-EV-3/17%20%E2%80%93%20Verificaci%C3%B3n%20en%20SQL%20Server.png) | ✅ |
| S2 | Cerrar un lead con compra (`PUT /api/leads/{id}/cierre`, `compro=true`) | Lead "Convertido" **y creado como cliente** | Lead convertido y cliente registrado en `dbo.Clientes` | [18](../../Proyecto-WS/Evidencias-EV-3/18%20%E2%80%93%20Conversi%C3%B3n%20de%20lead%20a%20cliente.png) · [19](../../Proyecto-WS/Evidencias-EV-3/19%20%E2%80%93%20Cliente%20registrado%20en%20SQL%20Server.png) | ✅ |
| S3 | Cerrar un lead sin compra (`compro=false` + motivo) | Lead "No convertido" con motivo; **no** se crea cliente | Lead no convertido; sin cliente asociado | [20](../../Proyecto-WS/Evidencias-EV-3/20%20%E2%80%93%20Registro%20de%20lead%20sin%20compra.png) · [21](../../Proyecto-WS/Evidencias-EV-3/21%20%E2%80%93%20Cierre%20de%20lead%20sin%20compra.png) · [22](../../Proyecto-WS/Evidencias-EV-3/22_Verificacion_Lead_No_Convertido_SQL.png) · [23](../../Proyecto-WS/Evidencias-EV-3/23_Verificacion_Sin_Cliente_SQL.png) | ✅ |

### Proceso 2 – Oportunidades comerciales

| # | Prueba | Resultado esperado | Resultado obtenido | Evidencia | Estado |
|---|---|---|---|---|---|
| S4 | Consultar fechas relevantes (`GET /api/fechas-relevantes`) | Lista de fechas activas | Devuelve las fechas, incluida Navidad | [08](../../Proyecto-WS/Evidencias-EV-3/08_Prueba_Fechas_Relevantes.png) · [09](../../Proyecto-WS/Evidencias-EV-3/09_Fecha_Navidad_SQL.png) · [10](../../Proyecto-WS/Evidencias-EV-3/10_Fecha_Navidad_API.png) | ✅ |
| S5 | Generar oportunidades de una campaña (`POST /api/oportunidades/generar`) | Una oportunidad "Generada" por cliente que acepta comunicaciones | Oportunidades creadas y guardadas en SQL Server | [11](../../Proyecto-WS/Evidencias-EV-3/11_Generacion_Oportunidades_API.png) · [12](../../Proyecto-WS/Evidencias-EV-3/12_Oportunidad_Guardada_SQL.png) | ✅ |
| S6 | Consultar oportunidades (`GET /api/oportunidades`) | Lista con cliente, mascota y estado | Devuelve las oportunidades de la campaña | [13](../../Proyecto-WS/Evidencias-EV-3/13_Consulta_Oportunidades_API.png) | ✅ |
| S7 | Registrar el resultado de un contacto (`PUT /api/oportunidades/{id}/resultado`) | Estado "Ganada" o "Sin compra" con comentario | Resultado actualizado y verificado en SQL Server | [14](../../Proyecto-WS/Evidencias-EV-3/14_Resultado_Oportunidad_PUT.png) · [15](../../Proyecto-WS/Evidencias-EV-3/15_Verificacion_Resultado_SQL.png) | ✅ |

## 3. Pruebas de error

| # | Escenario | Comportamiento esperado | Comportamiento obtenido | Evidencia | Estado |
|---|---|---|---|---|---|
| E1 | Registrar un lead sin nombre | El WS rechaza los datos con un mensaje claro | `400` con `{"error":"El campo nombre es obligatorio"}` | [26](../../Proyecto-WS/Evidencias-EV-3/26_Validacion_Lead_Sin_Nombre.png) | ✅ |
| E2 | Web service apagado | Quien lo llama recibe un error de conexión | "No es posible conectar con el servidor remoto" | [27](../../Proyecto-WS/Evidencias-EV-3/27_Error_Servicio_Web_Apagado.png) | ✅ |
| E3 | SQL Server apagado | El WS no se cae y responde un error controlado | `{"error":"No fue posible registrar el lead"}` | [28](../../Proyecto-WS/Evidencias-EV-3/28_Error_Conexion_SQL_Server_Apagado.png) | ✅ |
| E4 | SQL Server vuelve a encenderse | El WS se recupera sin reiniciarse | Conexión exitosa después de reactivar SQL Server | [29](../../Proyecto-WS/Evidencias-EV-3/29_Verificacion_Recuperacion_SQL_Server_Conexion_Exitosa.png) | ✅ |
| E5 | WS apagado mientras el worker procesa un trabajo | El worker informa el fallo a Flowable (`/fail`) y el trabajo se reintenta al volver el WS | – | – | ⏳ |
| E6 | Datos incompletos en el formulario de la interfaz | La interfaz no deja enviar y muestra qué falta | – | – | ⏳ |
| E7 | Flowable apagado | La interfaz muestra un mensaje entendible en lugar de quedarse en blanco | – | – | ⏳ |
| E8 | Campaña de una fecha sin clientes | El proceso 2 termina en "Campaña sin clientes" | – | – | ⏳ |

## 4. Recorridos de punta a punta (desde la interfaz)

Se ejecutan con todos los componentes encendidos: interfaz → Flowable OSS → worker → WS → SQL Server.

| # | Recorrido | Pasos | Resultado esperado | Evidencia | Estado |
|---|---|---|---|---|---|
| R1 | Proceso 1 – El cliente no compra | Registrar lead → seguimiento "No" → **Registrar motivo de no compra** | Lead "No convertido" con motivo y comentario en SQL Server; el proceso termina | – | ⏳ |
| R2 | Proceso 1 – El cliente compra | Registrar lead → seguimiento "Sí" → Gestionar pedido | Lead "Convertido" y **cliente creado**; el proceso termina | – | ⏳ |
| R3 | Proceso 2 – Campaña | Iniciar campaña "Navidad" → aparece "Contactar cliente" por cada cliente | Oportunidades "Generada" en SQL Server, con `process_instance_id` | – | ⏳ |
| R4 | Proceso 2 – Cliente interesado | Contactar cliente → "Interesado" → Gestionar pedido de campaña | Oportunidad "Ganada" | – | ⏳ |
| R5 | Proceso 2 – Cliente no interesado | Contactar cliente → "No interesado" + comentario | Oportunidad "Sin compra" con comentario | – | ⏳ |
| R6 | Unión de los procesos | El cliente creado en R2 aparece en una campaña nueva | El lead convertido recibe una oportunidad | – | ⏳ |

Las capturas de esta sección se guardarán en esta carpeta con el formato `R1-01-descripcion.png`.

## 5. Observaciones

- Las pruebas de servicios y de error del WS se hicieron con el puerto 8082 para demostrar la configuración externa (C2).
- Pendiente de corrección antes de R1: el worker aún no envía `telefono` ni `correo` al WS (informado a la responsable el 21-11).
