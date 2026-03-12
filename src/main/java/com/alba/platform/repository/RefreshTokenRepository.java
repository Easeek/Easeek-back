package com.alba.platform.repository;

import com.alba.platform.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserIdAndToken(String userId, String token);

    void deleteByExpiresAtBefore(LocalDateTime now);

    Optional<RefreshToken> findByTokenAndExpiresAtAfter(String token, LocalDateTime now);
}