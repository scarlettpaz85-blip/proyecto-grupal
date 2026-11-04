# # Proyecto-WS – Web Services Java

Web services REST de **Peluditos Pet Chile**, desarrollados con Java 21, Gradle, Jakarta REST, Jersey y servidor Grizzly.

**Responsable del componente:** Christopher Comas Celis  
**Desarrollo de base de datos y servicios:** Jennifer Igor España

## 1. Tecnologías utilizadas

- Java 21.
- Gradle.
- Jakarta REST (Jersey 3).
- Servidor Grizzly.
- Microsoft SQL Server 2017.
- JDBC para conexión y persistencia de datos.
- Git y GitHub para control de versiones.

## 2. Estructura del componente

```text
Proyecto-WS/
├── BaseDatos-Java/
│   ├── Evidencias-base-de-datos/
│   ├── Evidencias-Java/
│   ├── Scripts/
│   │   ├── 01_Creacion_DB_PeluditosPetChile.sql
│   │   ├── 02_Inserciones_Prueba.sql
│   │   └── 03_Consultas_Verificacion.sql
│   └── README.md
├── src/
│   └── main/
│       └── java/cl/peluditos/ws/
│           ├── ClienteResource.java
│           ├── CorsFilter.java
│           ├── Database.java
│           ├── HealthResource.java
│           ├── Lead.java
│           ├── LeadResource.java
│           ├── Main.java
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

La configuración utiliza una variable de entorno para proteger la contraseña:

`PELUDITOS_DB_PASSWORD`

Antes de ejecutar el servicio, se debe configurar esa variable en el sistema operativo con la contraseña del usuario de SQL Server utilizado por el proyecto.

No se deben guardar contraseñas ni credenciales personales dentro del repositorio.

La conexión utiliza SQL Server mediante JDBC y permite que los servicios REST consulten y registren información en la base de datos.

## 5. Cómo ejecutar el servicio

**Requisitos:**

- JDK 21 instalado.
- SQL Server iniciado.
- Base de datos `DB_PeluditosPetChile` creada.
- Variable de entorno `PELUDITOS_DB_PASSWORD` configurada.

Desde una terminal de Windows:

```powershell
cd Proyecto-WS
.\gradlew.bat build
.\gradlew.bat run
```

El servicio queda disponible en:

`http://localhost:8081/api`

Para comprobar que está funcionando, abrir:

`http://localhost:8081/api/health`

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

### 6.1 Registro de leads

Ruta: `POST /api/leads`

Datos principales:

- nombre
- telefono
- correo
- canal
- interes

El servicio valida los campos obligatorios, registra la información en SQL Server y devuelve el identificador del lead creado.

### 6.2 Consulta de leads

Ruta: `GET /api/leads?page=1&size=10`

Permite consultar los leads mediante paginación.

- `page`: número de página.
- `size`: cantidad de registros por página.
- Valores predeterminados: página 1 y tamaño 10.

### 6.3 Consulta por identificador

Ruta: `GET /api/leads/{id}`

Permite obtener los datos de un lead específico. Si no existe, devuelve un error 404.

### 6.4 Registro de seguimientos

Ruta: `POST /api/leads/{id}/seguimientos`

Recibe información del seguimiento:

- medio
- comentario
- resultado

El seguimiento queda asociado al lead correspondiente.

### 6.5 Cierre de leads

Ruta: `PUT /api/leads/{id}/cierre`

Permite registrar si el interesado realizó una compra.

Datos recibidos:

- `compro`: valor booleano.
- `motivoNoCompra`: motivo cuando no se concreta la compra.

Los estados utilizados son `Convertido` y `No convertido`.

### 6.6 Registro de clientes

Ruta: `POST /api/clientes`

Permite registrar clientes con información como:

- nombre
- telefono
- correo
- comuna
- aceptaComunicaciones
- leadOrigenId

## 7. CORS

Se incorpora `CorsFilter.java` para gestionar las solicitudes de origen cruzado (CORS) y permitir la comunicación entre la interfaz web y el servicio REST según la configuración del proyecto.

## 8. Integración con Flowable y External Worker

El servicio dispone de operaciones REST para la gestión de leads y clientes.

El contrato previsto para el proceso considera:

1. El Worker recibe las variables del proceso, como `nombre`, `canal` e `interes`.
2. Invoca `POST /api/leads` para registrar el interesado.
3. Obtiene el identificador del lead desde la respuesta.
4. Devuelve las variables `leadId` y `registrado` al proceso Flowable.

La integración completa de los componentes debe verificarse mediante pruebas de punta a punta.

## 9. Pruebas y evidencias

Las evidencias del componente se encuentran en:

- `BaseDatos-Java/Evidencias-base-de-datos/`
- `BaseDatos-Java/Evidencias-Java/`

Incluyen capturas de creación de tablas, conexión con SQL Server, compilación, ejecución del servicio, operaciones REST y verificaciones de persistencia.

Los scripts de consultas de verificación permiten revisar los registros almacenados en la base de datos.

## 10. Estado del componente

El componente cuenta con:

- Conexión JDBC con SQL Server.
- Persistencia de leads.
- Consulta de leads con paginación.
- Consulta individual de leads.
- Registro de seguimientos.
- Cierre de leads.
- Registro de clientes.
- Configuración CORS.
- Evidencias de pruebas y persistencia.

La integración completa con Flowable, el External Worker y la interfaz debe validarse en el recorrido de punta a punta del proyecto.