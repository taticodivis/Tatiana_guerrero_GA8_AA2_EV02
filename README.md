# Codivis Móvil — GA8-220501096-AA2-EV02

**Tatiana Guerrero · SENA · Análisis y Desarrollo de Software**

Aplicación Android nativa en Java: registro, acceso, cierre de sesión y administración de clientes (crear, listar, buscar, editar, eliminar). Datos locales SQLite separados por cuenta. No requiere servidor ni conexión a la API web. No incluye usuarios ni datos de demostración.

## Archivos

- `apk/Codivis-Movil-GA8-AA2-EV02.apk`: instalador de depuración firmado para evaluación.
- `Desarrollo-Movil-GA8-AA2-EV02.pdf` y `.html`: requerimientos, diagramas, arquitectura, ambientes, pruebas e instrucciones.
- `app/src/main`: código fuente por capas y recursos.
- `app/src/test`: 37 pruebas de autenticación, clientes, SQLite, sesión e interfaz.
- `evidencias`: resultados XML, registro de construcción, firma y manifiesto del APK.
- `enlace-repositorio.txt`: URL del repositorio remoto.

## Abrir y construir

Abrir esta carpeta en Android Studio. Instalar SDK 35 y Build Tools 36.0.0. El proyecto incluye Gradle Wrapper 9.1.0 y AGP 9.0.1. Usar JDK compatible con Gradle 9.1 (se verificó con JDK 25); nivel de código Java 17. Android Studio debe generar `local.properties` con la ruta del SDK del equipo.

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
```

La primera ejecución requiere Internet. APK generado: `app/build/outputs/apk/debug/app-debug.apk`.

## Instalar y probar

Android 8.0 o superior. Copiar el APK al teléfono y abrirlo, o ejecutar `adb install -r apk/Codivis-Movil-GA8-AA2-EV02.apk` con depuración USB autorizada. Nombre visible: **Codivis Móvil**. Identificador: `com.codivis.crm.movil`, independiente del APK de EV01.

Registrar `pedro` con una contraseña propia de 8 a 128 caracteres. Iniciar sesión, crear un cliente, buscarlo, editarlo y comprobar que persiste al reabrir. Cerrar sesión, registrar `luis` y verificar que sus clientes están separados. Eliminar un cliente requiere confirmación.

Las 37 pruebas se ejecutaron en JVM con JUnit y Robolectric (API 28 para componentes Android), sin fallos. **La instalación y ejecución en teléfono sigue pendiente.** No se presentan maquetas como evidencia de ejecución. La revisión lint puede conservar recomendaciones de actualización/localización; consultar su informe.

La sesión persiste hasta cerrarla. Desinstalar o borrar los datos elimina cuentas y clientes locales. No hay sincronización, recuperación de contraseña ni servidor externo. No se incluyen claves privadas de firma.

## Entrega

La guía admite RAR o ZIP. Ejecutar `Empaquetar.ps1` con Rar.exe disponible para generar el RAR en esta misma carpeta. Se excluyen cachés, SDK, compilaciones intermedias, `.git`, rutas locales y otros archivos comprimidos. Repositorio: https://github.com/taticodivis/Tatiana_guerrero_GA8_AA2_EV02
