
# Proyecto-WS – Web Services Java

Web Services REST de **Peluditos Pet Chile**, desarrollados con Java 21, Gradle, Jakarta REST, Jersey y servidor Grizzly.

El componente permite gestionar clientes potenciales (leads), seguimientos, clientes, fechas relevantes y oportunidades comerciales, mediante servicios REST conectados a Microsoft SQL Server e integrados con los procesos de negocio de Flowable OSS.

**Responsable del componente:** Christopher Comas Celis  
**Desarrollo de base de datos y servicios:** Jennifer Igor España

---

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
- Tailscale para la comunicación privada entre los equipos del grupo.
- Visual Studio Code como entorno de desarrollo.

## 2. Estructura del componente

```text
Proyecto-WS/
├── BaseDatos-Java/
├── Evidencias-EV-3/
├── Evidencia-EV-4/
├── Evidencias/
│   └── Punta_a_Punta/
├── gradle/
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

La carpeta `BaseDatos-Java` se mantiene como parte de la estructura del proyecto. Su contenido interno debe revisarse para documentar con precisión los archivos y recursos disponibles.

La carpeta `Evidencia-EV-4` corresponde a las evidencias de la cuarta entrega. Se debe conservar la organización real de los archivos existentes en el repositorio.

## 3. Base de datos de negocio

El servicio utiliza Microsoft SQL Server 2017 para almacenar la información relacionada con la gestión comercial de Peluditos Pet Chile.

**Nombre de la base de datos:** `DB_PeluditosPetChile`

La estructura de datos del proyecto contempla seis tablas principales:

1. `Leads`: información de los interesados y su estado comercial.
2. `Seguimientos`: registro de contactos y observaciones.
3. `Clientes`: información de los clientes registrados.
4. `Mascotas`: mascotas asociadas a cada cliente.
5. `FechasRelevantes`: fechas importantes para futuras campañas.
6. `OportunidadesComerciales`: oportunidades comerciales generadas.

Los scripts y recursos relacionados con la base de datos deben consultarse en los archivos disponibles dentro del repositorio.

## 4. Configuración de conexión

La conexión con SQL Server se implementa mediante JDBC en la clase `Database.java`.

La clase obtiene los parámetros de conexión mediante variables de entorno. Esto permite ejecutar el servicio en distintos equipos y servidores sin modificar el código fuente.

### 4.1. Variables de entorno

| Variable | Descripción | Referencia local |
|---|---|---|
| `PELUDITOS_DB_HOST` | Servidor de SQL Server | `localhost` |
| `PELUDITOS_DB_PORT` | Puerto de SQL Server | `1435` |
| `PELUDITOS_DB_NAME` | Nombre de la base de datos | `DB_PeluditosPetChile` |
| `PELUDITOS_DB_USER` | Usuario de SQL Server | `peluditos_java` |
| `PELUDITOS_DB_PASSWORD` | Contraseña del usuario SQL Server | Configurar como secreto |
| `PELUDITOS_WS_PORT` | Puerto de ejecución del servicio REST | `8081` |

### 4.2. Configuración del entorno local

Durante el desarrollo local del equipo se utiliza:

- Servidor SQL Server: `localhost`.
- Puerto SQL Server: `1435`.
- Base de datos: `DB_PeluditosPetChile`.
- Usuario: `peluditos_java`.
- Puerto del Web Service: `8081`.

El puerto `1435` corresponde a la configuración local utilizada durante las pruebas del proyecto.

**Importante:** la clase `Database.java` utiliza `1433` como puerto predeterminado cuando no se configura `PELUDITOS_DB_PORT`. Para ejecutar el servicio en el entorno local del equipo se debe establecer explícitamente el puerto `1435`.

### 4.3. Configuración para Azure

Para el despliegue en Azure no se deben asumir los mismos valores del entorno local.

El responsable del despliegue debe configurar las variables de entorno con los datos reales del servidor donde se encuentre SQL Server:

- `PELUDITOS_DB_HOST`: dirección o nombre del servidor SQL Server.
- `PELUDITOS_DB_PORT`: puerto TCP habilitado para SQL Server.
- `PELUDITOS_DB_NAME`: `DB_PeluditosPetChile`.
- `PELUDITOS_DB_USER`: usuario autorizado para la conexión.
- `PELUDITOS_DB_PASSWORD`: contraseña configurada como secreto.
- `PELUDITOS_WS_PORT`: puerto en el que se ejecutará el Web Service.

El puerto de Azure debe confirmarse en la máquina virtual y en la configuración de red. No se debe asumir que será `1435` ni `1433` sin comprobarlo.

La contraseña real no debe incorporarse a archivos del repositorio, capturas públicas ni documentación compartida.

### 4.4. Archivo de ejemplo

El archivo `.env.example` contiene valores de referencia para configurar el entorno. No debe contener contraseñas reales.

Ejemplo para el entorno local:

```env
PELUDITOS_WS_PORT=8081
PELUDITOS_DB_HOST=localhost
PELUDITOS_DB_PORT=1435
PELUDITOS_DB_NAME=DB_PeluditosPetChile
PELUDITOS_DB_USER=peluditos_java
PELUDITOS_DB_PASSWORD=CAMBIAR_POR_SECRETO_REAL
```

Este archivo sirve como referencia. Las variables deben configurarse en el entorno donde se ejecute el servicio.

## 5. Cómo ejecutar el servicio

### 5.1. Requisitos

Para ejecutar el componente se requiere:

- JDK 21 instalado.
- Gradle Wrapper incluido en el proyecto.
- Microsoft SQL Server 2017.
- SQL Server iniciado.
- Base de datos `DB_PeluditosPetChile` creada.
- Usuario y permisos de acceso configurados.
- Conexión JDBC correctamente configurada.

### 5.2. Configuración en PowerShell

Ejemplo de configuración de variables de entorno para la sesión actual del entorno local:

```powershell
$env:PELUDITOS_DB_HOST="localhost"
$env:PELUDITOS_DB_PORT="1435"
$env:PELUDITOS_DB_NAME="DB_PeluditosPetChile"
$env:PELUDITOS_DB_USER="peluditos_java"
$env:PELUDITOS_DB_PASSWORD="CONTRASEÑA_SQL"
$env:PELUDITOS_WS_PORT="8081"
```

Los valores de usuario y contraseña deben corresponder a las credenciales configuradas en SQL Server.

Para Azure se deben reemplazar los valores locales por los datos reales del servidor de despliegue.

### 5.3. Compilar el proyecto

Desde la carpeta `Proyecto-WS`, ejecutar:

```powershell
.\gradlew.bat build
```

### 5.4. Ejecutar el servicio

Desde la misma carpeta:

```powershell
.\gradlew.bat run
```

Si no se configura `PELUDITOS_WS_PORT`, el servicio utiliza el puerto predeterminado `8081`.

La dirección base del servicio local es:

```text
http://localhost:8081/api
```

Si se configura otro puerto, la dirección base debe ajustarse al puerto utilizado.

La clase `Main.java` configura el servidor para escuchar en las interfaces de red mediante `0.0.0.0`, lo que permite recibir conexiones desde otros equipos cuando la red y las reglas de seguridad lo autorizan.

### 5.5. Comprobar el funcionamiento

Abrir en el navegador:

```text
http://localhost:8081/api/health
```

La respuesta JSON esperada es:

```json
{
  "servicio": "peluditos-ws",
  "estado": "ok"
}
```

Este endpoint comprueba que el Web Service está activo. No reemplaza la comprobación de conexión con SQL Server ni la verificación de Flowable.

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

### 7.1. Registro de leads

**Ruta:** `POST /api/leads`

Recibe los siguientes datos:

- `nombre`
- `telefono`
- `correo`
- `canal`
- `interes`
- `processInstanceId`

El servicio valida los campos obligatorios, registra la información en SQL Server y devuelve el identificador del lead creado.

### 7.2. Consulta de leads

**Ruta:** `GET /api/leads?page=1&size=10`

Permite consultar los leads mediante paginación.

- `page`: número de página.
- `size`: cantidad de registros por página.
- Valores predeterminados: página 1 y tamaño 10.

### 7.3. Consulta por identificador

**Ruta:** `GET /api/leads/{id}`

Permite obtener los datos de un lead específico. Si no existe, devuelve un error HTTP 404.

### 7.4. Registro de seguimientos

**Ruta:** `POST /api/leads/{id}/seguimientos`

Permite registrar información de seguimiento asociada al lead, como medio de contacto, comentario y resultado.

### 7.5. Cierre de leads

**Ruta:** `PUT /api/leads/{id}/cierre`

Permite registrar si el interesado realizó una compra.

Datos recibidos:

- `compro`: valor booleano.
- `motivoNoCompra`: motivo cuando no se concreta la compra.
- `comentarioNoCompra`: comentario adicional opcional.

Los estados utilizados son:

- `Convertido`.
- `No convertido`.

Cuando se registra una compra, el servicio crea el cliente asociado al lead, evitando duplicar clientes del mismo origen.

El campo `acepta_comunicaciones` se mantiene en falso cuando no existe consentimiento explícito.

## 8. Gestión de fechas relevantes y oportunidades

### 8.1. Consultar fechas relevantes

**Ruta:** `GET /api/fechas-relevantes`

Permite consultar las fechas relevantes activas registradas en SQL Server.

### 8.2. Generar oportunidades comerciales

**Ruta:** `POST /api/oportunidades/generar`

Recibe:

- `idFechaRelevante`.
- `processInstanceId`.

El servicio genera oportunidades para los clientes que tienen habilitada la aceptación de comunicaciones.

Además, verifica que no existan oportunidades duplicadas para el mismo cliente y fecha relevante.

### 8.3. Consultar oportunidades

**Ruta:** `GET /api/oportunidades?idFechaRelevante={id}`

Permite consultar las oportunidades comerciales y sus datos asociados.

### 8.4. Registrar resultado de una oportunidad

**Ruta:** `PUT /api/oportunidades/{id}/resultado`

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

Los mensajes de respuesta no deben exponer contraseñas ni información interna de conexión.

## 10. CORS

Se incorpora `CorsFilter.java` para gestionar las solicitudes de origen cruzado (CORS) y permitir la comunicación entre la interfaz web y el servicio REST según la configuración del proyecto.

Para el despliegue, los orígenes permitidos deben corresponder a las direcciones reales de la interfaz web. No se recomienda habilitar orígenes indiscriminadamente en un entorno público.

## 11. Integración con Flowable y External Workers

El Web Service Java dispone de operaciones REST para integrarse con el proceso de negocio gestionado mediante Flowable OSS y External Workers.

### 11.1. Comunicación entre componentes

Para permitir la comunicación entre los computadores de los integrantes se utiliza Tailscale, una red privada que facilita el acceso al Web Service Java.

La dirección `localhost` corresponde al equipo donde se ejecuta el servicio. Para acceder desde otro computador se debe utilizar una dirección de red accesible para ambos integrantes.

El servicio se ejecuta escuchando en las interfaces de red para permitir las conexiones autorizadas desde el equipo del otro integrante.

La dirección base configurada para el Worker debe apuntar al equipo donde se ejecuta el Web Service Java, utilizando la conexión privada de Tailscale.

En el entorno de despliegue, las direcciones de Tailscale o las direcciones locales de desarrollo deben sustituirse por las direcciones de red que correspondan a la infraestructura configurada.

### 11.2. Registro y cierre de leads

El proceso de integración contempla:

1. Flowable gestiona las tareas del proceso de negocio.
2. El External Worker recibe las variables del proceso.
3. El Worker utiliza `POST /api/leads` para registrar interesados.
4. El Web Service Java almacena la información en SQL Server.
5. El Worker utiliza `PUT /api/leads/{id}/cierre` para actualizar el resultado comercial.
6. SQL Server conserva el estado del lead como `Convertido` o `No convertido`.

Durante las pruebas de integración se verificaron registros de leads iniciados desde Flowable y su persistencia en SQL Server.

También se probaron los resultados de cierre:

- Lead convertido: se registró el estado `Convertido` y se creó el cliente asociado.
- Lead no convertido: se registró el estado `No convertido` y el motivo correspondiente.

### 11.3. Campañas y oportunidades

El proceso contempla:

1. Consultar las fechas relevantes activas.
2. Seleccionar la fecha de campaña.
3. Generar oportunidades comerciales.
4. Consultar las oportunidades generadas.
5. Registrar el resultado del contacto comercial.
6. Actualizar el estado de la oportunidad.

Durante la integración con Flowable se realizaron pruebas de generación y registro de resultados de oportunidades comerciales para la campaña de Navidad.

Se verificó que:

- La generación de oportunidades consideró clientes con aceptación de comunicaciones habilitada.
- Las oportunidades generadas quedaron almacenadas en SQL Server.
- Los External Workers de Flowable ejecutaron las operaciones de registro de resultados.
- Se registró una oportunidad con estado `Ganada`.
- Se registró otra oportunidad con estado `Sin compra`.
- Los resultados se comprobaron mediante consultas directas en SQL Server.

### 11.4. Verificación de integración entre Flowable y SQL Server

Como parte de las pruebas conjuntas del equipo se realizaron las siguientes verificaciones:

- Comunicación remota entre los equipos mediante Tailscale.
- Acceso de Scarlett al endpoint de salud del Web Service Java.
- Configuración de la dirección del Web Service en el Worker mediante `WS_URL`.
- Registro de leads desde el flujo de Flowable.
- Persistencia de los leads en SQL Server.
- Cierre de leads con y sin compra.
- Generación de oportunidades para la campaña de Navidad.
- Ejecución de Workers para registrar resultados de oportunidades.
- Comprobación de los estados comerciales directamente en SQL Server.

Estas pruebas permitieron comprobar la comunicación entre Flowable, los External Workers, el Web Service Java y la base de datos.

## 12. Pruebas y evidencias

Las evidencias del componente se organizan en las siguientes carpetas del repositorio:

- `Evidencias-EV-3/`
- `Evidencia-EV-4/`
- `Evidencias/Punta_a_Punta/`
- `BaseDatos-Java/` (contenido pendiente de revisión).

Las evidencias disponibles del proyecto contemplan capturas de conexión con SQL Server, compilación, ejecución del servicio, operaciones REST, integración con Flowable y verificaciones de persistencia.

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
- Comunicación remota mediante Tailscale.
- Registro de leads desde Flowable mediante External Workers.
- Ejecución de Workers para registrar resultados de oportunidades.
- Verificación de los resultados de integración directamente en SQL Server.

### 12.1. Evidencias de integración

Entre las pruebas de integración realizadas se encuentran:

**Registro y cierre de leads**

- Registro de un lead desde Flowable y comprobación de su persistencia en SQL Server.
- Cierre de un lead como `Convertido`, con creación del cliente asociado.
- Cierre de un lead como `No convertido`, con almacenamiento del motivo de no compra.

**Campaña de Navidad**

- Generación de oportunidades comerciales asociadas a la fecha relevante de Navidad.
- Consulta de oportunidades generadas.
- Ejecución de Worker con resultado de interés positivo, registrando el estado `Ganada`.
- Ejecución de Worker con resultado de interés negativo, registrando el estado `Sin compra`.
- Verificación de los resultados almacenados en SQL Server.

Las consultas de verificación permiten revisar los registros almacenados directamente en SQL Server.

## 13. Estado del componente

El componente cuenta con los siguientes avances documentados:

- Conexión JDBC con SQL Server en el entorno de desarrollo original.
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
- Variables de conexión a SQL Server configurables mediante variables de entorno.
- Validaciones y manejo de errores.
- Evidencias de compilación, ejecución y pruebas de persistencia.
- Pruebas de comunicación remota mediante Tailscale.
- Pruebas de integración con Flowable y External Workers para el registro y cierre de leads.
- Pruebas de generación de oportunidades y registro de sus resultados desde Workers.

Las pruebas realizadas permitieron validar la comunicación entre los componentes Java, SQL Server y Flowable para los escenarios documentados.

La validación global de todos los recorridos de punta a punta del proyecto, incluyendo la interfaz, los procesos completos y la gestión de pedidos, corresponde a las pruebas generales de integración del equipo.

## 14. Preparación del entorno y despliegue

### 14.1. Preparación de SQL Server

Para ejecutar el Web Service se necesita que SQL Server esté disponible y que exista la base de datos `DB_PeluditosPetChile`.

Antes de iniciar el servicio se debe comprobar:

- Que SQL Server esté iniciado.
- Que la base de datos exista.
- Que el usuario `peluditos_java` tenga permisos de acceso.
- Que el puerto TCP configurado esté habilitado.
- Que el equipo donde se ejecuta Java pueda alcanzar el servidor SQL Server.
- Que las variables de conexión estén configuradas correctamente.

La instalación de las herramientas de desarrollo en un nuevo equipo no implica que la base de datos original se encuentre recuperada. La reconstrucción o recuperación de la base de datos debe verificarse por separado.

### 14.2. Preparación del Web Service

Desde la carpeta `Proyecto-WS`:

1. Comprobar la instalación de Java 21.
2. Configurar las variables de entorno.
3. Comprobar la conexión con SQL Server.
4. Ejecutar `.\gradlew.bat build`.
5. Ejecutar `.\gradlew.bat run`.
6. Consultar `/api/health`.
7. Probar una operación REST que consulte o registre información en la base de datos.

### 14.3. Preparación para el despliegue en Azure

Para ejecutar el Web Service en Azure se deben configurar los parámetros correspondientes al entorno de destino.

El responsable del despliegue debe confirmar:

- Dirección o nombre del servidor SQL Server.
- Puerto TCP de SQL Server.
- Nombre de la base de datos.
- Usuario de conexión.
- Contraseña almacenada como secreto.
- Puerto de ejecución del Web Service.
- Reglas de red y firewall necesarias.
- Dirección desde la que Flowable y los Workers accederán al Web Service.
- Dirección desde la que la interfaz web accederá al servicio.

Los valores locales `localhost` y `1435` no deben utilizarse automáticamente en Azure. Se deben reemplazar por los valores reales de la infraestructura desplegada.

No se deben publicar credenciales, contraseñas ni archivos `.env` con datos reales en GitHub.

### 14.4. Orden general de inicio

El orden de inicio debe respetar las dependencias entre componentes:

1. Iniciar y comprobar SQL Server.
2. Comprobar que `DB_PeluditosPetChile` esté disponible.
3. Configurar las variables de entorno del Web Service.
4. Iniciar el Web Service Java.
5. Comprobar el endpoint `/api/health`.
6. Comprobar una operación que utilice SQL Server.
7. Iniciar o comprobar Flowable.
8. Iniciar los External Workers.
9. Iniciar o publicar la interfaz web.
10. Ejecutar las pruebas de integración entre los componentes.

El orden definitivo puede ajustarse según la infraestructura y la configuración de despliegue del equipo.

### 14.5. Verificaciones posteriores al despliegue

Después de desplegar el servicio se debe comprobar:

- Que el Web Service responda en su dirección publicada.
- Que `/api/health` devuelva el estado `ok`.
- Que el Web Service pueda conectarse a SQL Server.
- Que las consultas REST funcionen desde la red de despliegue.
- Que los registros nuevos se almacenen en SQL Server.
- Que Flowable pueda comunicarse con los Workers.
- Que los Workers puedan invocar las operaciones del Web Service.
- Que la interfaz web pueda acceder a los servicios necesarios.

La comprobación de `/api/health` por sí sola no demuestra que SQL Server o Flowable estén disponibles.

## 15. Continuidad y mantenimiento

Para continuar trabajando en el componente se consideran las siguientes tareas:

- Revisar el contenido de la carpeta `BaseDatos-Java`.
- Mantener los scripts de creación y preparación de la base de datos.
- Mantener actualizado el archivo `.env.example` con valores de ejemplo, sin secretos.
- Comprobar que la documentación coincida con la implementación de `Database.java`.
- Ejecutar las pruebas de compilación y funcionamiento después de cambios.
- Mantener las evidencias de las pruebas en las carpetas correspondientes.
- Documentar los parámetros utilizados en los distintos entornos.
- Mantener actualizadas las instrucciones de instalación y ejecución.
- Verificar la persistencia de los datos después de reiniciar los componentes.

---

**Proyecto grupal:** Peluditos Pet Chile  
**Repositorio:** https://github.com/scarlettpaz85-blip/proyecto-grupal  
**Componente:** Proyecto-WS – Web Services Java  
**Base de datos:** `DB_PeluditosPetChile`  
**Puerto local SQL Server:** `1435`  
**Puerto local Web Service:** `8081`
