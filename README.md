# Cuidar Android

Se publica aquí el código fuente de la aplicación Cuidar para sistemas operativos Android. Este código fuente es el *trabajo en curso* hacia la versión 3.5.4. En el archivo [CHANGELOG.md](CHANGELOG.md) se puede ver el historial de cambios y las novedades que contendrá esta versión.

Esta aplicación es el método elegido por millones de ciudadanos y ciudadanas de Argentina para mostrar sus certificados de circulación en el contexto de la pandemia de COVID-19, así como también para hacerse autodiagnósticos que les permitan saber si deben consultar al sistema de salud y ser derivados al Comité Operativo de Emergencia Provincial de su jurisdicción.

![Aplicación Cuidar](/app/src/main/res/mipmap-xhdpi/ic_launcher.png)

La aplicación puede descargarse [aquí](https://www.argentina.gob.ar/aplicaciones/coronavirus).

**¿Tenés dudas, comentarios, o sugerencias? ¿Encontraste algo que te parece que deberíamos arreglar, cambiar o corregir?** Te escuchamos [aquí](https://www.argentina.gob.ar/aplicaciones/coronavirus/contanos-sobre-la-app-cuidar-covid-19).

# Aclaración

Esta aplicación utiliza para su autenticación la combinación de DNI y número de trámite. Ese mecanismo es imperfecto, pero no es viable reemplazarlo sin incurrir en "alternativas" que resultan más invasivas, más "pesadas" o inviables en un contexto de pandemia donde es necesario desplegar rápidamente una aplicación que utilicen millones de ciudadanos y ciudadanas con diversa experiencia en tecnología.

Para proteger a la ciudadanía de versiones que imposten a la aplicación oficial o, que atenten contra la privacidad de datos resguardados por la misma utilizando listados conseguidos ilegalmente, la versión aquí expuesta no incluye las claves de autenticación reales contra el backend.

# Permisos

* Cámara: Utilizado para escanear el DNI.

* Ubicación: En caso de autodiagnóstico positivo, se solicita por única vez la geolocalización para determinar el Consejo Operativo de Emergencia Provincial apropiado para derivar al ciudadano. La Argentina tiene un sistema de salud federal, lo que significa que cada provincia tiene sus propios mecanismos de atención de salud.

    * Si le usuarie deniega el permiso, la aplicación funciona con normalidad utilizando la dirección declarada previamente.

    * Si aún así, alguien no desea usar la aplicación, puede portar el certificado de circulación en papel.

* Conexión de red: para poder enviar y recibir datos. El uso de los datos móviles de la aplicación Cuidar no tiene costo.


# Requerimientos

* Sistema operativo: Android 5.0 (Lollipop) o superior.

# Compilación

Se puede compilar con Gradle o con Android Studio. Para usar Gradle es necesario tener 6.5.1 o superior. En ese caso se debe correr `gradle build`. El apk queda en `app/build/outputs/apk/opensource`.

## Firebase

Para poder compilar la app es necesario que crees un proyecto de prueba en Firebase ya que se utilizan servicios de mensajería para su funcionamiento. Los pasos a seguir son:

- Ingresar en https://console.firebase.google.com/ con una cuenta de Google.
- Creá un proyecto de prueba, bajá `google-services.json` y colocalo en `app/`.

Guía: https://firebase.google.com/docs/android/setup?hl=es

# Arquitectura

La aplicación utiliza el patrón de arquitectura [MVVM](https://es.wikipedia.org/wiki/Modelo%E2%80%93vista%E2%80%93modelo_de_vista) (Model-View-ViewModel).

# Historia

La aplicación fue desarrollada originalmente en conjunto entre la Secretaría de Innovación Pública -siguiendo los requerimientos y necesidades del Ministerio de Salud-, el Ministerio de Ciencia y Tecnología de la Nación, la Fundación Sadosky, el Consejo Nacional de Investigaciones Científicas y Técnicas (CONICET) y la Cámara de la Industria Argentina del Software (CESSI), que nucleó a las empresas Hexacta, Globant, G&L Group, C&S, QServices, GestiónIT, Intive, Finnegans y Faraday. Asimismo, el equipo se complementó con el trabajo de Arsat, la empresa de telecomunicaciones del Estado, y los servicios brindados por Amazon Web Services, RedHat Argentina, Thinkly y Biodyn SAS. Las empresas antes mencionadas ofrecieron sus servicios como donación al Estado Argentino. Hoy en día el mantenimiento y mejora de la aplicación es realizado por el Estado Nacional a través de la Secretaría de Innovación Pública.

# Próximos pasos

Más allá de las mejoras funcionales que se incorporen por requerimiento de las autoridades sanitarias, estamos trabajando en automatizar una parte de nuestra batería de tests, lo que va a requerir cierto refactoring de las vistas principales y facilitar futuras extensiones.

# Contribución

¿Querés contribuir con el proyecto? Dejanos tu PR en github. Por favor documentá claramente el objetivo del cambio y ayudanos a mantener la homogeneidad del código (estamos trabajando en hacerlo más homogéneo).
