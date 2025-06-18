package com.jcs.fileservice.service.interfaces;

import com.jcs.fileservice.models.minio.ImageMetadata;
import com.jcs.fileservice.models.image.ProcessedImage;

import java.io.IOException;

public interface ImageProcessorService {
    ProcessedImage processImage(byte[] imageData, float quality) throws IOException;
    byte[] resize(byte[] imageData, Integer targetWidth, Integer targetHeight) throws IOException;
    ImageMetadata extractMetadata(byte[] imageData) throws IOException;
}