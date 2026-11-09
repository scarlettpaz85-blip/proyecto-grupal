# Registro de prompts – Coordinación y documentación de la Entrega 2

- **Fecha:** 2026-10-27 al 2026-11-09
- **Integrante:** Christopher Comas Celis
- **Herramienta:** Claude (Anthropic)
- **Componente afectado:** Documentación (`Arquitectura/`, `Actas/`, `Pruebas/Entrega-2/E2E/`, `README.md`, `Videos/Entrega-2/`)

## Resumen del uso de IA
Se utilizó IA como **apoyo para coordinar y documentar**: redactar borradores de documentos, preparar guiones y ordenar evidencias. Las decisiones, la conducción de la reunión, la prueba de punta a punta, las grabaciones y las subidas al repositorio las hice yo o el equipo.

| Etapa | Quién lo hizo |
|---|---|
| Repartir tareas y coordinar al equipo por WhatsApp | Yo |
| Guiar la prueba de punta a punta en Google Meet (07-11) | Yo, con Scarlett y Jennifer ejecutando en sus equipos |
| Resolver problemas en vivo (interfaz sin tareas, PowerShell, puerto 8081) | Yo, con apoyo de la IA para diagnosticar |
| Guion de la prueba E2E, actas y README de evidencias | Borrador de la IA, revisado por mí con lo que pasó en la reunión |
| Actualizar el diagrama de arquitectura | IA, a partir de lo que probamos; revisado por mí |
| Guiones de los videos y presentación para la emprendedora | Borrador de la IA; grabación y presentación mías |
| Subir todo a GitHub y grabar los videos | Yo |

## Prompts utilizados

**Prompt 1: guía para la prueba de punta a punta**
```text
Coordinemos donde debo registrar cada screen para mantener informado.
```

**Prompt 2: diagnóstico durante la reunión**
```text
No me sale el 8081, revisemos codigo fuente y guiame para saber el paso.
```
*(con captura del error "localhost rechazó la conexión")*

**Prompt 3: ordenar las evidencias**
```text
Ya estan sacado los screen la numeracion la necesito y el video lo tengo listo.
```

**Prompt 4: revisión del repositorio**
```text
Revision general de repositorio por subidas de avance.
```

**Prompt 5: video para la emprendedora**
```text
Hagamos un guion para la emprendedora alguien a quien debemos explicar de manera entendible para todo tipo de publico sin necesidad de llegar mas alla.
```

## Resultado
- Guion de la prueba E2E con los recorridos A (no compra) y B (sí compra).
- README de `Pruebas/Entrega-2/E2E/` con las 12 capturas numeradas y explicadas.
- Diagrama de arquitectura actualizado: componentes implementados y montaje en dos equipos unidos por Tailscale.
- Actas del 05-11 y del 07-11.
- Guiones del video técnico y del video para la emprendedora, y la presentación de apoyo.

## Ajustes manuales y validación
- Las capturas son de la reunión real del 07-11; se verificó en SQL Server que el lead 8 quedó "Convertido" y el lead 9 "No convertido".
- Se corrigieron datos que la IA no conocía (por ejemplo, que el web service corría en el equipo de Jennifer).
- Se pidió simplificar el lenguaje del video para la emprendedora, sin términos técnicos.
- Se revisó en GitHub que cada archivo quedara subido correctamente.

## Aprendizaje
- Cómo se conectan los componentes: la interfaz inicia el proceso en Flowable, el worker toma los trabajos y llama al web service, y este guarda en SQL Server.
- Por qué `localhost` no funciona entre dos computadores, y para qué sirve una red privada como Tailscale.
- Cómo comprobar con `curl` si un problema está en Flowable o en la interfaz.
- Cómo explicar una solución técnica en lenguaje simple para el cliente.
