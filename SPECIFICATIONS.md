# Especificaciones Técnicas del Proyecto - Image Tool

## 📋 Prompt Original Mejorado

### Objetivo del Proyecto
Desarrollar una aplicación Java empresarial para la clasificación automática de archivos de imagen JPG/JPEG basándose en metadatos EXIF, con enfoque en calidad, mantenibilidad y testing.

### Requisitos Funcionales

#### RF-001: Compatibilidad de Plataforma
- **Descripción**: La aplicación debe ser compatible con Java 11 (LTS)
- **Justificación**: Java 11 es una versión LTS ampliamente soportada en entornos empresariales
- **Criterios de aceptación**:
  - Código compilable y ejecutable en JDK 11+
  - No uso de características exclusivas de versiones superiores

#### RF-002: Sistema de Construcción
- **Descripción**: Utilizar Gradle como herramienta de build automation
- **Justificación**: Gradle ofrece flexibilidad, performance y ecosistema robusto
- **Criterios de aceptación**:
  - Archivo `build.gradle` correctamente configurado
  - Gradle Wrapper incluido para reproducibilidad
  - Builds determinísticos y reproducibles

#### RF-003: Interfaz de Línea de Comandos
- **Descripción**: Aplicación ejecutable desde terminal/consola
- **Sintaxis**: `java -jar imgexif.jar <directorio>`
- **Criterios de aceptación**:
  - Recibe un argumento: ruta del directorio
  - Valida argumentos de entrada
  - Retorna código de salida apropiado (0 = éxito, 1 = error)
  - Muestra mensajes de ayuda si se ejecuta sin argumentos

#### RF-004: Sistema de Logging
- **Descripción**: Implementar logging usando `java.util.logging` (estándar)
- **Justificación**: No agregar dependencias externas innecesarias
- **Niveles requeridos**:
  - **INFO**: Operaciones principales y resumen de ejecución
  - **FINE/FINER/FINEST**: Detalles de procesamiento para debugging
  - **WARNING**: Problemas no críticos (archivos sin EXIF, etc.)
  - **SEVERE**: Errores críticos que impiden la ejecución
- **Criterios de aceptación**:
  - Logs en consola en tiempo real
  - Información contextual en cada mensaje (nombre archivo, etc.)
  - Sin dependencias de frameworks externos (log4j, slf4j, etc.)

#### RF-005: Testing con JUnit 5
- **Descripción**: Suite completa de tests unitarios usando JUnit 5 (Jupiter)
- **Objetivo de cobertura**: Mínimo 80% de cobertura de código
- **Tipos de tests requeridos**:
  - Tests unitarios para cada clase
  - Tests de integración para flujos completos
  - Tests parametrizados para múltiples escenarios
  - Tests con mocks (usando Mockito)
- **Criterios de aceptación**:
  - Todos los tests pasan (`gradle test`)
  - Cobertura ≥ 80% medida con JaCoCo
  - Tests rápidos (< 30 segundos total)
  - Tests independientes y reproducibles

#### RF-006: Análisis de Cobertura con JaCoCo
- **Descripción**: Configurar plugin JaCoCo para medir cobertura de tests
- **Reportes requeridos**:
  - Reporte HTML para visualización
  - Reporte XML para integración CI/CD
- **Umbral mínimo**: 80% de cobertura
- **Criterios de aceptación**:
  - Comando `gradle jacocoTestReport` genera reportes
  - Comando `gradle jacocoTestCoverageVerification` valida umbral
  - Reportes accesibles en `build/reports/jacoco/`

#### RF-007: Lectura de Metadatos EXIF
- **Descripción**: Extraer información EXIF de archivos JPG/JPEG
- **Librería**: metadata-extractor de Drew Noakes
- **Justificación**: Librería robusta, activamente mantenida, ampliamente usada
- **Metadatos a extraer**:
  - Fecha de captura original (TAG_DATETIME_ORIGINAL)
  - Fecha de digitalización (TAG_DATETIME_DIGITIZED) - fallback
  - Fecha genérica (TAG_DATETIME) - fallback secundario
- **Criterios de aceptación**:
  - Extracción correcta de fecha cuando existe
  - Manejo graceful cuando no hay EXIF
  - Soporte para diferentes formatos de fecha EXIF
  - No crashear con archivos corruptos

#### RF-008: Clasificación por Fecha de Captura
- **Descripción**: Organizar imágenes en directorios según fecha EXIF
- **Formato de directorio**: `yyyyMMdd` (ISO 8601 simplificado)
  - Ejemplos: `20231115`, `20240101`, `19990725`
- **Justificación del formato**:
  - Ordenamiento alfabético = ordenamiento cronológico
  - Compatible con sistemas de archivos (sin caracteres especiales)
  - Formato internacional sin ambigüedades
- **Operación**: **Mover** (no copiar) archivo al directorio destino
- **Criterios de aceptación**:
  - Directorio creado automáticamente si no existe
  - Archivo movido correctamente
  - Permisos y atributos preservados
  - Operación atómica (sin corrupción en caso de fallo)

#### RF-009: Manejo de Imágenes sin Fecha
- **Descripción**: Clasificar imágenes sin metadatos EXIF en directorio especial
- **Directorio destino**: `nodate`
- **Casos de uso**:
  - JPG sin metadatos EXIF
  - EXIF presente pero sin fecha de captura
  - Archivos corruptos/ilegibles
  - Imágenes escaneadas sin datos EXIF
- **Criterios de aceptación**:
  - Directorio `nodate` creado automáticamente
  - Todas las imágenes sin fecha movidas ahí
  - Log de warning para cada caso

#### RF-010: Soporte Multi-Extensión
- **Descripción**: Procesar archivos con diferentes variaciones de extensión JPG
- **Extensiones soportadas** (case-insensitive):
  - `.jpg`, `.JPG`, `.Jpg`, `.JpG`, etc.
  - `.jpeg`, `.JPEG`, `.Jpeg`, `.JpEg`, etc.
- **Justificación**: Diferentes cámaras y sistemas operativos usan diferentes convenciones
- **Criterios de aceptación**:
  - Detección case-insensitive de extensiones
  - Procesamiento correcto independiente de mayúsculas/minúsculas
  - Otros archivos (txt, png, etc.) ignorados

#### RF-011: Resolución de Conflictos de Nombres
- **Descripción**: Manejar casos donde el archivo destino ya existe
- **Estrategia**: Sufijo numérico incremental
- **Ejemplo**:
  - Original: `foto.jpg`
  - Si existe: `foto_1.jpg`
  - Si existe: `foto_2.jpg`
  - Y así sucesivamente...
- **Criterios de aceptación**:
  - No sobrescribir archivos existentes
  - Sufijo numérico consecutivo
  - Preservar extensión original

### Requisitos No Funcionales

#### RNF-001: Rendimiento
- Procesamiento de imágenes individuales: < 100ms por imagen (promedio)
- Soporte para directorios grandes: 10,000+ imágenes
- Uso de memoria eficiente: streaming de listado de archivos

#### RNF-002: Confiabilidad
- Manejo robusto de errores
- Continuar procesando aunque una imagen falle
- Operaciones atómicas de movimiento de archivos
- Sin pérdida de datos en caso de fallo

#### RNF-003: Mantenibilidad
- Código limpio y bien estructurado
- Documentación JavaDoc en clases y métodos públicos
- Arquitectura en capas separadas
- Principios SOLID aplicados
- Bajo acoplamiento, alta cohesión

#### RNF-004: Usabilidad
- Mensajes de error claros y accionables
- Progreso visible en logs
- Resumen al finalizar (cantidad de imágenes procesadas)
- Validación de entrada con mensajes descriptivos

#### RNF-005: Portabilidad
- Funcionamiento en Windows, Linux y macOS
- Sin dependencias de sistema operativo específicas
- Uso de `Path` y `Files` (NIO.2) para compatibilidad multiplataforma

#### RNF-006: Encoding
- Todos los ficheros fuentes deben estar codificados en UTF-8 sin BOM


### Arquitectura Propuesta

#### Capas

1. **Capa de Presentación (CLI)**
   - `ImageClassifier.java`: Clase main con validación de argumentos

2. **Capa de Servicio (Lógica de Negocio)**
   - `ImageOrganizer.java`: Orquestador principal de clasificación
   - `ExifReader.java`: Extractor de metadatos EXIF

3. **Capa de Modelo**
   - `ImageMetadata.java`: Objeto de transferencia de datos (DTO)

4. **Capa de Utilidades**
   - `DirectoryValidator.java`: Validaciones de directorios

#### Flujo de Ejecución

```
1. ImageClassifier.main()
   ├─> Validar argumentos CLI
   ├─> DirectoryValidator.validateSourceDirectory()
   ├─> new ImageOrganizer(sourcePath)
   └─> ImageOrganizer.organizeImages()
       ├─> Files.list() -> Stream de Paths
       ├─> Para cada archivo JPG/JPEG:
       │   ├─> ExifReader.extractMetadata()
       │   │   └─> Retorna ImageMetadata
       │   ├─> Determinar directorio destino
       │   │   ├─> Si tiene fecha: "yyyyMMdd"
       │   │   └─> Si no: "nodate"
       │   ├─> Crear directorio si no existe
       │   ├─> Resolver conflictos de nombre
       │   └─> Files.move(ATOMIC_MOVE)
       └─> Retornar cantidad procesada
```

#### Patrones de Diseño Aplicados

- **Strategy Pattern**: `ExifReader` puede ser inyectado (testability)
- **Builder Pattern**: Construcción de objetos complejos si necesario
- **Template Method**: Estructura de procesamiento de imágenes
- **Dependency Injection**: Constructor injection para testing

### Consideraciones Técnicas

#### Manejo de Excepciones

```java
try {
    // Operación principal
} catch (IllegalArgumentException e) {
    // Error de validación de entrada
    LOGGER.severe(...);
    System.exit(1);
} catch (IOException e) {
    // Error de I/O
    LOGGER.severe(...);
    System.exit(1);
} catch (Exception e) {
    // Error inesperado
    LOGGER.severe(...);
    System.exit(1);
}
```

#### Configuración de Gradle

```groovy
plugins {
    id 'java'
    id 'application'
    id 'jacoco'
}

sourceCompatibility = '11'
targetCompatibility = '11'

dependencies {
    implementation 'com.drewnoakes:metadata-extractor:2.18.0'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.9.3'
    testImplementation 'org.mockito:mockito-core:5.3.1'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.3.1'
}

test {
    useJUnitPlatform()
    finalizedBy jacocoTestReport
}

jacoco {
    toolVersion = "0.8.10"
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80
            }
        }
    }
}
```

### 🔧 Configuración de Proxy para Gradle

Si el entorno requiere proxy para acceder a repositorios, añade en `gradle.properties`:

```properties
systemProp.proxySet=true
systemProp.http.keepAlive=true
systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=8999
systemProp.http.proxyUser=
systemProp.http.proxyPassword=
#systemProp.http.nonProxyHosts=local.net|some.host.com
systemProp.https.keepAlive=true
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=8999
systemProp.https.proxyUser=
systemProp.https.proxyPassword=
#systemProp.https.nonProxyHosts=local.net|some.host.com
```

### Casos de Prueba Esenciales

#### Ejemplos de Test con Archivos Reales

Los siguientes archivos de prueba están disponibles en `src/test/resources/` para validar el comportamiento de la aplicación:

1. **Ejemplo 1: Imagen con Fecha EXIF**
   - **Archivo**: `src/test/resources/date.jpg`
   - **Metadatos EXIF**: Contiene fecha de captura del 25 de noviembre de 2025
   - **Comportamiento esperado**:
     - El archivo debe moverse al directorio `20251125`
     - El directorio `20251125` debe crearse automáticamente si no existe
     - Se debe registrar en el log la fecha extraída exitosamente
   - **Test unitario asociado**: Verificar que `ExifReader.extractMetadata()` retorna fecha correcta y que `ImageOrganizer.organizeImages()` mueve el archivo al directorio apropiado

2. **Ejemplo 2: Imagen sin Fecha EXIF**
   - **Archivo**: `src/test/resources/nodate.jpg`
   - **Metadatos EXIF**: No contiene información de fecha de captura
   - **Comportamiento esperado**:
     - El archivo debe moverse al directorio `nodate`
     - El directorio `nodate` debe crearse automáticamente si no existe
     - Se debe registrar un warning en el log indicando ausencia de fecha
   - **Test unitario asociado**: Verificar que `ExifReader.extractMetadata()` retorna null para la fecha y que `ImageOrganizer.organizeImages()` mueve el archivo al directorio `nodate`

#### Tests de ImageOrganizer

1. **Test: organizeImages_WithCaptureDate**
   - Setup: 2 imágenes con fecha EXIF (incluyendo `date.jpg`)
   - Acción: organizeImages()
   - Verificación: Directorio "yyyyMMdd" creado, imágenes movidas

2. **Test: organizeImages_WithoutCaptureDate**
   - Setup: 1 imagen sin EXIF (incluyendo `nodate.jpg`)
   - Acción: organizeImages()
   - Verificación: Directorio "nodate" creado, imagen movida

3. **Test: organizeImages_FileNameConflict**
   - Setup: Archivo con nombre duplicado en destino
   - Acción: organizeImages()
   - Verificación: Nuevo archivo tiene sufijo "_1"

4. **Test: organizeImages_MixedDates**
   - Setup: Múltiples imágenes con diferentes fechas
   - Acción: organizeImages()
   - Verificación: Múltiples directorios creados correctamente

5. **Test: organizeImages_IgnoresNonJpgFiles**
   - Setup: Directorio con JPG y otros archivos
   - Acción: organizeImages()
   - Verificación: Solo JPG procesados

#### Tests de ExifReader

1. **Test: extractMetadata_WithExifDate**
   - Input: JPG con EXIF válido (usar `src/test/resources/date.jpg`)
   - Output: ImageMetadata con fecha 2025-11-25
   - Verificación: Fecha extraída debe ser 25 de noviembre de 2025

2. **Test: extractMetadata_WithoutExifDate**
   - Input: JPG sin EXIF (usar `src/test/resources/nodate.jpg`)
   - Output: ImageMetadata sin fecha (null)
   - Verificación: captureDate debe ser null

3. **Test: extractMetadata_CorruptedFile**
   - Input: Archivo corrupto
   - Output: ImageMetadata sin fecha, sin exception
   - Verificación: Manejo graceful sin crash

#### Tests de DirectoryValidator

1. **Test: validateSourceDirectory_ValidDirectory**
   - Input: Directorio válido
   - Output: No exception

2. **Test: validateSourceDirectory_NonExistent**
   - Input: Directorio inexistente
   - Output: IllegalArgumentException

3. **Test: validateSourceDirectory_NotWritable**
   - Input: Directorio sin permisos de escritura
   - Output: IllegalArgumentException

### Métricas de Calidad

| Métrica | Objetivo | Medición |
|---------|----------|----------|
| Cobertura de tests | ≥ 80% | JaCoCo |
| Complejidad ciclomática | ≤ 10 por método | Análisis estático |
| Líneas por método | ≤ 50 | Revisión manual |
| Clases acopladas | ≤ 5 por clase | Análisis estático |
| Duplicación de código | < 3% | Análisis estático |

### Entregables

1. ✅ Código fuente completo en `src/main/java/`
2. ✅ Tests unitarios en `src/test/java/`
3. ✅ Archivo `build.gradle` configurado
4. ✅ Gradle Wrapper incluido
5. ✅ README.md con documentación completa
6. ✅ `.gitignore` configurado
7. ✅ Reporte de cobertura JaCoCo
8. ✅ JAR ejecutable en `build/libs/`

### Roadmap Futuro (Fuera del Alcance Actual)

Características que podrían agregarse en versiones futuras:

- 📸 Soporte para otros formatos (PNG, TIFF, RAW)
- 🔄 Modo recursivo (procesar subdirectorios)
- 📊 Estadísticas detalladas (histograma de fechas)
- 🎨 Organización por otros criterios (cámara, ubicación GPS)
- 💾 Modo copia en lugar de mover
- 🔍 Preview/dry-run antes de ejecutar
- 🌐 Interfaz web o GUI
- ⚙️ Archivo de configuración (YAML/Properties)
- 🔄 Undo/rollback de operaciones
- 📦 Integración con servicios cloud

---

**Documento de Especificaciones v1.0**
*Última actualización: 2025-11-25*
