package com.alba.platform.service;

import com.alba.platform.dto.request.SignupRequest;
import com.alba.platform.dto.response.LoginResponse;
import com.alba.platform.dto.response.SignupResponse;
import com.alba.platform.entity.RefreshToken;
import com.alba.platform.entity.SocialAccount;
import com.alba.platform.entity.User;
import com.alba.platform.repository.RefreshTokenRepository;
import com.alba.platform.repository.SocialAccountRepository;
import com.alba.platform.repository.UserRepository;
import com.alba.platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient webClient = WebClient.builder().build();

    // 생성자 직접 작성
    public AuthService(UserRepository userRepository,
        SocialAccountRepository socialAccountRepository,
        RefreshTokenRepository refreshTokenRepository,
        JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 소셜 로그인
     */
    @Transactional
    public LoginResponse socialLogin(SocialAccount.Provider provider, String accessToken, String deviceId, String deviceType) {
        // 1. 소셜 로그인 제공자로부터 사용자 정보 가져오기
        Map<String, Object> providerUserInfo = getProviderUserInfo(provider, accessToken);
        String providerId = providerUserInfo.get("id").toString();

        // 2. 소셜 계정 조회
        var socialAccount = socialAccountRepository.findByProviderAndProviderId(provider, providerId);

        if (socialAccount.isEmpty()) {
            // 신규 사용자
            return LoginResponse.builder()
                .isNewUser(true)
                .tempUserId("temp_" + System.currentTimeMillis())
                .providerData(LoginResponse.ProviderData.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .email((String) providerUserInfo.get("email"))
                    .profileImage((String) providerUserInfo.get("profileImage"))
                    .build())
                .build();
        }

        // 3. 기존 사용자
        User user = socialAccount.get().getUser();

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("사용 불가능한 계정입니다.");
        }

        // 4. 토큰 생성
        String jwtAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getUserType().name());
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // 5. 리프레시 토큰 저장
        saveRefreshToken(user.getId(), jwtRefreshToken, deviceId, deviceType);

        return LoginResponse.builder()
            .isNewUser(false)
            .accessToken(jwtAccessToken)
            .refreshToken(jwtRefreshToken)
            .userType(user.getUserType())
            .userId(user.getId())
            .profileComplete(user.getName() != null && user.getPhone() != null)
            .build();
    }

    /**
     * 회원가입
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 1. 전화번호 중복 체크
        if (userRepository.existsByPhone(request.getProfile().getPhone())) {
            throw new RuntimeException("이미 등록된 전화번호입니다.");
        }

        // 2. 필수 약관 체크
        if (!request.getAgreedTerms().getOrDefault("service", false) ||
            !request.getAgreedTerms().getOrDefault("privacy", false) ||
            !request.getAgreedTerms().getOrDefault("location", false)) {
            throw new RuntimeException("필수 약관에 동의하지 않았습니다.");
        }

        // 3. 사용자 생성
        User user = User.builder()
            .id(UUID.randomUUID().toString())
            .userType(request.getUserType())
            .name(request.getProfile().getName())
            .phone(request.getProfile().getPhone())
            .email(request.getProfile().getEmail())
            .profileImage(request.getProfile().getProfileImage())
            .nickname(request.getProfile().getNickname())
            .birthDate(request.getProfile().getBirthDate())
            .gender(request.getProfile().getGender())
            .status(User.UserStatus.ACTIVE)
            .build();

        userRepository.save(user);

        // 4. 소셜 계정 연결
        SocialAccount socialAccount = SocialAccount.builder()
            .user(user)
            .provider(request.getProvider())
            .providerId(request.getSocialId())
            .accessToken(request.getProviderToken())
            .build();

        socialAccountRepository.save(socialAccount);

        // 5. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getUserType().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // 6. 리프레시 토큰 저장
        saveRefreshToken(user.getId(), refreshToken, null, null);

        return SignupResponse.builder()
            .success(true)
            .userId(user.getId())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .redirectUrl("/" + user.getUserType().name().toLowerCase() + "/main")
            .message("회원가입이 완료되었습니다")
            .build();
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public Map<String, String> refreshAccessToken(String refreshToken) {
        // 1. 리프레시 토큰 검증 및 조회
        RefreshToken token = refreshTokenRepository
            .findByTokenAndExpiresAtAfter(refreshToken, LocalDateTime.now())
            .orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

        // 2. 사용자 조회
        User user = token.getUser();

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("사용 불가능한 계정입니다.");
        }

        // 3. 새 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getUserType().name());

        return Map.of("accessToken", newAccessToken);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String userId, String refreshToken) {
        refreshTokenRepository.deleteByUserIdAndToken(userId, refreshToken);
    }

    /**
     * 리프레시 토큰 저장
     */
    private void saveRefreshToken(String userId, String token, String deviceId, String deviceType) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .token(token)
            .deviceId(deviceId)
            .deviceType(deviceType)
            .expiresAt(LocalDateTime.now().plusDays(7))
            .build();

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * 소셜 로그인 제공자로부터 사용자 정보 가져오기
     */
    private Map<String, Object> getProviderUserInfo(SocialAccount.Provider provider, String accessToken) {
        String userInfoUrl = switch (provider) {
            case KAKAO -> "https://kapi.kakao.com/v2/user/me";
            case NAVER -> "https://openapi.naver.com/v1/nid/me";
            case GOOGLE -> "https://www.googleapis.com/oauth2/v3/userinfo";
            default -> throw new RuntimeException("지원하지 않는 소셜 로그인 제공자입니다.");
        };

        Map<String, Object> response = webClient.get()
            .uri(userInfoUrl)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        if (response == null) {
            throw new RuntimeException("소셜 로그인 정보를 가져올 수 없습니다.");
        }

        // Provider별 응답 형식에 맞게 파싱
        return parseProviderResponse(provider, response);
    }

    /**
     * Provider별 응답 파싱
     */
    private Map<String, Object> parseProviderResponse(SocialAccount.Provider provider, Map<String, Object> response) {
        return switch (provider) {
            case KAKAO -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

                yield Map.of(
                    "id", response.get("id").toString(),
                    "email", kakaoAccount.getOrDefault("email", ""),
                    "profileImage", profile.getOrDefault("profile_image_url", "")
                );
            }
            case NAVER -> {
                Map<String, Object> naverResponse = (Map<String, Object>) response.get("response");

                yield Map.of(
                    "id", naverResponse.get("id").toString(),
                    "email", naverResponse.getOrDefault("email", ""),
                    "profileImage", naverResponse.getOrDefault("profile_image", "")
                );
            }
            case GOOGLE -> Map.of(
                "id", response.get("sub").toString(),
                "email", response.getOrDefault("email", ""),
                "profileImage", response.getOrDefault("picture", "")
            );
            default -> throw new RuntimeException("지원하지 않는 제공자입니다.");
        };
    }
}