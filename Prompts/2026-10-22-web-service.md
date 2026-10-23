# Registro de prompts – Web service Java (Proyecto-WS)

- **Fecha:** 2026-10-21 / 2026-10-22
- **Integrante:** Christopher Comas Celis
- **Herramienta:** Claude (Anthropic)
- **Componente afectado:** `Proyecto-WS` (web service REST) y lista inicial de web services

## Resumen del uso de IA
Se utilizó IA como **apoyo** para apoyar el código base del web service de prueba (Java 21, Gradle, Jakarta REST con Jersey) y una propuesta de la lista de web services, siguiendo el ejemplo del material complementario del profesor. **Yo ejecuté el servicio en mi computador, realicé las pruebas, tomé las evidencias y lo subí al repositorio.**

| Etapa | Quién lo hizo |
|---|---|
| Código base del web service y propuesta de endpoints | IA, a partir del documento de la Entrega 1 y del ejemplo del profesor |
| Verificación de Java 21 y ejecución del servicio (`gradlew.bat run`) | Yo |
| Pruebas con navegador y curl (`/api/health`, `POST /api/leads`, `GET /api/leads`) | Yo |
| Capturas de evidencia y subida a GitHub | Yo |
| Coordinación con la integración del external worker | Yo, junto a Jennifer Igor |

## Prompts utilizados

**Prompt 1: corregir mi parte según la Entrega 1**
```text
Revisa si esta todo correcto lo propuesto, si no corrigeme para hacerlo nuevamente
```

**Prompt 2: ejecución y pruebas**
```text
[Captura de la consola] La prueba y ejecución
```

## Resultado
- Web service REST en `Proyecto-WS` con las operaciones `GET /api/health`, `POST /api/leads`, `GET /api/leads` y `GET /api/leads/{id}` (datos en memoria).
- Lista inicial de 10 web services en `Proyecto-WS/README.md`, cada uno relacionado con una actividad del proceso.

## Validación (trabajo propio)
- Ejecuté el servicio con Java 21 (Temurin) y verifiqué el mensaje "Web service Peluditos activo".
- `GET /api/health` respondió `{"estado":"ok"}`.
- `POST /api/leads` registró un lead de prueba y respondió con `id` y `registrado: true`.
- `GET /api/leads` mostró el lead registrado.
- Evidencias en `Pruebas/` (`ws-1-servidor.png` a `ws-4-lista.png`).
- El servicio también se ejecutó correctamente en el computador de otra integrante del equipo.

## Aprendizaje
- Un web service REST expone operaciones mediante métodos HTTP (GET para consultar, POST para crear) y rutas (`/api/leads`).
- Jersey es una implementación de la especificación Jakarta REST; las anotaciones `@Path`, `@GET`, `@POST` definen los endpoints.
- Las advertencias de la consola (JAX-B, DataSource) son de funciones opcionales de la librería y no afectan el servicio.
- El external worker es quien conecta Flowable con este servicio: toma el trabajo del proceso, llama al web service y devuelve el resultado.
