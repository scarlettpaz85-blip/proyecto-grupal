# Proyecto-UI

Interfaz para anotar interesados, ver la bandeja de Flowable y completar seguimiento / pedido.

**Stack:** TypeScript + React + Vite (puerto 3000)  
**Responsable:** Eunices Belén Trujillo

Elegimos este stack porque el resto del grupo va en Java (WS/Worker) y acá hacía falta algo de front para formularios y bandeja. TypeScript ayuda con las variables del proceso; Vite deja el proxy a Flowable (`:8080`) y al WS (`:8081`) sin pelear con CORS.

## Ejecutar

```bash
cd Proyecto-UI
npm install
npm run dev
```

http://localhost:3000

Hace falta Flowable con el proceso `peluditosLead`. El WS en `:8081` es opcional (pestaña Lista).

## Endpoints

| Nombre | Método / topic | Descripción |
|---|---|---|
| Iniciar proceso | `POST /flowable-rest/service/runtime/process-instances` | key `peluditosLead` + variables |
| Bandeja | `GET /flowable-rest/service/runtime/tasks?processDefinitionKey=peluditosLead` | tareas pendientes |
| Variables | `GET /flowable-rest/service/runtime/tasks/{id}/variables` | datos de la tarea |
| Completar | `POST /flowable-rest/service/runtime/tasks/{id}` | `action: complete` |
| Health WS | `GET /api/health` | estado del WS |
| Listar leads | `GET /api/leads` | lista del WS |

Credenciales Flowable (local): `rest-admin` / `test`
