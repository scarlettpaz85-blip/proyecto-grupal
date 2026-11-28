# Integración Flowable OSS 7.2 y External Worker

## 1. Objetivo

Documentar la configuración e integración entre Flowable OSS 7.2 y el External Worker del proyecto Peluditos Pet Chile.

El External Worker está desarrollado en Java 21 y permite ejecutar tareas BPMN mediante tópicos externos, comunicándose con el Web Service del proyecto.

## 2. Componentes de la integración

- **Flowable OSS 7.2:** motor de procesos BPMN.
- **External Worker:** aplicación Java 21 que consulta y ejecuta trabajos externos.
- **Proyecto-WS:** servicio REST que recibe las operaciones del Worker.
- **SQL Server:** base de datos utilizada para persistir la información del proyecto.

## 3. Tópicos atendidos por el Worker

| Tópico | Operación |
|---|---|
| `peluditos-lead-topic` | Registro de leads |
| `peluditos-cierre-topic` | Cierre de leads |
| `peluditos-oportunidades-topic` | Generación de oportunidades |
| `peluditos-resultado-oportunidad-topic` | Registro del resultado de oportunidades |

## 4. Configuración de conexión

El Worker utiliza variables de entorno para conectarse a Flowable y al Web Service.

| Variable | Descripción |
|---|---|
| `FLOWABLE_URL` | URL base de Flowable REST |
| `WS_URL` | URL base del Web Service |
| `FLOWABLE_USER` | Usuario de autenticación de Flowable |
| `FLOWABLE_PASSWORD` | Contraseña de autenticación |
| `FLOWABLE_TOKEN` | Alternativa de autenticación mediante Bearer |
| `WORKER_ID` | Identificador del Worker |

Las credenciales reales deben configurarse en el entorno de ejecución y no deben publicarse en GitHub.

## 5. Ejecución del External Worker

Se requiere Java JDK 21.

En Windows PowerShell:

```powershell
.\start-worker.ps1
```

En Linux:

```bash
chmod +x start-worker.sh
./start-worker.sh
```

Los scripts cargan las variables del archivo `.env` antes de ejecutar la aplicación Java.

## 6. Validación de integración local

Se realizó una prueba local del proceso de registro y cierre de un lead.

Resultados observados:

1. El Worker recibió una tarea del tópico `peluditos-lead-topic`.
2. El Web Service registró el lead y devolvió el identificador `1008`.
3. El Worker completó la tarea en Flowable.
4. Se ejecutó el cierre mediante `peluditos-cierre-topic`.
5. El resultado fue un lead no convertido, con motivo `Precio`.
6. Se verificó la persistencia del registro en SQL Server, en la base de datos `DB_PeluditosPetChile`.

Esta prueba valida el recorrido local de registro y cierre de un lead no convertido.

## 7. Estado y consideraciones para el despliegue

La integración descrita fue comprobada en el entorno local.

El despliegue remoto debe validarse en la máquina virtual de destino, comprobando:

- Acceso del Worker a Flowable OSS.
- Acceso del Worker al Web Service.
- Configuración de las variables de entorno.
- Autenticación y permisos para adquirir, completar y reportar fallos de trabajos externos.

Las direcciones `localhost` y `127.0.0.1` solo son válidas cuando los servicios están disponibles en el mismo equipo donde se ejecuta el Worker. Para otros equipos deben configurarse las direcciones de red correspondientes.

## 8. Archivos relacionados

La implementación del Worker y sus instrucciones de ejecución se encuentran en la carpeta:

`Proyecto-Worker/`

Archivos principales:

- `PeluditosApp.java`
- `.env.example`
- `start-worker.ps1`
- `start-worker.sh`
- `README.md`

