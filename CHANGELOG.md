Cuidar Android

Versión 3.5.2 -- en desarrollo
* Se agregan "splash screens" con consejos mientras se carga estado del server.
* Se utiliza Timber para mejorar el logging.
* Se termina de separar las claves con vistas a la publicación.
* Se mejora el aviso al usuario en caso de doble sesión.
* Agregada licencia GPLv3. ([#26](https://github.com/argob/cuidar-android/pull/26) by [@RodolfoGS](https://github.com/RodolfoGS))

Versión 3.5.0 -- 17/07/2020
Más importantes:
* Pantalla para bonaerenses.
* Mejora la encripción de la BD local.
* Se controla que los usuarios no estén sesionados en dos dispositivos simultáneamente.
* Se refuerza el mecanismo de autenticación contra el backend.

Menores:
* Nuevo spinner de provincias y localidades.
* Agrandar espacio para mensajes para derivados a salud.
* Cambio menor en el mensaje de notificación de COVID positivo.
* Cuando se recibe una notificación se actualiza al tocarla.

Versión 3.4.2 -- 02/07/2020
* Soluciona reglas de compilación proguard para release prod.

Versión 3.4.0 -- 01/07/2020
* Muestra el nro de SUBE y de patente que figura en el certificado.
* Corrige bug que pedía doble autodiagnóstico en algunos casos.
* Permite que se muestren tipos de actividades muy largas, generando más espacio si es necesario.
* Traducción de clases a Kotlin para manejar mejor los datos.
* Agrega antecedente de condición que baja las defensas.

Versión 3.3.1 -- 23/06/2020
* Hotfix: se arregla problema de QR muy chico para Android <8.

Versión 3.3.0 -- 20/06/2020
* Primera versión desarrollada internamente.
* Mensaje especial para ciudadanxs de CABA (información sobre UFUs).
* Cambios en el autodiagnóstico (default en NO, permite corregir, muestra resumen).
* Se empiezan a separar las claves del código con vistas a la publicación.
* Se incluye mecanismo de autenticación contra el backend para evitar abusos.

Versión 3.0.5 -- 27/04/2020
* Versión Android nativa publicada.
