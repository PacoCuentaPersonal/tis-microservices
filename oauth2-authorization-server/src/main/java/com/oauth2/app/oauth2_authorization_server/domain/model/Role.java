package com.oauth2.app.oauth2_authorization_server.domain.model;

import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    private Integer id;
    private PublicIdVO publicId;
    private String name;
    private String description;
    private boolean active;

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void updateDetails(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
