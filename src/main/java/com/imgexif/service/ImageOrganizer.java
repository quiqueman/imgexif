package com.imgexif.service;

import com.imgexif.model.ImageMetadata;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Orquestador de organización de imágenes.
 */
public class ImageOrganizer {
    private static final Logger LOGGER = Logger.getLogger(ImageOrganizer.class.getName());
    private static final String NO_DATE_DIR = "nodate";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final Path sourceDir;
    private final ExifReader exifReader;

    public ImageOrganizer(Path sourceDir) {
        this(sourceDir, new ExifReader());
    }

    // Para testing (inyección de mock)
    public ImageOrganizer(Path sourceDir, ExifReader exifReader) {
        this.sourceDir = Objects.requireNonNull(sourceDir);
        this.exifReader = Objects.requireNonNull(exifReader);
    }

    /**
     * Organiza las imágenes en el directorio fuente.
     * @return cantidad de imágenes procesadas
     */
    public int organizeImages() throws IOException {
        AtomicInteger count = new AtomicInteger(0);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file) && isJpgFile(file)) {
                    try {
                        processImage(file);
                        count.incrementAndGet();
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Error procesando imagen: " + file.getFileName(), e);
                    }
                }
            }
        }
        return count.get();
    }

    private void processImage(Path image) throws IOException {
        ImageMetadata metadata = exifReader.extractMetadata(image);
        Path targetDir = determineTargetDirectory(metadata);
        Files.createDirectories(targetDir);
        if (!Files.isWritable(targetDir)) {
            throw new IOException("No se puede escribir en el directorio destino: " + targetDir);
        }
        Path targetFile = resolveFileNameConflict(targetDir.resolve(image.getFileName()));
        Files.move(image, targetFile, StandardCopyOption.ATOMIC_MOVE);
        LOGGER.info("Imagen movida: " + image.getFileName() + " → " + targetFile);
    }

    Path determineTargetDirectory(ImageMetadata metadata) {
        if (metadata.hasCaptureDate()) {
            String dirName = DATE_FORMAT.format(metadata.getCaptureDate().get());
            Path target = sourceDir.resolve(dirName);
            if (!Files.exists(target)) {
                LOGGER.info("Creando directorio: " + dirName);
            }
            return target;
        } else {
            Path target = sourceDir.resolve(NO_DATE_DIR);
            if (!Files.exists(target)) {
                LOGGER.info("Creando directorio: " + NO_DATE_DIR);
            }
            return target;
        }
    }

    Path resolveFileNameConflict(Path target) {
        if (!Files.exists(target)) return target;
        String fileName = target.getFileName().toString();
        String base = fileName;
        String ext = "";
        int dot = fileName.lastIndexOf('.');
        if (dot != -1) {
            base = fileName.substring(0, dot);
            ext = fileName.substring(dot);
        }
        int counter = 1;
        Path parent = target.getParent();
        Path candidate;
        do {
            candidate = parent.resolve(base + "_" + counter + ext);
            counter++;
        } while (Files.exists(candidate));
        return candidate;
    }

    boolean isJpgFile(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg");
    }
}

