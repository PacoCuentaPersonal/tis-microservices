package com.oauth2.app.oauth2_authorization_server.application.port.out;

import com.oauth2.app.oauth2_authorization_server.domain.model.Account;
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
// UUID se usa internamente en PublicIdVO, pero la interfaz puede exponer PublicIdVO

public interface AccountRepository {

    Optional<Account> findByEmail(String email);

    boolean existsByPublicIdAndActiveTrue(PublicIdVO publicId);

    Optional<Account> findByPublicIdAndActiveTrue(PublicIdVO publicId);

    Page<Account> findByEmailContainingIgnoreCaseAndActiveTrue(String email, Pageable pageable);

    Page<Account> findAllByActiveTrue(Pageable pageable);

    Account save(Account account); // Para crear y actualizar

    // void delete(Account account); // El borrado es lógico a través del servicio de aplicación usualmente
    // Si se necesita un borrado físico o una operación específica de 'delete' en el repo, se añadiría.
}
