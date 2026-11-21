# Manual técnico – Instalación, configuración y mantenimiento

**Proyecto:** Peluditos Pet Chile – Gestión de leads y oportunidades comerciales
**Versión del manual:** Entrega 3 (noviembre 2026)
**Dirigido a:** una persona con conocimientos técnicos que necesite instalar, configurar o mantener la solución.
**Responsable del documento:** Christopher Comas Celis

> Los comandos están escritos para **Windows** (CMD o PowerShell), que es el sistema que usa el equipo. Los valores entre `< >` se reemplazan por los propios. **Nunca escriba contraseñas reales en archivos que se suban al repositorio.**

---

## 1. Qué se instala

La solución tiene seis piezas. La emprendedora solo usa la **interfaz**; el resto trabaja por detrás.

| Pieza | Función | Carpeta / origen |
|---|---|---|
| Interfaz propia | Pantallas para registrar consultas, iniciar campañas y completar tareas | `Proyecto-UI/` |
| Flowable OSS 7.2 | Motor que ejecuta los dos procesos BPMN | Descarga oficial (aplicación `flowable-rest`) |
| PostgreSQL | Base de datos interna de Flowable | Instalación local |
| External worker | Ejecuta las tareas automáticas llamando al web service | `Proyecto-Worker/` |
| Web service | Operaciones del negocio (leads, clientes, oportunidades) | `Proyecto-WS/` |
| SQL Server | Base de datos del negocio | `Proyecto-WS/BaseDatos-Java/Scripts/` |

Diagrama de componentes y de entornos: [`Arquitectura/Arquitectura.md`](../Arquitectura/Arquitectura.md).

## 2. Requisitos

| Software | Versión usada por el equipo | Uso |
|---|---|---|
| Java JDK | 21 | Web service, worker y Tomcat |
| Apache Tomcat | 10.1.x | Servidor de Flowable |
| Flowable OSS (`flowable-rest.war`) | 7.2.0 | Motor de procesos |
| PostgreSQL + pgAdmin | 18 | Base de datos de Flowable |
| Driver JDBC de PostgreSQL | 42.7.x | Conexión Tomcat → PostgreSQL |
| Microsoft SQL Server + SSMS | 2017 / SSMS 20 | Base de datos del negocio |
| Node.js + npm | 20 o superior | Interfaz |
| Git | cualquiera reciente | Descargar el proyecto |

Puertos usados por defecto: **3000** (interfaz), **8080** (Flowable), **8081** (web service), **5432** (PostgreSQL), **1433** (SQL Server). Todos se pueden cambiar por configuración (sección 4).

Descargar el proyecto:

```bat
git clone https://github.com/scarlettpaz85-blip/proyecto-grupal.git
cd proyecto-grupal
```

## 3. Instalación paso a paso

Siga el orden: primero las bases de datos, luego los servicios y al final la interfaz.

### 3.1 Base de datos del negocio (SQL Server)

1. En **SQL Server Configuration Manager**, habilite el protocolo **TCP/IP** y confirme el puerto **1433**. Reinicie el servicio de SQL Server.
2. En **SSMS**, ejecute los scripts de `Proyecto-WS/BaseDatos-Java/Scripts/` **en este orden**:
   1. `01_Creacion_DB_PeluditosPetChile.sql`: crea la base `DB_PeluditosPetChile` y sus 6 tablas.
   2. `02_Inserciones_Prueba.sql`: datos de prueba (clientes, mascotas, fechas relevantes). Opcional en producción.
   3. `03_Consultas_Verificacion.sql`: consultas para comprobar que todo quedó bien.
3. Cree el usuario que usará el web service (si no existe):

   ```sql
   CREATE LOGIN peluditos_java WITH PASSWORD = '<contraseña segura>';
   USE DB_PeluditosPetChile;
   CREATE USER peluditos_java FOR LOGIN peluditos_java;
   ALTER ROLE db_datareader ADD MEMBER peluditos_java;
   ALTER ROLE db_datawriter ADD MEMBER peluditos_java;
   ```

   La autenticación de SQL Server debe estar en modo mixto (Windows y SQL Server).

### 3.2 Base de datos de Flowable (PostgreSQL)

1. Instale PostgreSQL y abra pgAdmin.
2. Cree la base de datos **`flowable_db`** y un usuario con permisos sobre ella. Flowable crea sus tablas (`act_...`) solo al iniciar por primera vez.

### 3.3 Motor de procesos (Tomcat + Flowable OSS)

1. Descomprima Tomcat 10.1 y copie `flowable-rest.war` en `tomcat/webapps/`.
2. Copie el driver `postgresql-42.7.x.jar` en `tomcat/lib/`.
3. Inicie Tomcat una vez (`tomcat/bin/startup.bat`) para que se descomprima el `.war` y luego deténgalo (`shutdown.bat`).
4. Configure la conexión a PostgreSQL en `tomcat/webapps/flowable-rest/WEB-INF/classes/flowable-default.properties`:

   ```properties
   spring.datasource.driver-class-name=org.postgresql.Driver
   spring.datasource.url=jdbc:postgresql://localhost:5432/flowable_db
   spring.datasource.username=<usuario de PostgreSQL>
   spring.datasource.password=<contraseña de PostgreSQL>
   ```

5. Inicie Tomcat. Compruebe que responde (usuario por defecto `rest-admin`; cambie su contraseña en producción):

   ```bat
   curl -u rest-admin:<contraseña> http://localhost:8080/flowable-rest/service/management/engine
   ```

6. **Despliegue los dos procesos** desde la carpeta del proyecto:

   ```bat
   curl -u rest-admin:<contraseña> -F "file=@BPMN/TO-BE.bpmn" http://localhost:8080/flowable-rest/service/repository/deployments
   curl -u rest-admin:<contraseña> -F "file=@BPMN/TO-BE-oportunidades.bpmn" http://localhost:8080/flowable-rest/service/repository/deployments
   ```

   Verifique que aparezcan `peluditosLead` y `peluditosOportunidad`:

   ```bat
   curl -u rest-admin:<contraseña> "http://localhost:8080/flowable-rest/service/repository/process-definitions?latest=true"
   ```

### 3.4 Web service (`Proyecto-WS`)

1. Copie `Proyecto-WS/.env.example` como `Proyecto-WS/.env` y complete los valores (sección 4). El archivo `.env` está en `.gitignore` y no se sube.
2. Compile y ejecute:

   ```bat
   cd Proyecto-WS
   gradlew.bat testDatabase
   gradlew.bat run
   ```

   `testDatabase` debe mostrar "CONEXION EXITOSA CON SQL SERVER".
3. Compruebe en el navegador: `http://localhost:8081/api/health` → `{"servicio":"peluditos-ws","estado":"ok"}`.

### 3.5 External worker (`Proyecto-Worker`)

```bat
cd Proyecto-Worker
javac PeluditosApp.java
set FLOWABLE_URL=http://localhost:8080/flowable-rest
set WS_URL=http://localhost:8081/api
set FLOWABLE_USER=rest-admin
set FLOWABLE_PASSWORD=<contraseña de Flowable>
java PeluditosApp
```

Al iniciar muestra los 4 tópicos que atiende y luego, cada 2 segundos, "Sin trabajos pendientes" mientras no haya tareas automáticas.

### 3.6 Interfaz (`Proyecto-UI`)

1. Copie `Proyecto-UI/.env.example` como `Proyecto-UI/.env` y ajuste las direcciones de Flowable y del web service.
2. Instale y ejecute:

   ```bat
   cd Proyecto-UI
   npm install
   npm run dev
   ```

3. Abra `http://localhost:3000`.

> Si PowerShell bloquea `npm` ("la ejecución de scripts está deshabilitada"), use CMD, o ejecute una sola vez: `Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned`.

## 4. Configuración

Ninguna dirección, puerto ni contraseña debe quedar escrita en el código: todo se configura con **variables de entorno** o con un archivo **`.env`** local (no versionado). Cada componente incluye un `.env.example` con los nombres de las variables y valores de ejemplo sin contraseñas.

| Componente | Variable | Valor por defecto | Descripción |
|---|---|---|---|
| Web service | `PELUDITOS_WS_PORT` | `8081` | Puerto del web service |
| Web service | `PELUDITOS_DB_HOST` | `localhost` | Servidor de SQL Server |
| Web service | `PELUDITOS_DB_PORT` | `1433` | Puerto de SQL Server |
| Web service | `PELUDITOS_DB_NAME` | `DB_PeluditosPetChile` | Base de datos del negocio |
| Web service | `PELUDITOS_DB_USER` | `peluditos_java` | Usuario de SQL Server |
| Web service | `PELUDITOS_DB_PASSWORD` | (sin valor) | Contraseña de SQL Server. **Obligatoria** |
| Worker | `FLOWABLE_URL` | `http://localhost:8080/flowable-rest` | Dirección de Flowable |
| Worker | `WS_URL` | `http://localhost:8081/api` | Dirección base del web service |
| Worker | `FLOWABLE_USER` / `FLOWABLE_PASSWORD` | (sin valor) | Credenciales de Flowable (o `FLOWABLE_TOKEN`) |
| Worker | `WORKER_ID` | `peluditos-worker` | Nombre con que el worker se identifica ante Flowable |
| Interfaz | `VITE_FLOWABLE_TARGET` | `http://localhost:8080` | Destino del proxy hacia Flowable |
| Interfaz | `VITE_WS_TARGET` | `http://localhost:8081` | Destino del proxy hacia el web service |

**Componentes en distintos equipos:** si el web service corre en otro computador, basta con cambiar `WS_URL` (worker) y `VITE_WS_TARGET` (interfaz) por la dirección de ese equipo. En el desarrollo del proyecto se usó la red privada **Tailscale** para unir dos computadores; en ese caso se usa la IP de Tailscale (`100.x.x.x`).

## 5. Orden de encendido y apagado

**Encender:**
1. SQL Server y PostgreSQL (servicios de Windows).
2. Tomcat con Flowable (`startup.bat`). Esperar a que responda.
3. Web service (`gradlew.bat run`).
4. Worker (`java PeluditosApp`).
5. Interfaz (`npm run dev`).

**Apagar:** en orden inverso. Las tareas pendientes no se pierden: quedan guardadas en PostgreSQL y vuelven a aparecer al encender. Si el web service está apagado cuando el worker toma un trabajo, el worker informa el fallo a Flowable y el trabajo se reintenta (hasta 3 veces, cada 30 segundos).

## 6. Verificación rápida

| Comprobación | Cómo | Resultado esperado |
|---|---|---|
| SQL Server | `gradlew.bat testDatabase` | "CONEXION EXITOSA CON SQL SERVER" |
| Web service | `http://localhost:8081/api/health` | `"estado":"ok"` |
| Flowable | `curl -u rest-admin:<contraseña> .../repository/process-definitions?latest=true` | Aparecen `peluditosLead` y `peluditosOportunidad` |
| Worker | Consola del worker | Lista los 4 tópicos y "Sin trabajos pendientes" |
| Interfaz | `http://localhost:3000` → Bandeja | La bandeja carga sin errores |
| De punta a punta | Registrar un lead de prueba desde la interfaz | El worker muestra "HTTP 201" y el lead aparece en `dbo.Leads` |

## 7. Mantenimiento

**Respaldos (recomendado: semanal):**

```sql
-- SQL Server (en SSMS)
BACKUP DATABASE DB_PeluditosPetChile TO DISK = 'C:\respaldos\peluditos.bak';
```

```bat
:: PostgreSQL (Flowable)
pg_dump -U <usuario> -F c -f C:\respaldos\flowable_db.backup flowable_db
```

**Actualizar un proceso BPMN:** vuelva a desplegar el archivo con el mismo comando de la sección 3.3. Flowable crea una **nueva versión**: las instancias nuevas usan la versión nueva y las que ya estaban en curso terminan con la versión anterior. Si el cambio agrega o quita tareas de usuario, la interfaz debe actualizarse antes.

**Agregar una fecha de campaña:** por ahora se hace directamente en SQL Server, en la tabla `FechasRelevantes`. Desde la interfaz solo se pueden elegir las fechas existentes (pendiente para la Entrega 4).

**Cambiar contraseñas:** actualice la contraseña en SQL Server, PostgreSQL o Flowable y luego el valor correspondiente en el `.env` o en la variable de entorno. Reinicie el componente afectado.

**Revisar errores:** cada componente escribe en su propia consola. El worker muestra cada trabajo recibido, la respuesta del web service y los errores (`[Error <tópico>]`). Las respuestas de error del web service tienen la forma `{"error": "mensaje"}`.

## 8. Problemas frecuentes

| Síntoma | Causa probable | Solución |
|---|---|---|
| La interfaz muestra 0 tareas, pero el proceso fue iniciado | Hay otra copia de la interfaz abierta, o no apunta al Flowable correcto | Cierre las terminales extra; revise `VITE_FLOWABLE_TARGET`; compruebe las tareas con `curl .../runtime/tasks` |
| `npm run dev` abre el puerto 3001 | El 3000 está ocupado por otra copia de la interfaz | Cierre la otra terminal o use la dirección que indique Vite |
| `localhost:8081` "rechazó la conexión" | El web service está en otro equipo, o está apagado | Use la dirección del equipo donde corre (IP de Tailscale) o inícielo |
| El worker repite "Error … Connection refused" | El web service no está encendido o `WS_URL` es incorrecta | Encienda el web service y revise `WS_URL` |
| `testDatabase` falla con error de conexión | TCP/IP deshabilitado, puerto distinto o contraseña incorrecta | Revise SQL Server Configuration Manager y el `.env` |
| Error de certificado al conectar a SQL Server | Certificado autofirmado | La conexión ya usa `trustServerCertificate=true`; revise que no se haya cambiado |
| PowerShell no deja ejecutar `npm` | Política de ejecución de scripts | Use CMD o `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned` |
| Flowable no inicia | No encuentra el driver de PostgreSQL o los datos de conexión son incorrectos | Revise `tomcat/lib/` y `flowable-default.properties`; vea `tomcat/logs/catalina.*.log` |

## 9. Seguridad

- Las contraseñas solo van en `.env` locales o en variables de entorno. Los `.env` están en `.gitignore`.
- Cambie la contraseña por defecto de `rest-admin` antes de usar la solución con datos reales.
- No comparta capturas de pantalla que muestren contraseñas o tokens.
- El usuario `peluditos_java` solo tiene permisos de lectura y escritura sobre `DB_PeluditosPetChile`.

## 10. Limitaciones conocidas de esta versión

- La interfaz se ejecuta con el servidor de desarrollo de Vite. Para una instalación definitiva hay que compilarla (`npm run build`) y servirla con un proxy web; ver el entorno de destino en [`Arquitectura/Arquitectura.md`](../Arquitectura/Arquitectura.md).
- Cada componente se inicia por separado y a mano; no hay un script único de arranque.
- Las fechas de campaña se agregan directamente en la base de datos.

El detalle de pendientes está en el plan para la Entrega 4.
