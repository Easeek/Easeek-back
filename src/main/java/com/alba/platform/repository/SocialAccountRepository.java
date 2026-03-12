package com.alba.platform.repository;

import com.alba.platform.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderId(
        SocialAccount.Provider provider,
        String providerId
    );

    boolean existsByProviderAndProviderId(
        SocialAccount.Provider provider,
        String providerId
    );
}