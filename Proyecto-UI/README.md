# Proyecto-UI

Interfaz para anotar interesados, iniciar campañas de oportunidades, ver la bandeja de Flowable (ambos procesos) y consultar leads / oportunidades en el web service.

**Stack:** TypeScript + React + Vite (puerto 3000)  
**Responsable:** Eunices Belén Trujillo  
**Origen de ejecución (acordado):** `http://localhost:3000`

En desarrollo, Vite hace de proxy a Flowable (`:8080`) y al WS (`:8081`). En producción las URLs absolutas se completan en `.env.production` solo cuando el equipo las informe.

## Ejecutar

```bash
cd Proyecto-UI
npm install
npm run dev
```

http://localhost:3000

### Precondiciones

1. Flowable OSS con los BPMN desplegados: `peluditosLead` (`BPMN/TO-BE.bpmn`) y `peluditosOportunidad` (`BPMN/TO-BE-oportunidades.bpmn`).
2. External worker escuchando los 4 tópicos.
3. Web service en `:8081` con fechas relevantes activas (para campañas) y SQL Server disponible.

## Secciones

| Vista | Qué hace |
|---|---|
| **Anotar** | Inicia `peluditosLead` y completa `registrarLead` |
| **Campañas** | Lista fechas relevantes del WS, inicia `peluditosOportunidad` y consulta oportunidades |
| **Bandeja** | Tareas de ambos procesos (seguimiento, motivo, contactar, pedidos) |
| **Lista** | Leads persistidos en el WS |

## Endpoints usados

| Nombre | Método / ruta | Descripción |
|---|---|---|
| Iniciar lead | `POST /flowable-rest/service/runtime/process-instances` | key `peluditosLead` + variables |
| Iniciar campaña | `POST /flowable-rest/service/runtime/process-instances` | key `peluditosOportunidad` + `idFechaRelevante`, `nombreCampana` |
| Bandeja leads | `GET .../runtime/tasks?processDefinitionKey=peluditosLead` | tareas pendientes |
| Bandeja campañas | `GET .../runtime/tasks?processDefinitionKey=peluditosOportunidad` | tareas pendientes |
| Variables | `GET .../runtime/tasks/{id}/variables` | incluye `oportunidad` en el proceso 2 |
| Completar | `POST .../runtime/tasks/{id}` | `action: complete` |
| Health WS | `GET /api/health` | estado del WS |
| Fechas | `GET /api/fechas-relevantes` | fechas activas para elegir campaña |
| Oportunidades | `GET /api/oportunidades?idFechaRelevante=` | estado en SQL Server |
| Listar leads | `GET /api/leads` | lista del WS |

Credenciales Flowable (local): `rest-admin` / `test`

## Tareas de la bandeja (coherencia BPMN)

**Proceso 1 – leads (v3)**

- `realizarSeguimiento` → solo variable `compro`
- `registrarMotivo` → `motivoNoCompra` (obligatorio) y `comentarioNoCompra`
- `gestionarPedido` → `notaPedido` opcional

**Proceso 2 – oportunidades**

- `contactarCliente` → muestra `oportunidad`; completa `interesado` y `comentarioContacto`
- `gestionarPedidoCampana` → `notaPedido` opcional

Detalle de contratos en [`ACUERDOS-INTEGRACION.md`](../ACUERDOS-INTEGRACION.md).
