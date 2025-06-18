package com.tis.notificationservice.listener;

import com.tis.account.NewAccountCreatedEvent;
import com.tis.notificationservice.services.email.EmailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountListener {

    @Autowired
    private EmailServiceImpl emailService;

    @KafkaListener(topics = "newAccountCreated", groupId = "notification-service")
    public void listenNewAccountCreatedEvent(NewAccountCreatedEvent data) {
        log.info("New account event received: {}", data);

        try {
            // Generar token de verificación usando el publicID
            String verificationToken = generateVerificationToken(data.publicID());

            // Enviar email de verificación
            emailService.sendEmailVerification(
                    data.email(),
                    data.username(),
                    verificationToken
            );

            log.info("Verification email sent successfully to: {}", data.email());

        } catch (Exception e) {
            log.error("Error sending verification email to: {} - Error: {}", data.email(), e.getMessage(), e);
        }
    }

    /**
     * Genera token de verificación basado en el publicID
     */
    private String generateVerificationToken(java.util.UUID publicID) {
        // Usar el publicID directamente como token
        return publicID.toString();
    }
}