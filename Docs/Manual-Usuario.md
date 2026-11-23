# Manual de usuario – Peluditos Pet Chile

**Para:** la emprendedora de Peluditos Pet Chile
**Versión:** Entrega 3 (noviembre 2026)
**Preparado por:** equipo de Integración de Plataformas – Iplacex

---

## 1. ¿Para qué sirve este sistema?

Este sistema le permite tener **en un solo lugar** a todas las personas que le consultan por sus productos, sin importar si le escribieron por Instagram, WhatsApp, Facebook, TikTok, teléfono o la tienda web.

Con él usted podrá:

- **Anotar cada consulta** para que ninguna quede sin respuesta.
- **Hacer el seguimiento** y registrar si la persona compró o no, y por qué.
- **Volver a contactar a sus clientes** en fechas especiales (Navidad, cumpleaños de mascotas, campañas) y registrar el resultado de cada contacto.

El sistema le va indicando lo que tiene pendiente, de modo que no necesita recordarlo de memoria.

## 2. Antes de empezar

1. Abra el navegador (Chrome, Edge u otro) en la dirección que le entregó el equipo técnico. Durante el proyecto es `http://localhost:3000`.
2. Verá arriba el nombre **Peluditos Pet Chile** y cuatro botones: **Anotar**, **Campañas**, **Bandeja** y **Lista**.

> Si la página no carga o aparece un mensaje de error, revise la sección 8 o contacte al equipo técnico. La instalación está descrita en el [Manual técnico](Manual-Tecnico.md).

📸 *Captura: pantalla de inicio con los cuatro botones.*

## 3. Las cuatro secciones

| Botón | Para qué sirve | Cuándo usarlo |
|---|---|---|
| **Anotar** | Registrar a una persona que le hizo una consulta | Cada vez que llega una consulta nueva |
| **Bandeja** | Ver y completar lo que tiene pendiente | Todos los días, idealmente al comenzar la jornada |
| **Campañas** | Iniciar el recontacto de clientes en una fecha especial | Unos días antes de cada fecha importante |
| **Lista** | Consultar las personas registradas | Cuando quiera revisar el historial |

## 4. Anotar una consulta nueva

Use esta opción cada vez que alguien le pregunte por un producto o servicio.

1. Presione **Anotar**.
2. Complete el formulario **Anotar interesado**:

   | Campo | Qué escribir | ¿Obligatorio? |
   |---|---|---|
   | **Nombre** | Nombre de la persona (por ejemplo, *María González*) | Sí |
   | **Teléfono** | Número de contacto (por ejemplo, *+56912345678*) | Recomendado |
   | **Correo** | Correo electrónico, si lo tiene | No |
   | **Canal de llegada** | Por dónde le escribió: Instagram, WhatsApp, Facebook, TikTok, teléfono o tienda web | Se elige de la lista |
   | **¿Qué consulta?** | Qué le interesa (por ejemplo, *Torta mediana para el cumpleaños de su perro*) | Sí |
   | **Servicio** | Repostería para mascotas, Baños para perros o Repuestos de baños ecológicos | Se elige de la lista |

3. Presione **Anotar y clasificar**.
4. Aparecerá el mensaje *"Consulta anotada y clasificada"*. El sistema guarda la consulta y, en unos segundos, crea en su **Bandeja** la tarea **Realizar seguimiento** para esa persona.

Si falta un dato obligatorio, el sistema no le dejará continuar y le indicará qué campo debe completar.

📸 *Captura: formulario "Anotar interesado" completo.*

## 5. La bandeja: sus tareas pendientes

La **Bandeja** funciona como una lista de tareas. Cada recuadro es algo que usted tiene por hacer. Al hacer clic en una tarea, verá a la derecha los datos de la persona (nombre, teléfono, canal y qué le interesa) y lo que debe completar.

| Tarea | Qué significa |
|---|---|
| **Realizar seguimiento** | Debe contactar a la persona para saber si compró |
| **Registrar motivo de no compra** | La persona no compró; debe anotar por qué |
| **Gestionar pedido** | La persona compró; debe preparar y entregar su pedido |
| **Contactar cliente** | Es parte de una campaña; debe ofrecerle el producto a un cliente |
| **Gestionar pedido de campaña** | Un cliente de la campaña quiere comprar; debe preparar su pedido |

Cuando completa una tarea, desaparece de la bandeja. Si espera una tarea que aún no aparece, presione **Actualizar** después de unos segundos: algunos pasos los realiza el sistema automáticamente.

📸 *Captura: bandeja con tareas de los dos tipos.*

## 6. Seguimiento de una consulta

### 6.1 Realizar seguimiento

1. En la **Bandeja**, haga clic en **Realizar seguimiento**.
2. Revise los datos de la persona y contáctela.
3. En **¿Compró?**, marque **Sí** o **No**. Esta opción no viene marcada; debe elegirla usted.
4. Presione **Completar seguimiento**.

### 6.2 Si la persona compró

Aparecerá la tarea **Gestionar pedido**. Además, el sistema registra a la persona como **cliente**, de modo que podrá recibir sus campañas en el futuro.

1. Abra **Gestionar pedido**.
2. Si lo desea, escriba una nota (por ejemplo, *pedido listo para retiro el sábado*).
3. Presione **Marcar pedido gestionado**.

### 6.3 Si la persona no compró

Aparecerá la tarea **Registrar motivo de no compra**. Esta información le permitirá saber por qué se pierden ventas.

1. Abra **Registrar motivo de no compra**.
2. Elija el motivo: **Precio**, **Fecha no disponible**, **Sin respuesta**, **Compró en otro lugar** u **Otro**.
3. Si lo desea, agregue un comentario (por ejemplo, *lo necesitaba para mañana*).
4. Presione **Registrar motivo**.

📸 *Captura: tarea "Registrar motivo de no compra".*

## 7. Campañas: volver a contactar a sus clientes

Use esta opción para ofrecer sus productos a clientes que ya le compraron, en fechas especiales.

### 7.1 Iniciar una campaña

1. Presione **Campañas**.
2. En **Fecha relevante**, elija la fecha de la campaña (por ejemplo, *Navidad*).
3. Escriba un **nombre para la campaña** (por ejemplo, *Navidad 2026*).
4. Presione **Iniciar campaña**.
5. El sistema busca a los clientes que **aceptan recibir comunicaciones** y crea una tarea **Contactar cliente** por cada uno. Si ninguno corresponde, la campaña termina sin tareas.

En la parte inferior de **Campañas** puede revisar las **oportunidades** de cada fecha y su estado: *Generada* (por contactar), *Ganada* (quiso comprar) o *Sin compra*.

📸 *Captura: pantalla de Campañas con la fecha elegida.*

### 7.2 Contactar a cada cliente

Las tareas de la campaña aparecen **de a una** en la bandeja: al completar un cliente, aparece el siguiente.

1. En la **Bandeja**, abra **Contactar cliente**. Verá el nombre del cliente, su teléfono y su mascota.
2. Contáctelo con la oferta de la campaña.
3. En **¿Interesado?**, marque **Sí** o **No**, y anote un comentario si lo desea (por ejemplo, *pidió una torta para el 24*).
4. Presione **Completar contacto**.
5. Si el cliente está interesado, aparecerá **Gestionar pedido de campaña**: ábrala, agregue una nota si lo desea y presione **Marcar pedido de campaña**.

Cuando termina con todos los clientes, la campaña finaliza.

📸 *Captura: tarea "Contactar cliente".*

## 8. Mensajes que puede ver

| Mensaje | Qué significa | Qué hacer |
|---|---|---|
| *"El nombre es obligatorio"* u otro campo obligatorio | Falta un dato en el formulario | Complete el campo indicado |
| *"El motivo de no compra es obligatorio"* | No se eligió un motivo | Elija un motivo de la lista |
| *"No se pudo cargar la bandeja"* | El sistema de procesos no está disponible | Espere unos minutos y presione **Actualizar**; si continúa, contacte al equipo técnico |
| *"No se pudieron cargar las fechas"* / *"Sin fechas activas"* | No hay fechas de campaña configuradas o el servicio no responde | Contacte al equipo técnico |
| *"No se pudo iniciar la campaña"* | El sistema no pudo crear la campaña | Contacte al equipo técnico |

Sus datos no se pierden si aparece un error: las tareas pendientes quedan guardadas y vuelven a aparecer cuando el sistema se recupera.

## 9. Preguntas frecuentes

**¿Qué pasa si cierro el navegador?**
Nada. Sus tareas quedan guardadas y las verá de nuevo al volver a entrar.

**¿Puedo usarlo desde el celular?**
Sí, si el sistema está instalado en un equipo al que su celular pueda conectarse. Hoy funciona en el computador donde está instalado; la instalación definitiva está planificada para la siguiente etapa.

**¿Puedo agregar una fecha de campaña nueva?**
Por ahora, las fechas las agrega el equipo técnico. Que usted pueda agregarlas desde el sistema está planificado para la siguiente etapa.

**¿Dónde veo el historial de una persona?**
En **Lista** verá a las personas registradas y su estado. Las oportunidades de cada campaña se consultan en **Campañas**.

## 10. Recomendaciones de uso

- Anote cada consulta **apenas llega**, aunque sea solo con el nombre y lo que busca.
- Revise la **Bandeja** todos los días.
- Registre siempre el **motivo** cuando alguien no compra: con el tiempo le mostrará qué mejorar (precios, plazos, respuesta).
- Inicie las campañas **con algunos días de anticipación** a cada fecha importante.
