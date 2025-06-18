package com.jcs.fileservice.util;

import com.jcs.fileservice.config.ImageConfig;
import com.jcs.fileservice.exception.thrower.InvalidImageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageValidator {

    private final ImageConfig imageConfig;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp",
            "image/tiff"
    );

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("File is empty");
        }

        // Validar tamaño
        if (file.getSize() > imageConfig.getMaxFileSize()) {
            throw new InvalidImageException(
                    String.format("File size exceeds maximum allowed: %d MB",
                            imageConfig.getMaxFileSize() / (1024 * 1024))
            );
        }

        // Validar tipo MIME
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidImageException("Invalid file type: " + contentType);
        }

        // Validar que realmente es una imagen
        try {
            validateImageContent(file.getBytes());
        } catch (IOException e) {
            throw new InvalidImageException("Failed to validate image content", e);
        }
    }

    private void validateImageContent(byte[] data) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (!readers.hasNext()) {
                throw new InvalidImageException("File is not a valid image");
            }

            ImageReader reader = readers.next();
            reader.setInput(iis);

            // Validar dimensiones
            int width = reader.getWidth(0);
            int height = reader.getHeight(0);

            if (width > imageConfig.getMaxWidth() || height > imageConfig.getMaxHeight()) {
                throw new InvalidImageException(
                        String.format("Image dimensions exceed maximum allowed: %dx%d",
                                imageConfig.getMaxWidth(), imageConfig.getMaxHeight())
                );
            }

            reader.dispose();
        }
    }
}