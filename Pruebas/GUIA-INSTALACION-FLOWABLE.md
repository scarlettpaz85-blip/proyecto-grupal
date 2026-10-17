# Guía de instalación – Flowable REST 7.2 en Tomcat 10

> Cada integrante debe poder levantarlo en su equipo.

## 1. Requisitos
| Software | Versión | Descarga |
|---|---|---|
| Java JDK | 21 | https://adoptium.net (Temurin 21) |
| Apache Tomcat | 10.1.x | https://tomcat.apache.org/download-10.cgi (zip "Core") |
| Flowable OSS | 7.2.0 | https://github.com/flowable/flowable-engine/releases (archivo `flowable-7.2.0.zip`) |
| VS Code + extensión REST Client | – | Para ejecutar `Pruebas/flowable.http` |

Verificar Java en una terminal: `java -version` → debe decir 21.

## 2. Instalación
1. Descomprimir Tomcat, por ejemplo en `C:\tomcat10` (Windows) o `~/tomcat10` (Mac/Linux).
2. Descomprimir `flowable-7.2.0.zip` y buscar `wars/flowable-rest.war`.
3. Copiar `flowable-rest.war` a la carpeta `webapps/` de Tomcat.
4. Iniciar Tomcat:
   - Windows: `bin\startup.bat`
   - Mac/Linux: `chmod +x bin/*.sh && bin/startup.sh`
5. Esperar ~30–60 s y abrir: http://localhost:8080/flowable-rest/docs
6. Usuario `rest-admin`, clave `test`.

## 3. Prueba
Abrir `Pruebas/flowable.http` en VS Code y ejecutar las peticiones 1 → 10 en orden.

## 4. Problemas comunes
| Síntoma | Solución |
|---|---|
| Puerto 8080 ocupado | Cambiar `port="8080"` en `conf/server.xml` y ajustar `@baseUrl` |
| 404 en /flowable-rest | Revisar que el war se haya descomprimido en `webapps/flowable-rest/`; ver `logs/catalina.*.log` |
| `JAVA_HOME` no definido | Definir la variable de entorno apuntando al JDK 21 |
| Desaparecen los despliegues al reiniciar | La configuración por defecto puede usar una BD en memoria; volver a desplegar (más adelante se configura una BD persistente) |

## 5. Evidencia para la entrega
Guardar capturas en `Pruebas/evidencias/`:
1. Swagger abierto (`/docs`)
2. Respuesta del despliegue
3. Instancia iniciada
4. Bandeja con la tarea
5. Instancia finalizada en el historial
