package com.jcs.fileservice.dto.image;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ImageUploadRequest {

    @NotNull(message = "File is required")
    private MultipartFile file;
    @Size(max = 50, message = "Category too long")
    private String nameImageCategory;

    @Min(value = 1, message = "Quality must be between 1-100")
    @Max(value = 100, message = "Quality must be between 1-100")
    private Integer quality = 90;

    private boolean generateThumbnail = true;
}