# Análisis del caso – Peluditos Pet Chile

> Responsable: [Nombre] (Coordinación y análisis) · Versión 0.1 · 2026-10-17
> Fuentes: documento del caso (PDF), tienda web https://peluditospetchile.sumerlabs.com, Instagram @peluditospetchile.
> ⚠️ Los puntos marcados **[Supuesto]** deben validarse con la emprendedora (ver `Actas/PREGUNTAS-EMPRENDEDOR.md`).

## 1. Contexto
Peluditos Pet Chile elabora y vende pastelería para perros y gatos (tortas pequeñas, medianas, grandes y para gatos, con sabores personalizables). Su tienda web también ofrece baños ecológicos para mascotas y repuestos. La atención se realiza principalmente por redes sociales (Instagram, Facebook, TikTok) y WhatsApp.

## 2. Problemas identificados
| ID | Problema | Consecuencia |
|---|---|---|
| P1 | Las consultas llegan por varios canales y no se registran | No se sabe cuántos interesados hay ni de dónde vienen |
| P2 | No hay seguimiento formal de los interesados | Se pierden ventas por no volver a contactar |
| P3 | No se mide la conversión (interesado → cliente) | No se sabe qué canal funciona mejor |
| P4 | No se registran fechas relevantes de clientes y mascotas | Se pierden oportunidades de recompra (cumpleaños, campañas) |
| P5 | El recontacto depende de la memoria de la emprendedora | Contactos olvidados o tardíos |

## 3. Actores
| Actor | Descripción | Rol en el sistema |
|---|---|---|
| Emprendedora | Dueña del negocio; responde consultas y vende | Usuaria principal: registra leads, hace seguimiento, contacta clientes |
| Interesado (lead) | Persona que consulta y aún no compra | Se registra su información |
| Cliente | Persona que ya compró | Recibe contacto en fechas relevantes |
| Mascota | Perro o gato del cliente | Entidad con fecha de cumpleaños |
| Motor de procesos (Flowable) | Ejecuta los procesos y gestiona las tareas | Fuente de la verdad del estado de cada proceso |
| Servicio de notificación | Correo u otro canal | Envía recordatorios o mensajes |

[Supuesto] Solo la emprendedora opera el sistema (un único rol de usuario).

## 4. Proceso 1 – Gestión de leads
- **Objetivo:** que ningún interesado quede sin seguimiento y medir cuántos se convierten en clientes.
- **Inicio:** llega una consulta por cualquier canal.
- **Fin:** lead **convertido** (pasa a ser cliente) o **no convertido** (con motivo).

**Estados del lead:** Nuevo → En seguimiento → Convertido / No convertido.

**Reglas de negocio:**
| ID | Regla | Estado |
|---|---|---|
| RN01 | Todo lead debe registrar nombre, un medio de contacto y canal de origen | Propuesta |
| RN02 | Si el lead no responde, se recontacta hasta 3 veces, cada 2 días | [Supuesto] |
| RN03 | Tras el último intento sin respuesta, el lead se cierra como "no convertido – sin respuesta" | [Supuesto] |
| RN04 | Un lead se considera convertido cuando confirma el pedido | [Supuesto] |
| RN05 | Al cerrar como no convertido se registra el motivo (precio, fecha, despacho, sin respuesta, otro) | Propuesta |
| RN06 | Al convertirse, se crea el cliente y se solicita el nombre y la fecha de cumpleaños de la mascota | Propuesta |

## 5. Proceso 2 – Oportunidades comerciales
- **Objetivo:** que no se pierda ninguna fecha relevante para recontactar clientes.
- **Inicio:** temporizador diario que revisa las fechas próximas.
- **Fin:** oportunidad **ganada** (hubo compra), **sin compra** o **descartada**.

**Fechas consideradas:** cumpleaños de mascota, Halloween (31-oct), Navidad (25-dic), Día del Perro, Día del Gato, aniversario del emprendimiento, lanzamientos y otras definidas por la emprendedora.

**Estados de la oportunidad:** Generada → Contactada → Ganada / Sin compra / Descartada.

**Reglas de negocio:**
| ID | Regla | Estado |
|---|---|---|
| RN07 | Se genera una oportunidad por cliente cuando falten N días para una fecha relevante | [Supuesto] N = 7 para cumpleaños, 21 para campañas |
| RN08 | La emprendedora puede crear, editar y desactivar fechas y campañas | Propuesta |
| RN09 | Una campaña puede dirigirse a todos los clientes o a un segmento (perro/gato) | [Supuesto] |
| RN10 | El sistema avisa a la emprendedora; el contacto con el cliente lo hace ella personalmente | [Supuesto] (el caso indica que no se busca automatizar la venta) |
| RN11 | Solo se contacta a clientes que aceptaron recibir comunicaciones | Propuesta (Ley 19.628 de datos personales) |

## 6. Requerimientos funcionales
| ID | Requerimiento | Proceso | Prioridad MVP | Entrega estimada |
|---|---|---|---|---|
| RF01 | Registrar un lead (datos, canal, producto de interés) | P1 | Alta | 1–2 |
| RF02 | Ver bandeja de leads pendientes de seguimiento | P1 | Alta | 3 |
| RF03 | Registrar cada acción de seguimiento | P1 | Alta | 2–3 |
| RF04 | Recordar automáticamente el seguimiento pendiente | P1 | Media | 3 |
| RF05 | Cerrar un lead como convertido o no convertido (con motivo) | P1 | Alta | 1–2 |
| RF06 | Registrar cliente y mascota(s) con fecha de cumpleaños | P1/P2 | Alta | 2 |
| RF07 | Administrar fechas y campañas | P2 | Alta | 3 |
| RF08 | Generar oportunidades automáticamente según las fechas | P2 | Alta | 3 |
| RF09 | Bandeja de oportunidades por contactar | P2 | Alta | 3 |
| RF10 | Enviar notificación por correo (a la emprendedora o al cliente) | P2 | Media | 2–3 |
| RF11 | Reporte de conversión por canal | P1 | Media | Examen |
| RF12 | Reporte de oportunidades generadas y ganadas | P2 | Baja | Examen |

## 7. Requerimientos no funcionales
| ID | Requerimiento |
|---|---|
| RNF01 | Procesos ejecutados en Flowable OSS 7.2 (motor como fuente de la verdad) |
| RNF02 | Servicios web en Java 21 (Jakarta REST o SOAP) |
| RNF03 | Interfaz simple, usable desde celular [Supuesto] |
| RNF04 | Persistencia en base de datos relacional |
| RNF05 | Protección de datos personales y registro de consentimiento |
| RNF06 | Instalable en servidor siguiendo una guía documentada |

## 8. Modelo de datos preliminar
| Entidad | Atributos principales |
|---|---|
| Lead | id, nombre, teléfono, correo, canal, productoInteres, fechaRequerida, estado, motivoNoCompra, fechaRegistro, processInstanceId |
| Seguimiento | id, leadId, fecha, medio, comentario, resultado |
| Cliente | id, nombre, teléfono, correo, comuna, aceptaComunicaciones, fechaRegistro, leadOrigenId |
| Mascota | id, clienteId, nombre, especie (perro/gato), fechaNacimiento, preferencias |
| FechaRelevante | id, nombre, tipo (cumpleaños/campaña), fecha, diasAnticipacion, segmento, activa |
| Oportunidad | id, clienteId, fechaRelevanteId, estado, fechaGeneracion, resultado, processInstanceId |

## 9. Indicadores
- Leads registrados por canal y por mes.
- Tasa de conversión = leads convertidos / leads cerrados (total y por canal).
- Motivos más frecuentes de no compra.
- Oportunidades generadas, contactadas y ganadas por campaña.

## 10. Alcance del MVP
**Incluye:** registro manual de leads, seguimiento con recordatorios, cierre con resultado, registro de clientes y mascotas, administración de fechas, generación de oportunidades y aviso por correo.

**No incluye (por ahora):**
- Integración automática con Instagram, Facebook, TikTok o WhatsApp (sus APIs requieren aprobación de Meta y cuentas empresariales).
- Pagos en línea, inventario o integración con la tienda Sumer Labs.
- Envío masivo de marketing.

## 11. Decisiones preliminares
| Decisión | Justificación |
|---|---|
| Registro manual de leads | Evita depender de APIs de redes sociales; suficiente para el MVP |
| Notificaciones por correo mediante External Worker | Servicio simple y gratuito (SMTP o Brevo); cumple el requisito de integrar un servicio externo |
| Un proceso BPMN por problema (leads y oportunidades) | Separa responsabilidades; se conectan a través del cliente convertido |
| Flowable 7.2 | Material del curso y ejemplos del profesor basados en esa versión |

## 12. Riesgos
| Riesgo | Mitigación |
|---|---|
| Respuestas de la emprendedora tardan | Trabajar con supuestos documentados y ajustarlos después |
| Alcance demasiado grande | Priorizar el MVP; P1 primero, P2 después |
| Problemas instalando Flowable | Guía de instalación compartida; todos lo prueban temprano |
| Baja participación de algún integrante | Compromisos con responsable en cada acta |
