package com.oauth2.app.oauth2_authorization_server.application.port.in;

import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.account.AccountRequest;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.account.AccountUpdateRequest;
// This should ideally refer to the domain model
import com.oauth2.app.oauth2_authorization_server.domain.model.Account; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IAccountService {
    Page<Account> accountsFetch(String email, Pageable filter);
    Page<Account> fetchAllAccounts (Pageable pageable);
    Account accountCreate(AccountRequest request);
    Account accountUpdate(UUID publicId, AccountUpdateRequest request);
    void accountDelete(UUID publicId);
    Account findAccountByPublicId(UUID publicId);
    void verifyAccount(String token);
}
