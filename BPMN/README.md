# Modelos BPMN

| Archivo | Herramienta | Descripción |
|---|---|---|
| `AS-IS-1-atencion-consultas.bpmn` / `.png` | bpmn.io / Camunda Modeler (varios pools) | Cómo se atienden hoy las consultas (Problema 1) |
| `AS-IS-2-recontacto-clientes.bpmn` / `.png` | bpmn.io / Camunda Modeler (varios pools) | Cómo se recontacta hoy a clientes en fechas especiales (Problema 2) |
| `gestion-lead-minimo.bpmn20.xml` | Flowable | Proceso mínimo ejecutable (Entrega 1) |
| `TO-BE.bpmn` / `TO-BE.png` | Flowable Design (un pool, varios lanes) | Proceso futuro **ejecutable** en Flowable |

> Según indicación del profesor: el TO-BE se modela directamente en Flowable Design para no mantener dos modelos en paralelo.

## AS-IS – Descripción

### AS-IS 1: Atención de consultas
- **Participantes:** pool *Cliente* y pool *Peluditos Pet Chile* (lane *Emprendedora*).
- **Flujo:** el cliente consulta por redes → la emprendedora lee y responde → si el cliente confirma y transfiere, se verifica el pago, se elabora la torta y se entrega; si el cliente deja de responder, la consulta queda sin seguimiento.
- **Elementos BPMN usados:** eventos de inicio y fin, evento de mensaje (inicio e intermedio), evento de temporizador, gateway exclusivo, gateway basado en eventos, flujos de mensaje, almacén de datos, anotaciones.
- **Dolores:** no se registra la consulta ni el canal; no hay recontacto; no se registra al cliente ni a su mascota.

### AS-IS 2: Recontacto en fechas especiales
- **Participantes:** pool *Peluditos Pet Chile* (lane *Emprendedora*) y pool colapsado *Clientes y seguidores* (caja negra).
- **Flujo:** la emprendedora recuerda una fecha → busca clientes en los chats → publica una promoción o envía mensajes uno a uno → responde las consultas que lleguen.
- **Dolores:** depende de la memoria; no hay registro de fechas ni de cumpleaños de mascotas; no se mide el resultado de las campañas.

## Supuestos (por validar con la emprendedora)
- El AS-IS se basó en el documento del caso, la tienda web y el Instagram; aún no hay respuestas de la emprendedora.
- Las ventas se cierran por mensajería con pago por transferencia (único medio visible en la tienda).
- No existe hoy ningún registro de consultas, clientes ni mascotas.
- 

## Variables del proceso (TO-BE)

| Variable | Tipo | Origen (formulario / worker) | Uso |
|---|---|---|---|
| | | | |

## Historial de versiones

| Fecha | Versión | Cambio | Autor |
|---|---|---|---|
| | v0.1 | Versión preliminar | |
