package com.jcs.authenticationservice.service.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

@Service
public class KeyGeneratorService {
    @Value("${rsa.private-key.path}")
    private Resource privateKeyResource;
    @Value("${rsa.public-key.path}")
    private Resource publicKeyResource;

    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";
    private static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";

    private String cleanKey(String keyContent, String header, String footer) {
        return keyContent.replace(header, "")
                .replace(footer, "")
                .replaceAll("\\s+", "");
    }

    public PrivateKey generatePrivateKey() {
        if (Objects.isNull(privateKeyResource) || !privateKeyResource.exists()) {
            throw new IllegalArgumentException("Private Key resource not found: " + privateKeyResource);
        }

        try {
            String keyContent = Files.readString(Paths.get(privateKeyResource.getURI()));
            String cleanedKey = cleanKey(keyContent, PRIVATE_KEY_HEADER, PRIVATE_KEY_FOOTER);
            byte[] keyBytes = Base64.getDecoder().decode(cleanedKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error generating private key", e);
        }
    }

    public PublicKey generatePublicKey() {
        if (Objects.isNull(publicKeyResource) || !publicKeyResource.exists()) {
            throw new IllegalArgumentException("Public Key resource not found: " + publicKeyResource);
        }

        try {
            String keyContent = Files.readString(Paths.get(publicKeyResource.getURI()));
            String cleanedKey = cleanKey(keyContent, PUBLIC_KEY_HEADER, PUBLIC_KEY_FOOTER);
            byte[] keyBytes = Base64.getDecoder().decode(cleanedKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error generating public key", e);
        }
    }
}