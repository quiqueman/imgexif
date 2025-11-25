package com.imgexif.model;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * DTO inmutable para metadatos de imagen.
 */
public final class ImageMetadata {
    private final Path imagePath;
    private final LocalDate captureDate;

    public ImageMetadata(Path imagePath, LocalDate captureDate) {
        this.imagePath = Objects.requireNonNull(imagePath, "imagePath no puede ser null");
        this.captureDate = captureDate;
    }

    public Path getImagePath() {
        return imagePath;
    }

    public Optional<LocalDate> getCaptureDate() {
        return Optional.ofNullable(captureDate);
    }

    public boolean hasCaptureDate() {
        return captureDate != null;
    }

    @Override
    public String toString() {
        return "ImageMetadata{" +
                "imagePath=" + imagePath +
                ", captureDate=" + captureDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageMetadata)) return false;
        ImageMetadata that = (ImageMetadata) o;
        return imagePath.equals(that.imagePath) &&
                Objects.equals(captureDate, that.captureDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imagePath, captureDate);
    }
}

