package com.jcs.fileservice.service.impl;

import com.jcs.fileservice.config.ImageConfig;
import com.jcs.fileservice.models.minio.ImageMetadata;
import com.jcs.fileservice.models.image.ProcessedImage;
import com.jcs.fileservice.service.interfaces.ImageProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProcessorServiceImpl implements ImageProcessorService {

    private final ImageConfig imageConfig;

    @Override
    public ProcessedImage processImage(byte[] imageData, float quality) throws IOException {
        String format = detectImageFormat(imageData);
        log.info("Processing image - Format: {}, Quality: {}%", format, quality);

        // IMPORTANTE: Si ya es WebP de alta calidad, NO reprocesar
        if ("webp".equalsIgnoreCase(format) && quality >= 85) {
            log.info("Image is already WebP with good quality. Skipping reprocessing.");

            // Solo extraer metadata sin reprocesar
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            ImageMetadata metadata = extractMetadataFromImage(image, imageData.length);

            return ProcessedImage.builder()
                    .data(imageData) // Devolver los datos originales sin cambios
                    .metadata(metadata)
                    .build();
        }

        // Para otros formatos o WebP de baja calidad, procesar
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        if (image == null) {
            throw new IOException("Failed to read image");
        }

        // Solo normalizar si es necesario
        BufferedImage toProcess = image;
        if (needsNormalization(image)) {
            toProcess = normalizeImage(image);
        }

        // Convertir a WebP con calidad alta
        byte[] webpData = convertToWebP(toProcess, Math.max(quality, 90f));

        ImageMetadata metadata = extractMetadataFromImage(toProcess, webpData.length);

        log.info("Image processed - Original: {} KB, Final: {} KB",
                imageData.length / 1024, webpData.length / 1024);

        return ProcessedImage.builder()
                .data(webpData)
                .metadata(metadata)
                .build();
    }

    @Override
    public byte[] resize(byte[] imageData, Integer targetWidth, Integer targetHeight) throws IOException {
        String format = detectImageFormat(imageData);
        log.info("Resizing {} image to {}x{}", format, targetWidth, targetHeight);

        BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageData));
        if (original == null) {
            throw new IOException("Failed to read image");
        }

        Dimension targetDimensions = calculateTargetDimensions(
                original.getWidth(), original.getHeight(), targetWidth, targetHeight);

        // Si no hay cambio significativo, devolver original
        if (Math.abs(targetDimensions.width - original.getWidth()) < 2 &&
                Math.abs(targetDimensions.height - original.getHeight()) < 2) {
            log.info("No significant resize needed");
            return imageData;
        }

        // Usar Thumbnailator para alta calidad
        BufferedImage resized = Thumbnails.of(original)
                .size(targetDimensions.width, targetDimensions.height)
                .keepAspectRatio(targetWidth == null || targetHeight == null)
                .outputQuality(1.0)
                .asBufferedImage();

        // Para resize, usar calidad muy alta (95%) para evitar degradación
        byte[] result = convertToWebP(resized, 95f);

        log.info("Resize complete - {} KB -> {} KB",
                imageData.length / 1024, result.length / 1024);

        return result;
    }

    @Override
    public ImageMetadata extractMetadata(byte[] imageData) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        if (image == null) {
            throw new IOException("Failed to read image");
        }
        return extractMetadataFromImage(image, imageData.length);
    }


    private boolean needsNormalization(BufferedImage image) {
        return image.getTransparency() != Transparency.OPAQUE ||
                (image.getType() != BufferedImage.TYPE_INT_RGB &&
                        image.getType() != BufferedImage.TYPE_INT_ARGB);
    }

    private BufferedImage normalizeImage(BufferedImage source) {
        BufferedImage normalized = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = normalized.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, normalized.getWidth(), normalized.getHeight());
            g.drawImage(source, 0, 0, null);
        } finally {
            g.dispose();
        }

        return normalized;
    }

    private byte[] convertToWebP(BufferedImage image, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (!writers.hasNext()) {
            throw new IOException("WebP writer not found");
        }

        ImageWriter writer = writers.next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();

        if (writeParam.canWriteCompressed()) {
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

            String[] compressionTypes = writeParam.getCompressionTypes();
            if (compressionTypes != null && compressionTypes.length > 0) {
                // Para calidad >= 95%, usar lossless si está disponible
                if (quality >= 95) {
                    for (String type : compressionTypes) {
                        if (type.toLowerCase().contains("lossless")) {
                            writeParam.setCompressionType(type);
                            log.debug("Using lossless compression");
                            break;
                        }
                    }
                }
                if (writeParam.getCompressionType() == null) {
                    writeParam.setCompressionType(compressionTypes[0]);
                }
            }

            writeParam.setCompressionQuality(quality / 100f);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), writeParam);
        } finally {
            writer.dispose();
        }

        return baos.toByteArray();
    }

    private Dimension calculateTargetDimensions(int originalWidth, int originalHeight,
                                                Integer targetWidth, Integer targetHeight) {
        if (targetWidth != null && targetWidth > imageConfig.getMaxWidth()) {
            targetWidth = imageConfig.getMaxWidth();
        }
        if (targetHeight != null && targetHeight > imageConfig.getMaxHeight()) {
            targetHeight = imageConfig.getMaxHeight();
        }

        if (targetWidth != null && targetHeight != null) {
            return new Dimension(targetWidth, targetHeight);
        }

        double aspectRatio = (double) originalWidth / originalHeight;

        if (targetWidth != null) {
            targetHeight = (int) Math.round(targetWidth / aspectRatio);
        } else if (targetHeight != null) {
            targetWidth = (int) Math.round(targetHeight * aspectRatio);
        } else {
            targetWidth = originalWidth;
            targetHeight = originalHeight;
        }

        return new Dimension(targetWidth, targetHeight);
    }

    private String detectImageFormat(byte[] data) {
        if (data.length < 12) return "unknown";

        // WebP: RIFF....WEBP
        if (data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F' &&
                data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P') {
            return "webp";
        }

        // JPEG: FFD8FF
        if (data[0] == (byte) 0xFF && data[1] == (byte) 0xD8 && data[2] == (byte) 0xFF) {
            return "jpeg";
        }

        // PNG: 89504E47
        if (data[0] == (byte) 0x89 && data[1] == 0x50 && data[2] == 0x4E && data[3] == 0x47) {
            return "png";
        }

        // GIF: GIF87a or GIF89a
        if (data[0] == 0x47 && data[1] == 0x49 && data[2] == 0x46) {
            return "gif";
        }

        return "unknown";
    }

    private ImageMetadata extractMetadataFromImage(BufferedImage image, long fileSize) {
        return ImageMetadata.builder()
                .width(image.getWidth())
                .height(image.getHeight())
                .format("webp")
                .size(fileSize)
                .colorSpace(image.getColorModel().getColorSpace().toString())
                .bitDepth(image.getColorModel().getPixelSize())
                .exifData(new HashMap<>())
                .build();
    }
}