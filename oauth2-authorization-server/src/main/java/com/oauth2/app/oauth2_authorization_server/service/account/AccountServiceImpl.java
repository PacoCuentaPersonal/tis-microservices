package com.oauth2.app.oauth2_authorization_server.service.account;

import com.jcs.response.EnvelopeResponse;
import com.oauth2.app.oauth2_authorization_server.exception.throwers.AccountAlreadyExistsException;
import com.oauth2.app.oauth2_authorization_server.exception.throwers.AccountNotFoundException;
import com.oauth2.app.oauth2_authorization_server.interfaces.feing.IFileServiceClient;
import com.oauth2.app.oauth2_authorization_server.models.entity.Roles;
import com.oauth2.app.oauth2_authorization_server.dto.request.account.AccountRequest;
import com.oauth2.app.oauth2_authorization_server.models.entity.Account;
import com.oauth2.app.oauth2_authorization_server.dto.request.account.AccountUpdateRequest;
import com.oauth2.app.oauth2_authorization_server.interfaces.repository.JpaAccountRepository;
import com.oauth2.app.oauth2_authorization_server.models.feing.ImageUploadRequest;
import com.oauth2.app.oauth2_authorization_server.models.feing.ImageUploadResponse;
import com.oauth2.app.oauth2_authorization_server.service.roles.IRoleService;
import com.oauth2.app.oauth2_authorization_server.service.token.DataTokenService;
import com.tis.account.NewAccountCreatedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.*;

@Service
public class AccountServiceImpl implements IAccountService {
    private static final String BASE_PATH = "/api/v1/images/";
    private static final String BUCKET_NAME = "personas";

    @Value("${app.host.api-gateway}")
    private String hostGateway;

    private final PasswordEncoder passwordEncoder;

    private final IRoleService roleService;
    private final IFileServiceClient fileServiceClient;
    private final IAccountEvent accountEvent;

    private final JpaAccountRepository jpaAccountRepository;
    private final DataTokenService dataTokenService;


    public AccountServiceImpl(JpaAccountRepository jpaAccountRepository, PasswordEncoder passwordEncoder, IRoleService roleService, @Qualifier("fileServiceClient")IFileServiceClient fileServiceClient, IAccountEvent accountEvent, DataTokenService dataTokenService) {
        this.jpaAccountRepository = jpaAccountRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.fileServiceClient = fileServiceClient;
        this.accountEvent = accountEvent;
        this.dataTokenService = dataTokenService;
    }

    public Page<Account> fetchAllAccounts (Pageable pageable){
        return jpaAccountRepository.findAllByActiveTrue(pageable);
    }

    public Page<Account> accountsFetch(String email, Pageable filter){
       return jpaAccountRepository.findByEmailContainingIgnoreCaseAndActiveTrue(email, filter);
    }


    @Transactional(readOnly = false)
    public Account accountCreate(AccountRequest request){
        Optional<Account> byEmail = jpaAccountRepository.findByEmail(request.email());
        if(byEmail.isPresent()){
            throw new AccountAlreadyExistsException("El correo electronico esta en uso por otra cuenta");
        }

        ImageUploadRequest uploadRequest = ImageUploadRequest.builder()
                .file(request.image())
                .nameImageCategory(BUCKET_NAME)
                .quality(95)
                .generateThumbnail(true)
                .build();

        ResponseEntity<EnvelopeResponse<ImageUploadResponse>> response =
                fileServiceClient.uploadImage(uploadRequest);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error uploading image: " + response.getStatusCode());
        }
        String url = Objects.requireNonNull(response.getBody()).getData().getUrl();

        Roles role = roleService.findByPublicId(request.publicRoleId());

        Account account = new Account();
        account.setEmail(request.email());
        account.setUsername(request.username());
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setActive(Boolean.TRUE);
        account.setProfilePictureUrl(url);
        account.setRoles(role);
        Account save = jpaAccountRepository.save(account);

        // Creación de url
        String verificationToken = generateVerificationToken(save.getPublicId(), save.getEmail());
        String verificationUrl = String.join(hostGateway,"/api/v1/account/verify","?token=",verificationToken);

        // kafka
        NewAccountCreatedEvent accountCreatedEvent = new NewAccountCreatedEvent(
                account.getPublicId(),
                account.getEmail(),
                account.getUsername(),
                verificationUrl
        );
        accountEvent.sendNewAccountCreatedEvent(accountCreatedEvent);

        return save;

    }

    public Account accountUpdate(UUID publicId, AccountUpdateRequest request){
        Account account = findAccountByPublicId(publicId);
        boolean accountUpdated = false;

        if (request.hasImage()) {
            String profilePictureUrl = account.getProfilePictureUrl();
            List<String> fields = extractFieldsFromUrl(profilePictureUrl);
            String bucketName = fields.get(0);
            String publicImageId = fields.get(1);
            ResponseEntity<Void> voidResponseEntity = fileServiceClient.deleteImage(bucketName, publicImageId);
            if (!voidResponseEntity.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error deleting old image: " + voidResponseEntity.getStatusCode());
            }
            ImageUploadRequest uploadRequest = buildImageUploadRequest(request.image(), BUCKET_NAME, 95);
            ResponseEntity<EnvelopeResponse<ImageUploadResponse>> response =
                    fileServiceClient.uploadImage(uploadRequest);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error uploading image: " + response.getStatusCode());
            }
            String url = Objects.requireNonNull(response.getBody()).getData().getUrl();
            account.setProfilePictureUrl(url);
            accountUpdated = true;
        }

        if (request.hasEmail()){
            account.setEmail(request.email());
            accountUpdated = true;
        }
        if (request.hasPassword()){
            account.setPassword(passwordEncoder.encode(request.password()));
            accountUpdated = true;
        }
        if (request.hasUsername()){
            account.setUsername(request.username());
            accountUpdated = true;
        }
        if (request.hasPublicRoleId()){
            Roles byPublicId = roleService.findByPublicId(request.publicRoleId());
            account.setRoles(byPublicId);
            accountUpdated = true;
        }
        if (request.hasEmailVerified()){
            account.setEmailVerified(request.emailVerified());
            accountUpdated = true;
        }
        if (accountUpdated){
            jpaAccountRepository.save(account);
        }
        return account;

    }

    @Modifying
    @Transactional()
    public void accountDelete(UUID publicId){
        Account account = findAccountByPublicId(publicId);
        account.setActive(false);
    }

    @Transactional(readOnly = true)
    public Account findAccountByPublicId(UUID publicId){
        Optional<Account> optAccount = jpaAccountRepository.findByPublicIdAndActiveTrue(publicId);
        if (optAccount.isEmpty()){
            throw new AccountNotFoundException("El identificador de la cuenta no es válido");
        }
        return optAccount.get();
    }

    @Modifying
    @Transactional()
    public void verifyAccount(String token){
        Map<String, Object> stringObjectMap = dataTokenService.decryptAllData(token);
        UUID sub = (UUID) stringObjectMap.get("sub");
        Account accountByPublicId = findAccountByPublicId(sub);
        accountByPublicId.setEmailVerified(Boolean.TRUE);
        jpaAccountRepository.save(accountByPublicId);
    }



    private ImageUploadRequest buildImageUploadRequest(MultipartFile image, String bucketName, int quality) {
        return ImageUploadRequest.builder()
                .file(image)
                .nameImageCategory(bucketName)
                .quality(quality)
                .generateThumbnail(true)
                .build();
    }

    private List<String> extractFieldsFromUrl(String url) {
        int basePathIndex = url.indexOf(BASE_PATH);
        if (basePathIndex == -1) {
            throw new IllegalArgumentException("Invalid image URL format");
        }
        String pathAfterBase = url.substring(basePathIndex + BASE_PATH.length());
        String[] parts = pathAfterBase.split("/");
        if (parts.length < 2) {
            throw new IllegalArgumentException("URL must contain both bucket name and image ID");
        }

        String bucketName = parts[0];
        String imageId = parts[1];
        return List.of(bucketName, imageId);
    }

    /**
     * Genera un token de verificación que expira en 24 horas
     */
    private String generateVerificationToken(UUID accountPublicId, String email) {
        Map<String, Object> tokenData = Map.of(
                "sub", accountPublicId.toString(),
                "email", email,
                "purpose", "email_verification"
        );
        return dataTokenService.encryptData("verification_data", tokenData, Duration.ofHours(24));
    }

}
