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
public class Account {

    private Long id; // Internal ID, might be optional in some domain contexts if publicId is primary
    private PublicIdVO publicId;
    private String email;
    private String username;
    private String password; // Domain model might handle hashed password or work with it for creation/update
    private boolean emailVerified;
    private String profilePictureUrl;
    private PublicIdVO employeePublicId; // Represents a link to an employee
    private LocalDateTime lastLoginAt;
    private boolean active;
    private Role role; // Domain model for Role

    // Audit fields from BaseAuditEntity
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Example of a domain-specific method (optional)
    public boolean isVerifiedAndActive() {
        return emailVerified && active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public void updateProfilePicture(String newUrl) {
        this.profilePictureUrl = newUrl;
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
