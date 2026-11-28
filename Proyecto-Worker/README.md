# External Worker – Peluditos Pet Chile

## Propósito

Aplicación Java 21 que consulta Flowable OSS 7.2 y ejecuta las tareas BPMN de tipo `external-worker`.

El Worker invoca las operaciones REST del componente `Proyecto-WS` y completa o informa fallos de los trabajos en Flowable.

## Tópicos atendidos

| Tópico | Operación del Web Service | Resultado en Flowable |
|---|---|---|
| `peluditos-lead-topic` | `POST /api/leads` | `leadId`, registrado |
| `peluditos-cierre-topic` | `PUT /api/leads/{leadId}/cierre` | `estadoLead` |
| `peluditos-oportunidades-topic` | `POST /api/oportunidades/generar` | `oportunidades`, `totalOportunidades` |
| `peluditos-resultado-oportunidad-topic` | `PUT /api/oportunidades/{id}/resultado` | Completa la tarea |

Los tópicos están definidos en:

- `BPMN/TO-BE.bpmn`
- `BPMN/TO-BE-oportunidades.bpmn`

## Requisitos

- Java JDK 21. Comprobar con `java -version` y `javac -version`.
- Flowable OSS 7.2 desplegado y accesible desde el servidor donde se ejecutará el Worker.
- Web Service iniciado y accesible desde el servidor donde se ejecutará el Worker.
- Usuario y contraseña de Flowable o token Bearer con permisos para adquirir, completar y reportar fallos de External Jobs.

## Configuración

Copiar `.env.example` a `.env` y editar `.env` con las direcciones y credenciales del entorno de destino.

**Importante:** no subir `.env` al repositorio. El archivo `.gitignore` de la raíz excluye los archivos `.env`. Se permite versionar `.env.example`.

### Variables de entorno

| Variable | Obligatoria | Descripción |
|---|---|---|
| `FLOWABLE_URL` | Sí | URL base de Flowable. Por ejemplo: `http://IP_VM:8080/flowable-rest`. No agregar `/service`. |
| `WS_URL` | Sí | URL base del Web Service. Por ejemplo: `http://IP_VM:8081/api`. |
| `FLOWABLE_USER` | Una forma de autenticación | Usuario Basic Auth de Flowable. |
| `FLOWABLE_PASSWORD` | Una forma de autenticación | Contraseña Basic Auth. |
| `FLOWABLE_TOKEN` | Una forma de autenticación | Token Bearer alternativo a usuario y contraseña. |
| `WORKER_ID` | No | Identificador del Worker. Por defecto: `peluditos-worker`. |

El código lee variables de entorno del sistema operativo; no carga `.env` por sí solo. Por eso se deben iniciar mediante los scripts incluidos, que importan el archivo `.env` antes de ejecutar Java.

## Ejecución

### Linux

Desde esta carpeta:

```bash
chmod +x start-worker.sh
./start-worker.sh
```

## Desde PowerShell, ubicado en esta carpeta:

.\start-worker.ps1

Desde CMD, ubicado en esta carpeta:

powershell -ExecutionPolicy Bypass -File .\start-worker.ps1

## Tópicos y funcionamiento

El Worker consulta periódicamente los tópicos configurados en Flowable. Cuando encuentra una tarea externa:

1. Adquiere el trabajo correspondiente.
2. Ejecuta la operación del Web Service.
3. Envía el resultado a Flowable.
4. Completa la tarea o informa el error producido.

Si no existen tareas pendientes, el Worker puede permanecer en silencio mientras continúa consultando Flowable. Esto es normal.

## Validación local

Se realizó una prueba local del recorrido de registro y cierre de un lead:

- Registro de lead de prueba mediante `peluditos-lead-topic`.
- Respuesta del Web Service con `leadId=1008`.
- Cierre del lead mediante `peluditos-cierre-topic`.
- Resultado: lead no convertido, motivo `Precio`.
- Se comprobó la persistencia del registro en SQL Server, en la base de datos `DB_PeluditosPetChile`.

Esta validación corresponde al recorrido local de lead no convertido. No representa una validación del despliegue remoto ni de los tópicos de oportunidades.

## Consideraciones para el despliegue

Las direcciones `127.0.0.1` y `localhost` solo funcionan cuando Flowable y el Web Service están disponibles en el mismo equipo donde se ejecuta el Worker.

Si los servicios están en otra máquina, se deben configurar sus direcciones mediante la IP privada o el DNS correspondiente, de modo que sean accesibles desde el equipo del Worker.

El despliegue remoto debe validarse en el entorno de destino.
