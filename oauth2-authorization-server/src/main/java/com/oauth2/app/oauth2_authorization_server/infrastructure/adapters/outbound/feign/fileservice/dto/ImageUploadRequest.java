package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.feign.fileservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ImageUploadRequest {

    @NotNull(message = "File is required")
    private MultipartFile file;

    @Size(max = 50, message = "Category too long")
    private String nameImageCategory; // Kept original name

    @Min(value = 1, message = "Quality must be between 1-100")
    @Max(value = 100, message = "Quality must be between 1-100")
    private Integer quality = 90;

    private boolean generateThumbnail = true;
}
