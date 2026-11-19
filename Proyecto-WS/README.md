# Proyecto-WS – Web Services Java

Web services REST de **Peluditos Pet Chile**, desarrollados con Java 21, Gradle, Jakarta REST, Jersey y servidor Grizzly.

**Responsable del componente:** Christopher Comas Celis  
**Desarrollo de base de datos y servicios:** Jennifer Igor España

## 1. Tecnologías utilizadas

- Java 21.
- Gradle.
- Jakarta REST (Jersey 3).
- Servidor Grizzly.
- Microsoft SQL Server 2017.
- SQL Server Management Studio 20.1 (SSMS).
- JDBC para conexión y persistencia de datos.
- Microsoft JDBC Driver for SQL Server 13.2.1.jre11.
- Git y GitHub para control de versiones.

## 2. Estructura del componente

```text
Proyecto-WS/
├── BaseDatos-Java/
│   ├── Evidencias-base-de-datos/
│   ├── Evidencias-Java/
│   ├── Scripts/
│   └── README.md
├── Evidencias-EV-3/
├── src/
│   └── main/
│       └── java/cl/peluditos/ws/
│           ├── ClienteResource.java
│           ├── CorsFilter.java
│           ├── Database.java
│           ├── FechaRelevanteResource.java
│           ├── HealthResource.java
│           ├── Lead.java
│           ├── LeadResource.java
│           ├── Main.java
│           ├── OportunidadResource.java
│           └── SeguimientoResource.java
├── build.gradle.kts
├── gradlew
├── gradlew.bat
└── README.md
```

## 3. Base de datos de negocio

El servicio utiliza Microsoft SQL Server 2017 para almacenar la información del emprendimiento.

**Base de datos:** `DB_PeluditosPetChile`

La base de datos contiene seis tablas:

1. `Leads`: información de los interesados y estado comercial.
2. `Seguimientos`: registro de contactos y observaciones.
3. `Clientes`: información de los clientes registrados.
4. `Mascotas`: mascotas asociadas a cada cliente.
5. `FechasRelevantes`: fechas importantes para futuras campañas.
6. `OportunidadesComerciales`: oportunidades comerciales generadas.

Los scripts de creación, pruebas y consultas se encuentran en `BaseDatos-Java/Scripts/`.

## 4. Configuración de conexión

La conexión JDBC se implementa en `Database.java`.

El proyecto utiliza variables de entorno para configurar la conexión y proteger las credenciales:

- `PELUDITOS_DB_HOST`: servidor de SQL Server.
- `PELUDITOS_DB_PORT`: puerto de SQL Server.
- `PELUDITOS_DB_NAME`: nombre de la base de datos.
- `PELUDITOS_DB_USER`: usuario de SQL Server.
- `PELUDITOS_DB_PASSWORD`: contraseña del usuario de SQL Server.
- `PELUDITOS_WS_PORT`: puerto de ejecución del servicio REST.

No se deben guardar contraseñas ni credenciales personales dentro del repositorio.

## 5. Cómo ejecutar el servicio

### Requisitos

- JDK 21 instalado.
- SQL Server iniciado.
- Base de datos `DB_PeluditosPetChile` creada.
- Variables de entorno de conexión configuradas.
- Gradle Wrapper incluido en el proyecto.

### Configuración en PowerShell

Ejemplo de configuración:

```powershell
$env:PELUDITOS_DB_HOST="localhost"
$env:PELUDITOS_DB_PORT="1433"
$env:PELUDITOS_DB_NAME="DB_PeluditosPetChile"
$env:PELUDITOS_DB_USER="USUARIO_SQL"
$env:PELUDITOS_DB_PASSWORD="CONTRASEÑA_SQL"
$env:PELUDITOS_WS_PORT="8081"
```

Los valores de usuario y contraseña deben corresponder a las credenciales configuradas en SQL Server.

### Compilar el proyecto

Desde la carpeta `Proyecto-WS`:

```powershell
.\gradlew.bat build
```

### Ejecutar el servicio

```powershell
.\gradlew.bat run
```

Si no se configura `PELUDITOS_WS_PORT`, el servicio utiliza el puerto predeterminado `8081`.

La dirección base del servicio es:

`http://localhost:8081/api`

Si se configura el puerto `8082`, la dirección base será:

`http://localhost:8082/api`

### Comprobar el funcionamiento

Abrir en el navegador:

`http://localhost:8081/api/health`

La respuesta esperada es:

`Servicio Peluditos - Estado: OK`

## 6. Servicios REST implementados

| Operación | Método y ruta | Descripción |
|---|---|---|
| Estado del servicio | GET `/api/health` | Comprueba el funcionamiento del servicio. |
| Registrar lead | POST `/api/leads` | Registra un interesado en SQL Server. |
| Listar leads | GET `/api/leads?page=1&size=10` | Consulta leads con paginación. |
| Obtener lead | GET `/api/leads/{id}` | Busca un lead por identificador. |
| Registrar seguimiento | POST `/api/leads/{id}/seguimientos` | Registra un seguimiento asociado a un lead. |
| Cerrar lead | PUT `/api/leads/{id}/cierre` | Actualiza el estado comercial del lead. |
| Registrar cliente | POST `/api/clientes` | Registra un cliente en SQL Server. |
| Consultar fechas relevantes | GET `/api/fechas-relevantes` | Consulta las fechas relevantes activas. |
| Generar oportunidades | POST `/api/oportunidades/generar` | Genera oportunidades para clientes que aceptaron comunicaciones. |
| Consultar oportunidades | GET `/api/oportunidades?idFechaRelevante={id}` | Consulta oportunidades comerciales. |
| Registrar resultado | PUT `/api/oportunidades/{id}/resultado` | Registra el resultado del contacto comercial. |

## 7. Registro y gestión de leads

### 7.1 Registro de leads

Ruta: `POST /api/leads`

Recibe los siguientes datos:

- `nombre`
- `telefono`
- `correo`
- `canal`
- `interes`
- `processInstanceId`

El servicio valida los campos obligatorios, registra la información en SQL Server y devuelve el identificador del lead creado.

### 7.2 Consulta de leads

Ruta: `GET /api/leads?page=1&size=10`

Permite consultar los leads mediante paginación.

- `page`: número de página.
- `size`: cantidad de registros por página.
- Valores predeterminados: página 1 y tamaño 10.

### 7.3 Consulta por identificador

Ruta: `GET /api/leads/{id}`

Permite obtener los datos de un lead específico. Si no existe, devuelve un error 404.

### 7.4 Registro de seguimientos

Ruta: `POST /api/leads/{id}/seguimientos`

Permite registrar información de seguimiento asociada al lead, como medio de contacto, comentario y resultado.

### 7.5 Cierre de leads

Ruta: `PUT /api/leads/{id}/cierre`

Permite registrar si el interesado realizó una compra.

Datos recibidos:

- `compro`: valor booleano.
- `motivoNoCompra`: motivo cuando no se concreta la compra.
- `comentarioNoCompra`: comentario adicional opcional.

Los estados utilizados son `Convertido` y `No convertido`.

Cuando se registra una compra, el servicio crea el cliente asociado al lead, evitando duplicar clientes del mismo origen.

El campo `acepta_comunicaciones` se mantiene en falso cuando no existe consentimiento explícito.

## 8. Gestión de fechas relevantes y oportunidades

### 8.1 Consultar fechas relevantes

Ruta: `GET /api/fechas-relevantes`

Permite consultar las fechas relevantes activas registradas en SQL Server.

### 8.2 Generar oportunidades comerciales

Ruta: `POST /api/oportunidades/generar`

Recibe:

- `idFechaRelevante`
- `processInstanceId`

El servicio genera oportunidades para los clientes que tienen habilitada la aceptación de comunicaciones.

Además, verifica que no existan oportunidades duplicadas para el mismo cliente y fecha relevante.

### 8.3 Consultar oportunidades

Ruta: `GET /api/oportunidades?idFechaRelevante={id}`

Permite consultar las oportunidades comerciales y sus datos asociados.

### 8.4 Registrar resultado de una oportunidad

Ruta: `PUT /api/oportunidades/{id}/resultado`

Recibe:

- `interesado`: valor booleano.
- `comentarioContacto`: comentario del contacto.

Los estados resultantes son:

- `Ganada`.
- `Sin compra`.

## 9. Manejo de errores y validaciones

Los recursos REST incorporan validaciones de datos obligatorios e identificadores.

Se contemplan respuestas HTTP para diferentes situaciones:

| Código HTTP | Descripción |
|---|---|
| 201 | Registro creado correctamente. |
| 200 | Consulta o actualización exitosa. |
| 400 | Solicitud con datos inválidos. |
| 404 | Recurso no encontrado. |
| 500 | Error interno del servidor o base de datos. |
| 503 | Servicio de base de datos no disponible, cuando se identifica un error de conexión. |

Los mensajes de respuesta no exponen contraseñas ni información interna de conexión.

## 10. CORS

Se incorpora `CorsFilter.java` para gestionar las solicitudes de origen cruzado (CORS) y permitir la comunicación entre la interfaz web y el servicio REST según la configuración del proyecto.

## 11. Integración con Flowable y External Worker

El Web Service Java dispone de operaciones REST para integrarse con el proceso de negocio gestionado mediante Flowable OSS y External Workers.

### 11.1 Comunicación entre componentes

Para permitir la comunicación entre los computadores de los integrantes se contempla el uso de Tailscale, una red privada que facilita el acceso al Web Service Java.

La dirección `localhost` corresponde al equipo donde se ejecuta el servicio. Para acceder desde otro computador se debe utilizar una dirección de red accesible para ambos integrantes.

### 11.2 Registro y cierre de leads

El proceso de integración contempla:

1. Flowable gestiona las tareas del proceso de negocio.
2. El External Worker recibe las variables del proceso.
3. El Worker utiliza `POST /api/leads` para registrar interesados.
4. El Web Service Java almacena la información en SQL Server.
5. El Worker utiliza `PUT /api/leads/{id}/cierre` para actualizar el resultado comercial.
6. SQL Server conserva el estado del lead como `Convertido` o `No convertido`.

### 11.3 Campañas y oportunidades

El proceso contempla:

1. Consultar las fechas relevantes activas.
2. Seleccionar la fecha de campaña.
3. Generar oportunidades comerciales.
4. Consultar las oportunidades generadas.
5. Registrar el resultado del contacto comercial.
6. Actualizar el estado de la oportunidad.

La integración completa de estos procesos debe validarse conjuntamente con Flowable y los External Workers.

## 12. Pruebas y evidencias

Las evidencias del componente se encuentran en:

- `BaseDatos-Java/Evidencias-base-de-datos/`
- `BaseDatos-Java/Evidencias-Java/`
- `Evidencias-EV-3/`

Incluyen capturas de creación de tablas, conexión con SQL Server, compilación, ejecución del servicio, operaciones REST y verificaciones de persistencia.

Durante las pruebas se verificaron:

- Conexión JDBC con SQL Server.
- Compilación del proyecto mediante Gradle.
- Ejecución del servicio REST.
- Consulta del estado del servicio mediante `/api/health`.
- Registro de leads mediante `POST /api/leads`.
- Consulta de leads mediante `GET /api/leads`.
- Consulta individual de leads mediante `GET /api/leads/{id}`.
- Cierre de leads con compra y sin compra.
- Creación automática de clientes.
- Generación y consulta de oportunidades.
- Registro de resultados `Ganada` y `Sin compra`.
- Validación de datos obligatorios.
- Prueba de disponibilidad del servicio web.
- Prueba de conexión con SQL Server detenido y recuperación posterior.
- Configuración y comprobación del puerto configurable.

Las consultas de verificación permiten revisar los registros almacenados directamente en SQL Server.

## 13. Estado del componente

El componente cuenta con:

- Conexión JDBC con SQL Server.
- Persistencia y consulta de leads.
- Consulta de leads con paginación.
- Registro de seguimientos.
- Cierre de leads.
- Creación automática de clientes.
- Consulta de fechas relevantes.
- Generación y consulta de oportunidades comerciales.
- Registro de resultados comerciales.
- Configuración CORS.
- Puerto configurable mediante `PELUDITOS_WS_PORT`.
- Validaciones y manejo de errores.
- Evidencias de compilación, ejecución y pruebas de persistencia.

La validación completa del recorrido de punta a punta, incluyendo la interfaz, Flowable, External Workers y gestión de pedidos, corresponde a las pruebas de integración del equipo.
