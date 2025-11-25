# Prompt Optimizado para Desarrollo de Aplicación Java

## Versión Mejorada del Prompt Original

### Contexto
Necesito una aplicación Java empresarial para clasificación automática de fotografías digitales.

### Requisitos Técnicos Core

#### Plataforma y Herramientas
```
- Lenguaje: Java 11 (LTS - Long Term Support)
- Build System: Gradle (con Wrapper incluido)
- Interfaz: CLI (Command Line Interface)
- Logging: java.util.logging (estándar, sin dependencias externas)
```

#### Framework de Testing
```
- Framework: JUnit 5 (Jupiter)
- Mocking: Mockito 5.x
- Cobertura: JaCoCo plugin
- Objetivo de cobertura: ≥ 80%
- Características:
  * Tests unitarios para cada componente
  * Tests de integración
  * Tests parametrizados
  * Uso de @TempDir para tests de filesystem
```

#### Dependencias Externas
```
- metadata-extractor:2.18.0 (lectura de EXIF)
  * Justificación: Librería estable, mantenida, estándar de industria
  * Propósito: Extraer metadatos EXIF de archivos JPG/JPEG
```

### Requisitos Funcionales Detallados

#### 1. Entrada y Validación
```
Comando: java -jar imgexif.jar <directorio>

Validaciones:
- ✓ Argumento obligatorio (directorio)
- ✓ Directorio debe existir
- ✓ Directorio debe ser legible
- ✓ Directorio debe ser escribible
- ✓ Path debe ser un directorio válido (no archivo)

Salida en caso de error:
- Mensaje descriptivo en stderr
- Código de salida: 1
- Mensaje de ayuda con sintaxis correcta
```

#### 2. Procesamiento de Imágenes
```
Algoritmo:
1. Listar todos los archivos del directorio (no recursivo)
2. Filtrar solo archivos JPG/JPEG (case-insensitive)
3. Para cada imagen:
   a. Leer metadatos EXIF
   b. Extraer fecha de captura (prioridad):
      1. EXIF DateTimeOriginal (TAG_DATETIME_ORIGINAL)
      2. EXIF DateTimeDigitized (TAG_DATETIME_DIGITIZED)
      3. EXIF DateTime (TAG_DATETIME)
   c. Determinar directorio destino:
      - Si hay fecha: "yyyyMMdd" (ej: 20231115)
      - Si no hay fecha: "nodate"
   d. Crear directorio destino si no existe
   e. Resolver conflictos de nombre (sufijo _1, _2, etc.)
   f. Mover archivo (no copiar) con operación atómica
   g. Logear resultado

Características:
- ✓ Procesamiento resiliente (fallar en una imagen no detiene el proceso)
- ✓ Operaciones atómicas (Files.move con ATOMIC_MOVE)
- ✓ Logging detallado de cada operación
- ✓ Contador de imágenes procesadas
```

#### 3. Gestión de Metadatos EXIF
```
Estrategia de lectura:
1. Intentar leer metadatos con metadata-extractor
2. Si falla (IOException, ImageProcessingException):
   - Logear warning
   - Retornar metadata con fecha null
   - NO lanzar exception (continuar procesando)
3. Buscar fecha en orden de prioridad (ver arriba)
4. Convertir java.util.Date a java.time.LocalDate
5. Retornar objeto ImageMetadata inmutable

Manejo de errores:
- Archivos corruptos → fecha null → mover a "nodate"
- Sin metadatos EXIF → fecha null → mover a "nodate"
- EXIF presente pero sin fecha → null → mover a "nodate"
```

#### 4. Sistema de Directorios
```
Convención de nombres:
- Con fecha: yyyyMMdd (formato ISO 8601 simplificado)
  * Ventajas:
    - Ordenamiento alfabético = cronológico
    - Sin ambigüedades internacionales
    - Compatible con todos los filesystem
    - No requiere caracteres especiales

- Sin fecha: "nodate" (literal)
  * Casos:
    - Imágenes sin EXIF
    - EXIF sin fecha
    - Archivos ilegibles
    - Imágenes escaneadas

Creación de directorios:
- Files.createDirectories() (crea padres si necesario)
- Logear cuando se crea nuevo directorio
- Verificar permisos de escritura
```

#### 5. Resolución de Conflictos
```
Estrategia cuando archivo destino existe:

Original: foto.jpg
Existe en destino → foto_1.jpg
También existe → foto_2.jpg
También existe → foto_3.jpg
...

Algoritmo:
1. Obtener nombre base (sin extensión)
2. Obtener extensión (incluir punto)
3. Contador = 1
4. Mientras Files.exists(targetPath):
   a. Construir nuevo nombre: base + "_" + contador + extensión
   b. Verificar si existe
   c. Incrementar contador
5. Retornar path sin conflicto
```

### Arquitectura y Diseño

#### Estructura de Paquetes
```
com.imgexif/
├── ImageClassifier.java          (main, CLI handling)
├── model/
│   └── ImageMetadata.java        (DTO inmutable)
├── service/
│   ├── ExifReader.java           (EXIF extraction)
│   └── ImageOrganizer.java       (orchestration)
└── util/
    └── DirectoryValidator.java   (validation utilities)
```

#### Responsabilidades por Clase

**ImageClassifier (Main)**
```java
Responsabilidades:
- Punto de entrada de la aplicación
- Parsing y validación de argumentos CLI
- Orquestación de alto nivel
- Manejo de excepciones globales
- Códigos de salida apropiados
- Mensajes de ayuda

Dependencias:
- ImageOrganizer
- DirectoryValidator
```

**ImageOrganizer (Service)**
```java
Responsabilidades:
- Listar archivos del directorio
- Filtrar solo JPG/JPEG
- Coordinar procesamiento de cada imagen
- Determinar directorio destino
- Crear directorios
- Mover archivos
- Resolver conflictos de nombres
- Contar imágenes procesadas

Dependencias:
- ExifReader (inyectable para testing)

Métodos principales:
+ organizeImages(): int throws IOException
- processImage(Path): void throws IOException
- determineTargetDirectory(ImageMetadata): Path
- moveImageToDirectory(Path, Path): void throws IOException
- resolveFileNameConflict(Path): Path
- isJpgFile(Path): boolean
```

**ExifReader (Service)**
```java
Responsabilidades:
- Leer metadatos EXIF de archivo
- Extraer fecha de captura
- Convertir formatos de fecha
- Manejo robusto de errores

Dependencias:
- metadata-extractor library

Métodos principales:
+ extractMetadata(Path): ImageMetadata
- extractCaptureDate(Metadata): LocalDate
```

**ImageMetadata (Model)**
```java
Responsabilidades:
- Almacenar metadata de imagen
- Proporcionar acceso inmutable a datos

Atributos:
- imagePath: Path (final)
- captureDate: LocalDate (final, nullable)

Métodos:
+ getImagePath(): Path
+ getCaptureDate(): Optional<LocalDate>
+ hasCaptureDate(): boolean
+ toString(): String
```

**DirectoryValidator (Util)**
```java
Responsabilidades:
- Validar existencia de directorio
- Validar permisos (lectura/escritura)
- Validar tipo (directorio vs archivo)

Métodos:
+ validateSourceDirectory(Path): void throws IllegalArgumentException
```

### Configuración de Build (build.gradle)

```groovy
plugins {
    id 'java'
    id 'application'
    id 'jacoco'
}

group = 'com.imgexif'
version = '1.0.0'
sourceCompatibility = '11'
targetCompatibility = '11'

repositories {
    mavenCentral()
}

dependencies {
    // EXIF metadata extraction
    implementation 'com.drewnoakes:metadata-extractor:2.18.0'
    
    // Testing
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.9.3'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.9.3'
    testImplementation 'org.junit.jupiter:junit-jupiter-params:5.9.3'
    testImplementation 'org.mockito:mockito-core:5.3.1'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.3.1'
}

application {
    mainClass = 'com.imgexif.ImageClassifier'
}

test {
    useJUnitPlatform()
    finalizedBy jacocoTestReport
}

jacoco {
    toolVersion = "0.8.10"
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
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

jar {
    manifest {
        attributes 'Main-Class': 'com.imgexif.ImageClassifier'
    }
}
```

### Estrategia de Testing

#### Tests Esenciales por Componente

**DirectoryValidatorTest**
```
✓ validateSourceDirectory_ValidDirectory
✓ validateSourceDirectory_NullPath
✓ validateSourceDirectory_NonExistent
✓ validateSourceDirectory_NotADirectory
✓ validateSourceDirectory_NotReadable (si plataforma permite)
✓ validateSourceDirectory_NotWritable (si plataforma permite)
```

**ExifReaderTest**
```
✓ extractMetadata_WithExifDate
✓ extractMetadata_WithoutExifDate
✓ extractMetadata_InvalidFile
✓ extractMetadata_NonExistentFile
✓ extractMetadata_CorruptedJpg
```

**ImageOrganizerTest (con Mocks)**
```
✓ organizeImages_WithCaptureDate
✓ organizeImages_WithoutCaptureDate
✓ organizeImages_MixedDates
✓ organizeImages_IgnoresNonJpgFiles
✓ organizeImages_HandlesJpegExtension
✓ organizeImages_FileNameConflict
✓ organizeImages_MultipleConflicts
✓ organizeImages_EmptyDirectory
✓ organizeImages_IgnoresSubdirectories
✓ organizeImages_CaseInsensitiveExtension
```

**ImageMetadataTest**
```
✓ constructor_WithDate
✓ constructor_WithoutDate
✓ hasCaptureDate_True
✓ hasCaptureDate_False
✓ getCaptureDate_Present
✓ getCaptureDate_Absent
✓ toString_WithDate
✓ toString_WithoutDate
```

**ImageClassifierTest**
```
✓ main_NoArguments
✓ main_ValidDirectory
✓ main_InvalidDirectory
✓ main_NonWritableDirectory
```

#### Uso de Mocks
```java
@Mock
private ExifReader mockExifReader;

@Test
void testOrganizeImages_WithCaptureDate() throws IOException {
    // Setup
    Path image = createTestFile("test.jpg");
    LocalDate date = LocalDate.of(2023, 11, 15);
    ImageMetadata metadata = new ImageMetadata(image, date);
    
    when(mockExifReader.extractMetadata(image))
        .thenReturn(metadata);
    
    // Execute
    ImageOrganizer organizer = new ImageOrganizer(tempDir, mockExifReader);
    int count = organizer.organizeImages();
    
    // Verify
    assertEquals(1, count);
    assertTrue(Files.exists(tempDir.resolve("20231115/test.jpg")));
    verify(mockExifReader, times(1)).extractMetadata(any());
}
```

### Logging Standards

```java
// En cada clase de servicio
private static final Logger LOGGER = Logger.getLogger(ClassName.class.getName());

// Niveles y uso
LOGGER.info("Starting image classification for directory: " + path);
LOGGER.fine("Processing image: " + imagePath.getFileName());
LOGGER.warning("No capture date found in " + imagePath.getFileName());
LOGGER.severe("Directory does not exist: " + path);

// Con excepciones
LOGGER.log(Level.SEVERE, "Error processing image", exception);
```

### Checklist de Completitud

#### Código
- [ ] Todas las clases implementadas
- [ ] JavaDoc en métodos públicos
- [ ] Manejo de excepciones robusto
- [ ] Validaciones de entrada
- [ ] Logging apropiado
- [ ] Sin code smells evidentes

#### Tests
- [ ] Cobertura ≥ 80%
- [ ] Todos los tests pasan
- [ ] Tests independientes
- [ ] Tests con nombres descriptivos
- [ ] Uso de @TempDir para filesystem
- [ ] Mocks donde apropiado

#### Build
- [ ] build.gradle correcto
- [ ] Gradle Wrapper presente
- [ ] `gradle build` exitoso
- [ ] `gradle test` exitoso
- [ ] `gradle jacocoTestReport` genera reportes
- [ ] JAR ejecutable generado

#### Documentación
- [ ] README.md completo
- [ ] Instrucciones de uso
- [ ] Ejemplos de ejecución
- [ ] Estructura del proyecto
- [ ] Requisitos del sistema

#### Extras
- [ ] .gitignore configurado
- [ ] Sin warnings de compilación
- [ ] Compatible Java 11
- [ ] Funciona en Windows/Linux/macOS

---

## Formato de Prompt para IA

**Instrucción concisa para herramienta de IA:**

```
Crea una aplicación Java 11 con las siguientes características:

CORE:
- Build: Gradle con wrapper
- CLI: recibe directorio como argumento
- Logging: java.util.logging
- Testing: JUnit 5 + Mockito, cobertura 80%+ con JaCoCo

FUNCIONALIDAD:
- Leer metadatos EXIF de archivos JPG/JPEG (usar metadata-extractor)
- Clasificar imágenes en directorios:
  * Con fecha EXIF → directorio "yyyyMMdd"
  * Sin fecha EXIF → directorio "nodate"
- Mover (no copiar) archivos
- Resolver conflictos de nombres con sufijo _1, _2, etc.
- Procesar solo directorio raíz (no recursivo)
- Soportar extensiones jpg/jpeg (case-insensitive)

ARQUITECTURA:
- ImageClassifier: main, CLI
- ImageOrganizer: orquestador (service)
- ExifReader: extractor EXIF (service)
- ImageMetadata: DTO (model)
- DirectoryValidator: validaciones (util)

CALIDAD:
- Tests unitarios completos
- Código limpio y documentado
- Manejo robusto de errores
- Operaciones atómicas de archivo
- Logging detallado

Genera: código fuente, tests, build.gradle, README.md completo
```

---

**Prompt Optimizado v1.0**
*Última actualización: 2025-11-25*
