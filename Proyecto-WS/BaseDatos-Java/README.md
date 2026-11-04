\# Base de Datos - Peluditos Pet Chile



\## 1. Descripción del proyecto



Este módulo corresponde al desarrollo de la base de datos del proyecto \*\*Peluditos Pet Chile\*\*, cuyo objetivo es gestionar leads, seguimientos, clientes, mascotas, fechas relevantes y oportunidades comerciales.



La base de datos permite organizar la información y establecer relaciones entre las distintas entidades del proyecto.



\## 2. Tecnologías utilizadas



\- Microsoft SQL Server.

\- SQL Server Management Studio 20 (SSMS 20).

\- Lenguaje SQL.



\## 3. Nombre de la base de datos



`DB\_PeluditosPetChile`



\## 4. Tablas creadas



La base de datos contiene seis tablas:



| Tabla | Descripción |

|---|---|

| Leads | Almacena los posibles clientes y sus intereses. |

| Seguimientos | Registra los contactos realizados a los leads. |

| Clientes | Almacena la información de los clientes. |

| Mascotas | Registra las mascotas asociadas a cada cliente. |

| FechasRelevantes | Almacena fechas importantes para campañas comerciales. |

| OportunidadesComerciales | Registra oportunidades comerciales asociadas a clientes y fechas relevantes. |



\## 5. Relaciones entre las tablas



Se establecieron las siguientes relaciones mediante claves foráneas:



\- Seguimientos con Leads.

\- Clientes con Leads.

\- Mascotas con Clientes.

\- OportunidadesComerciales con Clientes.

\- OportunidadesComerciales con FechasRelevantes.

\- OportunidadesComerciales con Mascotas.



\## 6. Índices y restricciones



Se incorporaron índices para apoyar las consultas y búsquedas de información.



También se definieron:



\- Claves primarias (PRIMARY KEY).

\- Claves foráneas (FOREIGN KEY).

\- Restricciones CHECK.

\- Valores predeterminados (DEFAULT).

\- Un índice único filtrado para Lead de origen en Clientes.



\## 7. Organización de los archivos



```text

BaseDatos/

├── README.md

├── Scripts/

│   ├── 01\_Creacion\_DB\_PeluditosPetChile.sql

│   ├── 02\_Inserciones\_Prueba.sql

│   └── 03\_Consultas\_Verificacion.sql

└── Evidencias/

```



\## 8. Scripts SQL



\- \*\*01\_Creacion\_DB\_PeluditosPetChile.sql:\*\* contiene la creación de la base de datos, tablas, relaciones, restricciones e índices.

\- \*\*02\_Inserciones\_Prueba.sql:\*\* realiza inserciones de prueba para comprobar las relaciones entre las tablas. Finaliza con una reversión de la transacción (ROLLBACK).

\- \*\*03\_Consultas\_Verificacion.sql:\*\* contiene consultas para verificar tablas, columnas, relaciones, índices, restricciones y cantidad de registros.



\## 9. Pruebas realizadas



Se realizaron pruebas para comprobar:



\- La creación de las seis tablas.

\- La existencia de las relaciones entre las tablas.

\- La creación de los índices.

\- La correcta definición de columnas y tipos de datos.

\- La existencia de restricciones.

\- La inserción de datos ficticios y la consulta de información relacionada.

\- La reversión de los datos de prueba mediante ROLLBACK.



\## 10. Estado del desarrollo



La estructura de la base de datos fue creada y se realizaron pruebas de verificación en SQL Server Management Studio 20.



La integración de esta base de datos con los demás componentes del proyecto deberá realizarse y validarse como una etapa posterior.

