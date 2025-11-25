package com.imgexif.util;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utilidad para validar directorios fuente.
 */
public class DirectoryValidator {
    /**
     * Valida que el directorio fuente exista, sea directorio y tenga permisos.
     * @param dir Path del directorio
     * @throws IllegalArgumentException si no es válido
     */
    public static void validateSourceDirectory(Path dir) {
        if (dir == null) {
            throw new IllegalArgumentException("El directorio no puede ser null.");
        }
        if (!Files.exists(dir)) {
            throw new IllegalArgumentException("El directorio no existe: " + dir);
        }
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("La ruta no es un directorio: " + dir);
        }
        if (!Files.isReadable(dir)) {
            throw new IllegalArgumentException("El directorio no es legible: " + dir);
        }
        if (!Files.isWritable(dir)) {
            throw new IllegalArgumentException("El directorio no es escribible: " + dir);
        }
    }
}

