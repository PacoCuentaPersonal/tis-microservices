package com.tis.notificationservice.services.email;

import com.tis.notificationservice.config.EmailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl extends BaseEmailService implements IEmailService {

    @Autowired
    private EmailConfig emailConfig;

    public EmailServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        super(javaMailSender, templateEngine);
    }

    // ========================================
    // MÉTODO ORIGINAL (para compatibilidad)
    // ========================================

    @Override
    public void sendEmail(String to, String subject, String template, Context context) {
        // Delega al método protegido de la clase base
        super.sendEmail(to, subject, template, context);
    }

    // ========================================
    // MÉTODOS HELPER para templates específicos
    // ========================================

    /**
     * Envía email para restablecer contraseña
     */
    public void sendPasswordResetEmail(String email, String username, String resetToken) {
        try {
            Context context = createBaseContext();

            // Variables específicas del template
            context.setVariable("email", email);
            context.setVariable("username", username);
            context.setVariable("resetUrl", baseUrl + "/reset-password?token=" + resetToken);

            sendEmail(email, "Restablecer tu contraseña", "reset-password-email", context);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando email de reset password", e);
        }
    }

    /**
     * Envía email de verificación
     */
    public void sendEmailVerification(String email, String username, String verificationToken) {
        try {
            Context context = createBaseContext();

            // Variables específicas del template
            context.setVariable("email", email);
            context.setVariable("username", username);
            context.setVariable("verifyUrl", baseUrl + "/verify-email?token=" + verificationToken);

            sendEmail(email, "Verifica tu dirección de email", "email-verification", context);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando email de verificación", e);
        }
    }

    /**
     * Envía email usando template con configuración automática
     */
    public void sendTemplateEmail(String to, String subject, String template, Context additionalContext) {
        Context context = createBaseContext();

        // Agregar variables adicionales si existen
        if (additionalContext != null) {
            additionalContext.getVariableNames().forEach(varName ->
                    context.setVariable(varName, additionalContext.getVariable(varName))
            );
        }

        sendEmail(to, subject, template, context);
    }

    // ========================================
    // MÉTODO HELPER PRIVADO
    // ========================================

    /**
     * Crea el contexto base con la configuración global
     */
    private Context createBaseContext() {
        Context context = new Context();
        context.setVariable("config", emailConfig);
        return context;
    }
}