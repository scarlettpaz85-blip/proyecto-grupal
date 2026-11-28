
# Arquitectura

- Diagrama y estado de cada componente: [`Arquitectura.md`](Arquitectura.md)
- Imagen de componentes: [`modelo-arquitectura.png`](modelo-arquitectura.png)
- Imagen de entornos (desarrollo y destino): [`modelo-despliegue.png`](modelo-despliegue.png)

## Decisiones de arquitectura

| Fecha | Decisión | Motivo |
|---|---|---|
| 20-10-2026 | External worker para invocar servicios desde el proceso | Desacopla Flowable de la lógica de negocio y permite usar cualquier lenguaje |
| 20-10-2026 | Web service Java REST (Jakarta REST + Jersey) | Especificación del curso; simple de consumir desde el worker y la interfaz |
| 27-10-2026 | Flowable OSS 7.2 local en lugar de Flowable Cloud | Requisito de la Entrega 2; la nube presentó fallas con external workers y luego quedó fuera de servicio por un incidente de seguridad |
| 27-10-2026 | PostgreSQL para la base de datos del motor de Flowable | Base relacional compatible con Flowable 7.2 |
| 03-11-2026 | Microsoft SQL Server 2017 para la base de datos de negocio | La responsable ya construyó el esquema (6 tablas) en SQL Server; el documento de la Entrega 2 lo admite y permite usar tecnologías distintas para el motor y el negocio. Se accede desde el web service mediante JDBC (driver mssql-jdbc) |
| 03-11-2026 | Interfaz propia en TypeScript + React + Vite | Reemplaza los formularios de Flowable Enterprise; TypeScript ayuda a respetar las variables del proceso y el proxy de Vite evita configurar CORS |
| 17-11-2026 | Segundo proceso (`peluditosOportunidad`) separado del proceso de leads | Son dos procesos de negocio distintos (clientes potenciales vs. clientes existentes); se conectan mediante los datos: el lead convertido se registra como cliente |
| 17-11-2026 | Subproceso multi-instancia secuencial para contactar a cada cliente de una campaña | Una tarea por cliente en la bandeja, sin crear un proceso por cliente; secuencial para evitar conflictos de variables |
| 17-11-2026 | Configuración por variables de entorno con archivos `.env.example` | Requisito de la Entrega 3: cambiar de entorno sin modificar el código y sin subir contraseñas |
| 17-11-2026 | Entorno de destino propuesto: un solo servidor con proxy web | Simplifica la instalación para el emprendimiento; reemplaza el proxy de Vite, que solo existe en desarrollo |

## Seguimiento de arquitectura – Entrega 4

Durante la Entrega 4 se continúa trabajando sobre la arquitectura definida en las entregas anteriores, manteniendo la separación entre la interfaz, el motor de procesos, el External Worker, el Web Service y las bases de datos.

El objetivo de esta etapa es verificar y documentar la integración de los componentes, la persistencia de los datos y la preparación del despliegue en un entorno remoto.

### 1. Configuración del entorno de desarrollo

Se registran los siguientes puertos utilizados en el entorno local de desarrollo:

| Componente | Puerto | Estado |
|---|---:|---|
| Web Service Java | `8081` | Confirmado para el entorno de desarrollo |
| SQL Server | `1435` | Confirmado para el entorno de desarrollo |

Estos valores corresponden al entorno de desarrollo informado por la responsable del Web Service y la base de datos. No deben considerarse automáticamente como los puertos definitivos del entorno de despliegue.

La configuración de conexión se realiza mediante variables de entorno, para permitir adaptar los componentes a distintos entornos sin modificar el código.

### 2. Integración entre componentes

La arquitectura mantiene las siguientes responsabilidades:

- La interfaz propia permite a la emprendedora interactuar con los procesos y consultar información del negocio.
- Flowable OSS administra la ejecución de los procesos BPMN y sus tareas.
- El External Worker adquiere los trabajos de los tópicos configurados, ejecuta las operaciones correspondientes y comunica sus resultados a Flowable.
- El Web Service Java proporciona las operaciones REST para gestionar la información del negocio.
- PostgreSQL almacena la información interna del motor Flowable.
- SQL Server almacena la información comercial de Peluditos Pet Chile.

### 3. Persistencia de la información

Se mantienen dos bases de datos con responsabilidades diferenciadas:

- PostgreSQL: persistencia de la información interna de Flowable.
- SQL Server: persistencia de los datos de negocio de Peluditos Pet Chile.

La verificación de persistencia después de reiniciar los componentes debe quedar respaldada por las pruebas y evidencias de la Entrega 4.

### 4. Preparación del despliegue

El equipo contempla Azure como entorno de destino. La configuración definitiva debe documentarse una vez que se encuentre disponible y verificada la infraestructura.

Los siguientes datos quedan pendientes de confirmación:

| Elemento | Información pendiente |
|---|---|
| Servidor de despliegue | Dirección IP o nombre DNS definitivo |
| Flowable OSS | Dirección y puerto de acceso |
| PostgreSQL | Servidor, puerto y base de datos |
| Web Service Java | Dirección y puerto de acceso |
| SQL Server | Servidor, puerto y base de datos |
| External Worker | Variables de conexión a Flowable y al Web Service |
| Interfaz propia | Dirección de acceso y configuración del proxy web |
| Seguridad | Configuración de credenciales y variables secretas |

No se deben publicar contraseñas, tokens ni archivos `.env` reales en el repositorio.

### 5. Verificaciones de integración EV4

Las pruebas de integración deben permitir comprobar:

- La comunicación entre Flowable y el External Worker.
- La comunicación entre el Worker y el Web Service Java.
- La comunicación entre el Web Service y SQL Server.
- La ejecución de los procesos BPMN desde la interfaz propia.
- La persistencia de los datos de negocio.
- La continuidad de la información después de reiniciar los componentes.
- La disponibilidad de los servicios mediante sus mecanismos de comprobación.

Las pruebas realizadas y sus resultados deben documentarse en la carpeta de evidencias correspondiente. No se considera completada una verificación hasta contar con su resultado y respaldo.

### 6. Relación con los documentos de Entrega 4

Este documento se complementa con los siguientes recursos del repositorio:

- `Arquitectura.md`: diagramas y descripción de los componentes y entornos.
- `modelo-arquitectura.png`: representación gráfica de los componentes.
- `modelo-despliegue.png`: representación de los entornos de desarrollo y destino.
- `ACUERDOS-INTEGRACION.md`: acuerdos técnicos y contratos de integración.
- `Docs/Manual-Tecnico.md`: instrucciones de configuración, instalación y ejecución.
- `Pruebas/`: pruebas y evidencias de integración.

La arquitectura y sus diagramas deben mantenerse sincronizados con la configuración y el funcionamiento real de la solución.
