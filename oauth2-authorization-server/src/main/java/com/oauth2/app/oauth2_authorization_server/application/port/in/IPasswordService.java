package com.oauth2.app.oauth2_authorization_server.application.port.in;

public interface IPasswordService {
    void changePassword(Long userId, String oldPassword, String newPassword);
    void resetPassword(String resetToken, String newPassword);
    String generateResetToken(String email);
    boolean validateResetToken(String token);
    boolean validatePasswordStrength(String password);
}
