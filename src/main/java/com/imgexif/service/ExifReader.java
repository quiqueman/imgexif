package com.imgexif.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.*;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.imgexif.model.ImageMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para extraer metadatos EXIF de imágenes.
 */
public class ExifReader {
    private static final Logger LOGGER = Logger.getLogger(ExifReader.class.getName());

    /**
     * Extrae los metadatos EXIF de una imagen.
     * @param imagePath Ruta de la imagen.
     * @return ImageMetadata con la fecha de captura (puede ser null).
     */
    public ImageMetadata extractMetadata(Path imagePath) {
        LocalDate captureDate = null;
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imagePath.toFile());
            captureDate = extractCaptureDate(metadata);
            if (captureDate != null) {
                LOGGER.fine("Fecha de captura extraída de " + imagePath.getFileName() + ": " + captureDate);
            } else {
                LOGGER.warning("No se encontró fecha de captura en " + imagePath.getFileName());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo leer EXIF de " + imagePath.getFileName(), e);
        }
        return new ImageMetadata(imagePath, captureDate);
    }

    /**
     * Extrae la fecha de captura de los metadatos EXIF.
     */
    private LocalDate extractCaptureDate(Metadata metadata) {
        // 1. DateTimeOriginal
        ExifSubIFDDirectory exif = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (exif != null) {
            Date date = exif.getDateOriginal();
            if (date != null) return toLocalDate(date);
            date = exif.getDateDigitized();
            if (date != null) return toLocalDate(date);
        }
        // 2. DateTime (EXIF IFD0)
        Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (ifd0 != null) {
            Date date = ifd0.getDate(ExifIFD0Directory.TAG_DATETIME);
            if (date != null) return toLocalDate(date);
        }
        return null;
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}

