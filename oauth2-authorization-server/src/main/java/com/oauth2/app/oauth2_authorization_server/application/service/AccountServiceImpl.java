package com.oauth2.app.oauth2_authorization_server.application.service;

import com.jcs.response.EnvelopeResponse;
import com.oauth2.app.oauth2_authorization_server.application.port.in.IAccountService;
import com.oauth2.app.oauth2_authorization_server.application.port.in.IRoleService;
import com.oauth2.app.oauth2_authorization_server.application.port.out.AccountRepository;
import com.oauth2.app.oauth2_authorization_server.application.port.out.IAccountEvent;
import com.oauth2.app.oauth2_authorization_server.domain.model.Account;
import com.oauth2.app.oauth2_authorization_server.domain.model.Role;
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;
// Corrected DTO imports
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.account.AccountRequest;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.account.AccountUpdateRequest;
import com.oauth2.app.oauth2_authorization_server.application.exception.throwers.AccountAlreadyExistsException; // Path corregido
import com.oauth2.app.oauth2_authorization_server.application.exception.throwers.AccountNotFoundException; // Path corregido
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.feign.fileservice.IFileServiceClient;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.feign.fileservice.dto.ImageUploadRequest;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.feign.fileservice.dto.ImageUploadResponse;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.token.DataTokenService;
import com.tis.account.NewAccountCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // Keep this import for MultipartFile type

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService {
    private static final String BASE_PATH = "/api/v1/images/";
    private static final String BUCKET_NAME = "personas";

    @Value("${app.host.api-gateway}")
    private String hostGateway;

    private final PasswordEncoder passwordEncoder;
    private final IRoleService roleService; 
    private final IFileServiceClient fileServiceClient; 
    private final IAccountEvent accountEvent; 
    private final AccountRepository accountRepository; 
    private final DataTokenService dataTokenService; 

    @Override
    public Page<Account> fetchAllAccounts(Pageable pageable) {
        return accountRepository.findAllByActiveTrue(pageable);
    }

    @Override
    public Page<Account> accountsFetch(String email, Pageable filter) {
        return accountRepository.findByEmailContainingIgnoreCaseAndActiveTrue(email, filter);
    }

    @Override
    @Transactional
    public Account accountCreate(AccountRequest request) {
        accountRepository.findByEmail(request.email()).ifPresent(acc -> {
            throw new AccountAlreadyExistsException("El correo electronico esta en uso por otra cuenta");
        });

        String imageUrl = uploadProfileImage(request.image());
        Role role = roleService.findByPublicId(request.publicRoleId());

        Account account = Account.builder()
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .active(Boolean.TRUE)
                .emailVerified(Boolean.FALSE) 
                .profilePictureUrl(imageUrl)
                .role(role)
                .publicId(PublicIdVO.generate()) 
                .build();

        Account savedAccount = accountRepository.save(account);

        String verificationToken = generateVerificationToken(savedAccount.getPublicId().value(), savedAccount.getEmail());
        String verificationUrl = String.join("", hostGateway, "/api/v1/account/verify?token=", verificationToken);

        NewAccountCreatedEvent accountCreatedEvent = new NewAccountCreatedEvent(
                savedAccount.getPublicId().value(),
                savedAccount.getEmail(),
                savedAccount.getUsername(),
                verificationUrl
        );
        accountEvent.sendNewAccountCreatedEvent(accountCreatedEvent);
        return savedAccount;
    }

    @Override
    @Transactional
    public Account accountUpdate(UUID publicId, AccountUpdateRequest request) {
        Account account = accountRepository.findByPublicIdAndActiveTrue(new PublicIdVO(publicId))
                .orElseThrow(() -> new AccountNotFoundException("El identificador de la cuenta no es válido: " + publicId));

        boolean accountUpdated = false;

        if (request.hasImage()) {
            deleteOldProfileImage(account.getProfilePictureUrl());
            account.setProfilePictureUrl(uploadProfileImage(request.image()));
            accountUpdated = true;
        }

        if (request.hasEmail()) {
            accountRepository.findByEmail(request.email()).ifPresent(existingAcc -> {
                if (!existingAcc.getPublicId().value().equals(publicId)) {
                    throw new AccountAlreadyExistsException("El nuevo correo electrónico ya está en uso.");
                }
            });
            account.setEmail(request.email());
            accountUpdated = true;
        }
        if (request.hasPassword()) {
            account.setPassword(passwordEncoder.encode(request.password()));
            accountUpdated = true;
        }
        if (request.hasUsername()) {
            account.setUsername(request.username());
            accountUpdated = true;
        }
        if (request.hasPublicRoleId()) {
            Role newRole = roleService.findByPublicId(request.publicRoleId());
            account.setRole(newRole);
            accountUpdated = true;
        }
        if (request.hasEmailVerified()) {
            account.setEmailVerified(request.emailVerified());
            accountUpdated = true;
        }

        if (accountUpdated) {
            return accountRepository.save(account);
        }
        return account;
    }

    @Override
    @Transactional
    public void accountDelete(UUID publicId) {
        Account account = accountRepository.findByPublicIdAndActiveTrue(new PublicIdVO(publicId))
                .orElseThrow(() -> new AccountNotFoundException("El identificador de la cuenta no es válido para eliminar: " + publicId));
        account.setActive(false);
        accountRepository.save(account); 
    }

    @Override
    @Transactional(readOnly = true)
    public Account findAccountByPublicId(UUID publicId) {
        return accountRepository.findByPublicIdAndActiveTrue(new PublicIdVO(publicId))
                .orElseThrow(() -> new AccountNotFoundException("El identificador de la cuenta no es válido: " + publicId));
    }

    @Override
    @Transactional
    public void verifyAccount(String token) {
        Map<String, Object> tokenData = dataTokenService.decryptAllData(token);
        UUID accountPublicId = UUID.fromString(tokenData.get("sub").toString());

        Account account = accountRepository.findByPublicIdAndActiveTrue(new PublicIdVO(accountPublicId))
                .orElseThrow(() -> new AccountNotFoundException("Token de verificación inválido o cuenta no encontrada."));
        
        account.setEmailVerified(Boolean.TRUE);
        accountRepository.save(account);
    }

    private String uploadProfileImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) return null;

        ImageUploadRequest uploadRequest = ImageUploadRequest.builder()
                .file(imageFile)
                .nameImageCategory(BUCKET_NAME)
                .quality(95)
                .generateThumbnail(true)
                .build();

        ResponseEntity<EnvelopeResponse<ImageUploadResponse>> response =
                fileServiceClient.uploadImage(uploadRequest);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().getData() == null) {
            throw new RuntimeException("Error uploading image to file service: " + response.getStatusCode());
        }
        return response.getBody().getData().getUrl();
    }

    private void deleteOldProfileImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) return;
        List<String> fields = extractFieldsFromUrl(imageUrl);
        if (fields.size() >= 2) {
            String bucketName = fields.get(0);
            String imageId = fields.get(1);
            try {
                fileServiceClient.deleteImage(bucketName, imageId); 
            } catch (Exception e) {
                System.err.println("Failed to delete old profile image: " + imageUrl + ". Error: " + e.getMessage());
            }
        }
    }

    private List<String> extractFieldsFromUrl(String url) {
        if (url == null || !url.contains(BASE_PATH)) {
            return Collections.emptyList();
        }
        String pathAfterBase = url.substring(url.indexOf(BASE_PATH) + BASE_PATH.length());
        String[] parts = pathAfterBase.split("/");
        if (parts.length < 2) {
            return Collections.emptyList();
        }
        return Arrays.asList(parts[0], parts[1]);
    }

    private String generateVerificationToken(UUID accountPublicId, String email) {
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("sub", accountPublicId.toString());
        tokenData.put("email", email);
        tokenData.put("purpose", "email_verification");
        return dataTokenService.encryptData("verification_data", tokenData, Duration.ofHours(24));
    }
}
