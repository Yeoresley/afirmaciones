# ASSI Afirmaciones v0.4 — código fuente

Proyecto Android Java con 12 imágenes POV, TTS y sesiones. No es una APK compilada.

## Compilar mediante GitHub Actions
1. Crear un repositorio privado en GitHub.
2. Subir el **contenido de esta carpeta** a la raíz del repositorio, incluyendo `.github/workflows/android-debug.yml`.
3. En GitHub, Actions → Build Android debug APK → Run workflow.
4. Descargar el artefacto `ASSI-Afirmaciones-debug` y extraer `app-debug.apk`.
5. Instalar en Android para probar. Es una compilación de desarrollo, no de producción.

## Cambios v0.4
- Contador basado en reloj monotónico (reduce deriva de temporizador).
- Pausa de voz y sesión al salir de la app.
- Guarda fecha/hora de la última sesión completada.
- Declaración de visibilidad del servicio TTS para Android 11+.
- Flujo de compilación reproducible en GitHub Actions.

## Pendientes de validación
- Compilación real, ejecución en dispositivo, pausas, TTS, restauración de imágenes y compatibilidad Android.
- Historial por día, notificaciones y voces de calidad.
- Sustituir las 9 imágenes de baja resolución por originales individuales.
