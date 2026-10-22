# Proyecto-WS – Web services Java

Web services REST de Peluditos Pet Chile (Java 21, Gradle, Jakarta REST con Jersey y servidor Grizzly).

**Responsable:** Christopher Comas Celis

## Cómo ejecutar
Requisito: JDK 21 instalado (`java -version`).

```bash
# Windows
gradlew.bat run
# Mac / Linux
./gradlew run
```

El servicio queda activo en `http://localhost:8081/api`. Verificar en el navegador: http://localhost:8081/api/health

Pruebas: abrir `pruebas-ws.http` en VS Code (extensión REST Client) o usar curl:

```bash
curl http://localhost:8081/api/health
curl -X POST http://localhost:8081/api/leads -H "Content-Type: application/json" -d "{\"nombre\":\"Maria\",\"canal\":\"instagram\",\"interes\":\"Torta\"}"
curl http://localhost:8081/api/leads
```

## Integración con el external worker
El worker (topic `peluditos-lead-topic`) debe:
1. Leer las variables del proceso `nombre`, `canal` e `interes` (opcional: `telefono`, `correo`).
2. Invocar `POST http://localhost:8081/api/leads` con esas variables en JSON.
3. Devolver a Flowable las variables `leadId` (campo `id` de la respuesta) y `registrado` (`true`) para que el proceso continúe.

## Lista inicial de web services

| # | Operación | Método y ruta | Entrada | Salida | Actividad del proceso | Estado |
|---|---|---|---|---|---|---|
| 1 | Estado del servicio | `GET /api/health` | – | `{estado}` | Verificación técnica | ✅ Implementado |
| 2 | Registrar lead | `POST /api/leads` | nombre, teléfono, correo, canal, interés | lead con `id`, `estado`, `registrado` | P1 – Registrar lead | ✅ Implementado (memoria) |
| 3 | Listar leads | `GET /api/leads` | – | lista de leads | Bandeja / reportes | ✅ Implementado (memoria) |
| 4 | Obtener lead | `GET /api/leads/{id}` | id | lead | P1 – Realizar seguimiento | ✅ Implementado (memoria) |
| 5 | Registrar seguimiento | `POST /api/leads/{id}/seguimientos` | fecha, medio, comentario, resultado | seguimiento registrado | P1 – Realizar seguimiento | Planificado |
| 6 | Cerrar lead | `PUT /api/leads/{id}/cierre` | convertido (bool), motivo | lead actualizado | P1 – Gateway ¿Compró? | Planificado |
| 7 | Crear cliente y mascota | `POST /api/clientes` | datos del cliente y de su(s) mascota(s), consentimiento | cliente con `id` | P1 – Lead convertido | Planificado |
| 8 | Administrar fechas relevantes | `GET/POST /api/fechas` | nombre, tipo, fecha, días de anticipación | fecha registrada | P2 – Administrar campañas | Planificado |
| 9 | Oportunidades próximas | `GET /api/oportunidades/proximas?dias=7` | días | clientes con fecha cercana | P2 – Timer diario | Planificado |
| 10 | Enviar notificación | `POST /api/notificaciones/correo` | destinatario, asunto, mensaje | resultado del envío | P2 – Avisar a la emprendedora (servicio externo de correo) | Planificado |

Los datos se guardan **en memoria** (se pierden al reiniciar). En las próximas entregas se conectará una base de datos.
