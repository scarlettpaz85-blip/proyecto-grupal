External Worker – Peluditos Pet Chile
Propósito
Aplicación Java 21 que consulta Flowable OSS 7.2 y ejecuta las tareas BPMN de tipo `external-worker`. El Worker invoca las operaciones REST del componente `Proyecto-WS` y completa o informa fallos de los trabajos en Flowable.
Tópicos atendidos
Tópico	Operación del Web Service	Resultado en Flowable
`peluditos-lead-topic`	`POST /api/leads`	`leadId`, `registrado`
`peluditos-cierre-topic`	`PUT /api/leads/{leadId}/cierre`	`estadoLead`
`peluditos-oportunidades-topic`	`POST /api/oportunidades/generar`	`oportunidades`, `totalOportunidades`
`peluditos-resultado-oportunidad-topic`	`PUT /api/oportunidades/{id}/resultado`	Completa la tarea
Los tópicos están definidos en `BPMN/TO-BE.bpmn` y `BPMN/TO-BE-oportunidades.bpmn`.
Requisitos
Java JDK 21 (comprobar con `java -version` y `javac -version`).
Flowable OSS 7.2 desplegado y accesible desde el servidor donde se ejecutará el Worker.
Web Service iniciado y accesible desde el servidor donde se ejecutará el Worker.
Usuario/contraseña de Flowable o token Bearer con permisos para adquirir, completar y reportar fallos de External Jobs.
Configuración
Copiar `.env.example` a `.env`.
Editar `.env` con las direcciones y credenciales del entorno de destino.
No subir `.env` al repositorio. El `.gitignore` raíz excluye archivos `.env` y permite versionar `.env.example`.
Variables:
Variable	Obligatoria	Descripción
`FLOWABLE_URL`	Sí	URL base de Flowable, por ejemplo `http://IP_VM:8080/flowable-rest`. No agregar `/service`.
`WS_URL`	Sí	URL base del WS, por ejemplo `http://IP_VM:8081/api`.
`FLOWABLE_USER`	Una forma de autenticación	Usuario Basic Auth de Flowable.
`FLOWABLE_PASSWORD`	Una forma de autenticación	Contraseña Basic Auth.
`FLOWABLE_TOKEN`	Una forma de autenticación	Token Bearer alternativo a usuario/contraseña.
`WORKER_ID`	No	Identificador del Worker; por defecto `peluditos-worker`.
El código lee variables de entorno del sistema operativo; no carga `.env` por sí solo. Por eso se deben iniciar mediante los scripts incluidos, que importan el archivo `.env` antes de ejecutar Java.
Ejecución en Linux
Desde esta carpeta:
```bash
chmod +x start-worker.sh
./start-worker.sh
```
Ejecución en Windows PowerShell
Desde esta carpeta:
```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\start-worker.ps1
```
Los scripts compilan `PeluditosApp.java` y luego ejecutan la aplicación. Para detenerla, usar `Ctrl+C`.
Comprobación
Al iniciar, la consola debe mostrar:
La URL configurada de Flowable.
La URL configurada del Web Service.
Los cuatro tópicos anteriores.
Cuando no hay tareas, el Worker continúa consultando Flowable. Para validar integración real, iniciar desde la interfaz un proceso que alcance una External Worker Task y comprobar que el Worker la recibe, invoca el WS y Flowable continúa el proceso.
Reintentos y errores
El código actual informa a Flowable los fallos de procesamiento y configura `retryTimeout` en `PT30S`. La adquisición solicita `numberOfRetries: 3` y bloquea el trabajo por `PT5M`. Revisar los logs de la consola ante errores HTTP o variables BPMN ausentes.
Notas para el despliegue
Si Flowable y el Web Service están en la misma VM, se pueden usar direcciones loopback como `127.0.0.1` para las conexiones internas.
Si alguno está en otra máquina o contenedor, sustituir las direcciones por el hostname/IP accesible desde la VM del Worker.
No abrir los puertos del motor o del WS a Internet sin necesidad; limitar el acceso por firewall/red privada.
La configuración de este componente queda preparada, pero la conectividad y las credenciales deben validarse en la VM de destino.
