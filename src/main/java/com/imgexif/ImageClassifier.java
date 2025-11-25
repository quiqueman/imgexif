package com.imgexif;

import com.imgexif.service.ImageOrganizer;
import com.imgexif.util.DirectoryValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageClassifier {
    private static final Logger LOGGER = Logger.getLogger(ImageClassifier.class.getName());

    public static void main(String[] args) {
        if (args.length != 1) {
            printUsage();
            System.exit(1);
        }
        Path sourceDir = Paths.get(args[0]);
        try {
            DirectoryValidator.validateSourceDirectory(sourceDir);
            LOGGER.info("Starting image organization in directory: " + sourceDir);
            ImageOrganizer organizer = new ImageOrganizer(sourceDir);
            int processed = organizer.organizeImages();
            LOGGER.info("Completed image organization. Processed " + processed + " images.");
            System.out.println("Successfully processed " + processed + " images.");
        } catch (IllegalArgumentException e) {
            LOGGER.severe(e.getMessage());
            printUsage();
            System.exit(1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.err.println("Uso: java -jar imgexif.jar <directorio>");
    }
}

