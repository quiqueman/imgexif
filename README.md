# Image Tool - Clasificador Automático de Imágenes JPG

[![Java](https://img.shields.io/badge/Java-11%2B-blue.svg)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/Gradle-8.8-brightgreen.svg)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-Open%20Source-green.svg)]()

Aplicación Java profesional para clasificar y organizar automáticamente archivos JPG/JPEG basándose en sus metadatos EXIF. Ideal para fotógrafos, archivistas digitales y cualquier persona que necesite organizar grandes colecciones de fotografías.

## 🎯 Características Principales

- ✅ **Compatible con Java 11**: Código moderno y mantenible
- 🏗️ **Build con Gradle**: Sistema de construcción robusto y reproducible
- 💻 **Interfaz CLI**: Ejecución simple desde línea de comandos
- 📊 **Logging Integrado**: Sistema de logging estándar de Java para trazabilidad
- 🧪 **Tests Unitarios**: Suite completa con JUnit 5 y Mockito
- 📈 **Análisis de Cobertura**: Plugin JaCoCo configurado (objetivo: 80%+)
- 📸 **Lectura EXIF**: Extracción profesional de metadatos usando metadata-extractor
- 📁 **Organización Automática**: Clasificación por fecha de captura con formato `yyyyMMdd`
- 🔄 **Gestión de Conflictos**: Resolución inteligente de nombres duplicados
- 🎨 **Extensiones Flexibles**: Soporta `.jpg`, `.jpeg`, `.JPG`, `.JPEG`, etc.

## 📋 Requisitos del Sistema

- **Java**: JDK 11 o superior
- **Gradle**: 7.0+ (incluido Gradle Wrapper - no requiere instalación)
- **Sistema Operativo**: Windows, Linux, macOS
- **Permisos**: Lectura y escritura en el directorio de trabajo

## 🚀 Inicio Rápido

### 1. Clonar o Descargar el Proyecto

```bash
cd C:\prj\ia\imgtool
```

### 2. Construir el Proyecto

**En Linux/macOS:**
```bash
./gradlew build
```

**En Windows:**
```powershell
gradlew.bat build
```

### 3. Ejecutar la Aplicación

**Usando Gradle:**
```bash
./gradlew run --args="C:\ruta\a\mis\fotos"
```

**Usando el JAR generado:**
```bash
java -jar build/libs/imgtool-1.0.0.jar C:\ruta\a\mis\fotos
```

## 📦 Construcción y Testing

### Compilar el Proyecto
```bash
gradle build
```

### Ejecutar Tests Unitarios
```bash
gradle test
```

### Generar Reporte de Cobertura
```bash
gradle jacocoTestReport
```

El reporte HTML estará disponible en:
```
build/reports/jacoco/test/html/index.html
```

### Verificar Cobertura Mínima (80%)
```bash
gradle jacocoTestCoverageVerification
```

### Limpiar Build
```bash
gradle clean
```

## 💡 Uso Detallado

### Sintaxis Básica

```bash
java -jar imgtool-1.0.0.jar <directorio_origen>
```

**Parámetros:**
- `<directorio_origen>`: Ruta al directorio que contiene las imágenes JPG a clasificar

### Comportamiento de la Aplicación

La aplicación procesa todos los archivos JPG/JPEG en el directorio especificado:

#### ✅ Imágenes con Fecha EXIF
Si la imagen contiene metadatos EXIF con fecha de captura:
- Se crea un directorio con formato `yyyyMMdd` (ejemplo: `20231115` para el 15 de noviembre de 2023)
- La imagen se mueve a ese directorio

#### ⚠️ Imágenes sin Fecha EXIF
Si la imagen NO contiene fecha de captura en EXIF:
- Se crea un directorio llamado `nodate`
- La imagen se mueve a ese directorio

#### 🔍 Características Adicionales
- ✅ Procesa extensiones: `.jpg`, `.jpeg`, `.JPG`, `.JPEG`, `.JpG`, etc. (sin distinción de mayúsculas)
- ✅ Crea directorios automáticamente si no existen
- ✅ Resuelve conflictos de nombres añadiendo sufijo `_1`, `_2`, etc.
- ✅ Procesa solo el directorio raíz (no recursivo en subdirectorios)
- ✅ Ignora archivos que no sean JPG/JPEG
- ✅ Movimiento atómico de archivos para evitar corrupción

## 📂 Estructura del Proyecto

```
imgtool/
├── build.gradle                               # Configuración Gradle
├── settings.gradle                            # Configuración del proyecto
├── gradle.properties                          # Propiedades de Gradle
├── gradlew                                    # Gradle Wrapper (Linux/macOS)
├── gradlew.bat                                # Gradle Wrapper (Windows)
├── README.md                                  # Este archivo
├── .gitignore                                 # Archivos ignorados por Git
├── gradle/
│   └── wrapper/                               # Archivos del Gradle Wrapper
└── src/
    ├── main/
    │   └── java/
    │       └── com/imgtool/
    │           ├── ImageClassifier.java       # Clase principal (main)
    │           ├── model/
    │           │   └── ImageMetadata.java     # Modelo de datos de imagen
    │           ├── service/
    │           │   ├── ExifReader.java        # Lector de metadatos EXIF
    │           │   └── ImageOrganizer.java    # Organizador de imágenes
    │           └── util/
    │               └── DirectoryValidator.java # Validador de directorios
    └── test/
        └── java/
            └── com/imgtool/
                ├── ImageClassifierTest.java   # Tests de la clase principal
                ├── model/
                │   └── ImageMetadataTest.java
                ├── service/
                │   ├── ExifReaderTest.java
                │   └── ImageOrganizerTest.java
                └── util/
                    └── DirectoryValidatorTest.java
```

## 📝 Ejemplos de Uso

### Ejemplo 1: Organizar Fotos de Vacaciones

**Comando:**
```bash
java -jar imgtool-1.0.0.jar C:\Users\Juan\Pictures\Vacaciones2023
```

**Estado Inicial:**
```
C:\Users\Juan\Pictures\Vacaciones2023\
├── IMG_001.jpg    (Fecha EXIF: 2023-11-15)
├── IMG_002.JPG    (Fecha EXIF: 2023-11-15)
├── foto1.jpeg     (Fecha EXIF: 2023-12-01)
├── scan.jpg       (Sin EXIF)
└── captura.jpg    (Sin EXIF)
```

**Estado Final:**
```
C:\Users\Juan\Pictures\Vacaciones2023\
├── 20231115/
│   ├── IMG_001.jpg
│   └── IMG_002.JPG
├── 20231201/
│   └── foto1.jpeg
└── nodate/
    ├── scan.jpg
    └── captura.jpg
```

### Ejemplo 2: Procesamiento con Conflictos de Nombres

Si hay un archivo `foto.jpg` que ya existe en el directorio destino:
```
20231115/
├── foto.jpg       (archivo existente)
└── foto_1.jpg     (archivo movido con sufijo)
```

## 📊 Sistema de Logging

La aplicación utiliza el sistema de logging estándar de Java (`java.util.logging`). Los logs se muestran en la consola en tiempo real.

### Niveles de Log

| Nivel | Descripción | Ejemplo |
|-------|-------------|---------|
| **INFO** | Operaciones principales y resumen | "Starting image classification...", "Processed 25 images." |
| **FINE** | Detalles de procesamiento individual | "Extracted capture date 2023-11-15 from IMG_001.jpg" |
| **WARNING** | Problemas no críticos | "No capture date found in scan.jpg" |
| **SEVERE** | Errores críticos | "Directory does not exist: C:\invalid\path" |

### Ejemplo de Salida

```
INFO: Starting image classification for directory: C:\Users\Juan\Pictures\Vacaciones2023
INFO: Starting image organization in directory: C:\Users\Juan\Pictures\Vacaciones2023
INFO: Created directory: 20231115
INFO: Created directory: nodate
INFO: Completed image organization. Processed 5 images.
INFO: Image classification completed successfully. Processed 5 images.
Successfully processed 5 images.
```

## 🌐 Configuración de Proxy para Gradle

Si trabajas detrás de un proxy corporativo, puedes configurar Gradle usando el archivo `gradle.properties`. Ejemplo de configuración:

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

Coloca estos parámetros en el fichero `gradle.properties` en la raíz del proyecto. Esto permitirá a Gradle descargar dependencias y ejecutar tareas correctamente detrás de un proxy.

## 🔧 Dependencias Principales

| Dependencia | Versión | Propósito |
|-------------|---------|-----------|
| **metadata-extractor** | 2.18.0 | Lectura profesional de metadatos EXIF de imágenes |
| **JUnit 5** | 5.9.3 | Framework de testing moderno |
| **Mockito** | 5.3.1 | Framework para mocking en tests unitarios |
| **JaCoCo** | 0.8.10 | Análisis de cobertura de código |

## 🏗️ Arquitectura del Código

### Diseño de Capas

```
┌─────────────────────────────────────┐
│     ImageClassifier (Main)          │  ← Capa de Presentación (CLI)
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     ImageOrganizer (Service)        │  ← Capa de Lógica de Negocio
│     ExifReader (Service)            │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     ImageMetadata (Model)           │  ← Capa de Modelo de Datos
│     DirectoryValidator (Util)       │
└─────────────────────────────────────┘
```

### Principios de Diseño

- ✅ **Separation of Concerns**: Cada clase tiene una responsabilidad única
- ✅ **Dependency Injection**: Facilita testing con mocks
- ✅ **Fail-Safe**: Continúa procesando aunque una imagen falle
- ✅ **Atomic Operations**: Usa `ATOMIC_MOVE` para evitar corrupción
- ✅ **Clean Code**: Código legible y bien documentado

## 🧪 Cobertura de Tests

Los tests unitarios cubren:

- ✅ Validación de directorios (existencia, permisos, tipo)
- ✅ Extracción de metadatos EXIF (con y sin fecha)
- ✅ Organización de imágenes (múltiples escenarios)
- ✅ Resolución de conflictos de nombres
- ✅ Manejo de errores y excepciones
- ✅ Procesamiento de diferentes extensiones
- ✅ Casos edge (directorios vacíos, archivos inválidos, etc.)

**Objetivo de Cobertura**: ≥ 80%

## ⚠️ Limitaciones Conocidas

- La aplicación **no es recursiva**: solo procesa archivos en el directorio raíz especificado
- Solo procesa archivos **JPG/JPEG**: otros formatos (PNG, GIF, RAW, etc.) son ignorados
- Los archivos son **movidos**, no copiados: el archivo original ya no estará en su ubicación inicial
- Requiere **permisos de escritura** en el directorio de trabajo

## 🐛 Solución de Problemas

### Error: "Directory does not exist"
**Causa**: La ruta especificada no existe
**Solución**: Verificar que la ruta sea correcta y el directorio exista

### Error: "Directory is not writable"
**Causa**: No hay permisos de escritura
**Solución**: Ejecutar con permisos adecuados o cambiar permisos del directorio

### No se procesan algunas imágenes
**Causa**: El archivo puede estar corrupto o no ser un JPG válido
**Solución**: Revisar los logs para ver warnings específicos

## 🤝 Contribuciones

Este es un proyecto de código abierto. Las contribuciones son bienvenidas:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo una licencia permisiva.

## 👨‍💻 Autor y Mantenimiento

Proyecto creado para facilitar la organización de colecciones fotográficas basándose en metadatos EXIF.

---

**¿Preguntas o Sugerencias?** Abre un issue en el repositorio del proyecto.
