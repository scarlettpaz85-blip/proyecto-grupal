# Modelos BPMN

| Archivo | Herramienta | Descripción |
|---|---|---|
| `AS-IS.bpmn` / `AS-IS.svg` | bpmn.io (varios pools) | Proceso actual de Peluditos Pet Chile: atención de consultas (Problema 1) y recontacto en fechas especiales (Problema 2) |
| `TO-BE.bpmn` | Flowable Design | Proceso propuesto, ejecutable en Flowable |
| `gestion-lead-minimo.bpmn20.xml` | Flowable | Proceso mínimo de prueba |

> Según indicación del profesor: el TO-BE se modela directamente en Flowable Design para no mantener dos modelos en paralelo.

## AS-IS – Descripción

El archivo `AS-IS.bpmn` contiene los dos procesos actuales, separados visualmente mediante **grupos** dentro del pool *Peluditos Pet Chile*.

**Participantes:**
- Pool *Cliente* (expandido): participa en el Proceso 1.
- Pool *Peluditos Pet Chile*, con el lane *Emprendedora*: realiza ambos procesos.
- Pool *Clientes y seguidores* (colapsado, caja negra): recibe las promociones del Proceso 2.

### Proceso 1: Atención de consultas
- **Flujo:** el cliente consulta por mensaje directo, teléfono o tienda web → la emprendedora lee la consulta y responde con precios, opciones y disponibilidad → si el cliente confirma y transfiere, se verifica el pago, se prepara el pedido y se entrega; si el cliente deja de responder, la consulta queda sin seguimiento.
- **Elementos BPMN:** eventos de inicio y fin, eventos de mensaje (inicio e intermedio), evento de temporizador, gateway exclusivo, gateway basado en eventos, flujos de mensaje, almacén de datos y anotaciones.
- **Dolores:** no se registra la consulta ni el canal; no hay recontacto; no se registra al cliente ni a su mascota; el perfil no tiene enlace directo a WhatsApp.

### Proceso 2: Recontacto en fechas especiales
- **Flujo:** la emprendedora recuerda una fecha → busca clientes en los chats → publica una promoción o envía mensajes uno a uno → responde las consultas que lleguen.
- **Elementos BPMN:** gateway exclusivo de división y unión, almacén de datos, pool colapsado, flujos de mensaje y anotaciones.
- **Dolores:** depende de la memoria; no hay registro de fechas ni de cumpleaños de mascotas; no se mide el resultado de las campañas; publicaciones esporádicas y sin calendario de campañas.
- **Evidencia (Instagram, 17-10-2026):** se celebran cumpleaños de mascotas, pero no hay campaña de Halloween.

### Criterio de modelado
Las tareas se modelaron de forma **genérica** (por ejemplo, "Preparar pedido"), para que el proceso aplique a cualquier producto del emprendimiento, según la indicación del profesor.

## Supuestos (por validar con la emprendedora)
- El AS-IS se basó en el documento del caso, la tienda web y el Instagram; aún no hay respuestas de la emprendedora.
- Las ventas se cierran por mensajería con pago por transferencia (único medio visible en la tienda).
- No existe hoy ningún registro de consultas, clientes ni mascotas.
- Las campañas se difunden por publicaciones en redes o mensajes uno a uno por WhatsApp.

## Variables del proceso (TO-BE)

| Variable | Tipo | Origen (formulario / worker) | Uso |
|---|---|---|---|
| | | | |

## Historial de versiones

| Fecha | Versión | Cambio | Autor |
|---|---|---|---|
| 2026-10-17 | v1.0 | AS-IS atención de consultas y recontacto de clientes (dos archivos) | Christopher Comas Celis |
| 2026-10-20 | v1.1 | Unificación en `AS-IS.bpmn` según documento de la Entrega 1; tareas genéricas; evidencia del Instagram | Christopher Comas Celis |
