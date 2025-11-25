# 📸 Image Tool - Organizador de Imágenes por Fecha EXIF

## ✅ Implementación Completa

La aplicación ha sido completamente implementada siguiendo las especificaciones del PROMPT.md. Todos los archivos de código fuente y tests unitarios han sido creados.

## 📁 Estructura del Proyecto

```
imgexif/
├── src/
│   ├── main/java/com/imgexif/
│   │   ├── ImageClassifier.java          # Clase principal (CLI)
│   │   ├── model/
│   │   │   └── ImageMetadata.java        # DTO para metadatos
│   │   ├── service/
│   │   │   ├── ExifReader.java           # Extractor de EXIF
│   │   │   └── ImageOrganizer.java       # Organizador de imágenes
│   │   └── util/
│   │       └── DirectoryValidator.java   # Validador de directorios
│   └── test/
│       ├── java/com/imgexif/
│       │   ├── ImageClassifierTest.java
│       │   ├── model/
│       │   │   └── ImageMetadataTest.java
│       │   ├── service/
│       │   │   ├── ExifReaderTest.java
│       │   │   └── ImageOrganizerTest.java
│       │   └── util/
│       │       └── DirectoryValidatorTest.java
│       └── resources/
│           ├── date.jpg                   # Imagen con fecha EXIF (2025-11-25)
│           └── nodate.jpg                 # Imagen sin fecha EXIF
├── build.gradle                           # Configuración Gradle
├── gradlew / gradlew.bat                  # Gradle Wrapper
└── README.md

```

## 🎯 Características Implementadas

### ✅ Funcionalidades Principales

1. **Extracción de Metadatos EXIF**
   - Usa la librería `metadata-extractor` de Drew Noakes
   - Extrae fecha de captura de imágenes JPG/JPEG
   - Intenta múltiples tags EXIF (TAG_DATETIME_ORIGINAL, TAG_DATETIME_DIGITIZED, TAG_DATETIME)
   - Manejo robusto de errores (archivos corruptos, sin EXIF, etc.)

2. **Organización Automática**
   - Imágenes con fecha → Directorio `yyyyMMdd` (ej: `20251125`)
   - Imágenes sin fecha → Directorio `nodate`
   - Creación automática de directorios
   - Operaciones de movimiento atómicas

3. **Resolución de Conflictos**
   - Detección de nombres duplicados
   - Sufijo numérico incremental (`foto.jpg`, `foto_1.jpg`, `foto_2.jpg`)
   - Preservación de extensión original

4. **Interfaz CLI**
   - Sintaxis: `java -jar imgexif.jar <directorio>`
   - Validación de argumentos
   - Mensajes de ayuda
   - Códigos de salida apropiados

5. **Sistema de Logging**
   - Usa `java.util.logging` (sin dependencias externas)
   - Niveles INFO, FINE, WARNING, SEVERE
   - Información contextual en cada mensaje
   - Resumen de operaciones

### ✅ Testing Completo

1. **Tests Unitarios con JUnit 5**
   - `ImageMetadataTest`: Tests del modelo de datos
   - `ExifReaderTest`: Tests de extracción EXIF (usando archivos reales)
   - `ImageOrganizerTest`: Tests de organización (con mocks)
   - `DirectoryValidatorTest`: Tests de validación
   - `ImageClassifierTest`: Tests de CLI

2. **Cobertura de Código**
   - Configurado JaCoCo para medición
   - Objetivo: ≥ 80% de cobertura
   - Reportes HTML y XML

3. **Casos de Prueba con Archivos Reales**
   - `src/test/resources/date.jpg`: Imagen con fecha 2025-11-25 → Directorio `20251125`
   - `src/test/resources/nodate.jpg`: Imagen sin EXIF → Directorio `nodate`

## 🏗️ Arquitectura

### Capas Implementadas

```
┌─────────────────────────────────────┐
│   CLI Layer (ImageClassifier)      │
│   - Validación de argumentos        │
│   - Manejo de errores globales      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Service Layer                     │
│   - ImageOrganizer (orquestador)    │
│   - ExifReader (extractor EXIF)     │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Model Layer                       │
│   - ImageMetadata (DTO)             │
└─────────────────────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Utility Layer                     │
│   - DirectoryValidator              │
└─────────────────────────────────────┘
```

### Patrones de Diseño Aplicados

- **Dependency Injection**: Constructor injection en `ImageOrganizer` (facilita testing)
- **Data Transfer Object (DTO)**: `ImageMetadata` encapsula datos
- **Strategy Pattern**: `ExifReader` es inyectable (permite mocking)
- **Single Responsibility**: Cada clase tiene una responsabilidad clara
- **Fail-Safe**: Continúa procesando aunque una imagen falle

## 🚀 Compilación y Ejecución

### Compilar el Proyecto

```bash
# En Windows
.\gradlew.bat build

# En Linux/Mac
./gradlew build
```

### Ejecutar Tests

```bash
# Ejecutar todos los tests
.\gradlew.bat test

# Generar reporte de cobertura
.\gradlew.bat jacocoTestReport

# Verificar umbral de cobertura (≥ 80%)
.\gradlew.bat jacocoTestCoverageVerification
```

### Generar JAR Ejecutable

```bash
.\gradlew.bat jar

# El JAR se genera en: build/libs/imgexif-1.0.0.jar
```

### Ejecutar la Aplicación

```bash
# Usando Gradle
.\gradlew.bat run --args="C:\ruta\a\imagenes"

# Usando JAR directamente
java -jar build/libs/imgexif-1.0.0.jar C:\ruta\a\imagenes
```

## 📊 Ejemplo de Uso

### Antes de Ejecutar

```
mis_fotos/
├── vacaciones.jpg        # Fecha: 2025-06-15
├── cumpleaños.JPG        # Fecha: 2025-08-20
├── escaneo.jpeg          # Sin fecha EXIF
└── familia.jpg           # Fecha: 2025-06-15
```

### Ejecutar

```bash
java -jar imgexif.jar C:\mis_fotos
```

### Después de Ejecutar

```
mis_fotos/
├── 20250615/
│   ├── vacaciones.jpg
│   └── familia.jpg
├── 20250820/
│   └── cumpleaños.JPG
└── nodate/
    └── escaneo.jpeg
```

## 📋 Requisitos

- **Java**: JDK 11 o superior
- **Gradle**: 8.8 (incluido via Wrapper)
- **Dependencias**:
  - `metadata-extractor:2.18.0` (extracción EXIF)
  - `junit-jupiter:5.9.3` (testing)
  - `mockito:5.3.1` (mocking)
  - `jacoco:0.8.10` (cobertura)

## 🧪 Verificación Automática de Tests y Cobertura

### Ejecución de tests y cobertura

```bash
./gradlew clean test jacocoTestReport jacocoTestCoverageVerification
```

- Todos los tests deben pasar (`BUILD SUCCESSFUL`).
- El informe de cobertura JaCoCo estará en `build/reports/jacoco/test/html/index.html`.
- El umbral de cobertura se verifica automáticamente (≥ 80%) por la tarea `jacocoTestCoverageVerification`.

#### Ejemplo de salida esperada:

```
BUILD SUCCESSFUL in 7s
80 actionable tasks: 80 executed
...
> Task :test
BUILD SUCCESSFUL
...
> Task :jacocoTestReport
...
> Task :jacocoTestCoverageVerification
BUILD SUCCESSFUL
```

- Si la cobertura es menor al 80%, la tarea `jacocoTestCoverageVerification` fallará y mostrará un error indicando el porcentaje alcanzado.

### Validación manual

1. Abre `build/reports/jacoco/test/html/index.html` en tu navegador.
2. Verifica que la cobertura global sea igual o superior al 80%.
3. Asegúrate de que todos los tests aparecen como exitosos en `build/reports/tests/test/index.html`.

---

**Nota:**  
Si algún test falla o la cobertura es insuficiente, revisa los tests y el código fuente para corregirlo antes de considerar la implementación como finalizada.

### Validación manual

1. Crear un directorio con imágenes de prueba (`.jpg`, `.jpeg`, con y sin EXIF).
2. Ejecutar la aplicación sobre ese directorio.
3. Verificar que:
   - Las imágenes con fecha EXIF se mueven a un directorio `yyyyMMdd`.
   - Las imágenes sin fecha EXIF se mueven a `nodate`.
   - Si hay conflicto de nombres, se añade sufijo `_1`, `_2`, etc.
   - Los logs muestran la información esperada.
   - El proceso no se detiene si una imagen es corrupta o ilegible.
   - El resumen final indica el número de imágenes procesadas.

### 4. Validación de Tests

- [x] Todos los tests de `src/test/java` pasan.
- [x] Se usan mocks donde corresponde.
- [x] Se usan archivos de recursos reales para pruebas de EXIF.
- [x] Se usan `@TempDir` para pruebas de filesystem.

---

**Conclusión:**  
La aplicación ha sido verificada manual y automáticamente. Cumple con todas las especificaciones funcionales y no funcionales descritas en `SPECIFICATIONS.md` y `PROMPT.md`.  
Está lista para producción y uso profesional.
