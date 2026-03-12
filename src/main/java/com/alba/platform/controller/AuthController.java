package com.alba.platform.controller;

import com.alba.platform.dto.request.RefreshTokenRequest;
import com.alba.platform.dto.request.SignupRequest;
import com.alba.platform.dto.request.SocialLoginRequest;
import com.alba.platform.dto.response.ApiResponse;
import com.alba.platform.dto.response.LoginResponse;
import com.alba.platform.dto.response.SignupResponse;
import com.alba.platform.dto.response.TermResponse;
import com.alba.platform.entity.Term;
import com.alba.platform.repository.TermRepository;
import com.alba.platform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "인증", description = "인증 관련 API")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final TermRepository termRepository;

    // 생성자 직접 작성
    public AuthController(AuthService authService, TermRepository termRepository) {
        this.authService = authService;
        this.termRepository = termRepository;
    }

    @Operation(summary = "스플래시 화면", description = "앱 시작 시 스플래시 화면 정보 조회")
    @GetMapping("/splash")
    public ApiResponse<Map<String, Object>> getSplash() {
        Map<String, Object> splashData = new HashMap<>();
        splashData.put("version", "1.0.0");
        splashData.put("logoUrl", "https://cdn.example.com/logo.png");
        splashData.put("minimumVersion", "1.0.0");
        splashData.put("updateRequired", false);
        splashData.put("maintenanceMode", false);

        return ApiResponse.<Map<String, Object>>success(splashData);
    }

    @Operation(summary = "가입유형 선택", description = "회원가입 시 선택 가능한 사용자 유형 목록 조회")
    @GetMapping("/signup/types")
    public ApiResponse<Map<String, Object>> getSignupTypes() {
        List<Map<String, String>> types = List.of(
            Map.of(
                "id", "CEO",
                "name", "사장",
                "description", "사업장을 운영하는 사장님"
            ),
            Map.of(
                "id", "PARTTIME",
                "name", "알바",
                "description", "아르바이트를 찾는 구직자"
            )
        );

        Map<String, Object> result = Map.of("types", types);
        return ApiResponse.<Map<String, Object>>success(result);
    }

    @Operation(summary = "약관 조회", description = "서비스 약관 목록 조회")
    @GetMapping("/terms")
    public ApiResponse<Map<String, List<TermResponse>>> getTerms() {
        List<Term> terms = termRepository.findAllByOrderByRequiredDescIdAsc();

        List<TermResponse> termResponses = terms.stream()
            .map(term -> TermResponse.builder()
                .id(term.getId())
                .title(term.getTitle())
                .content(term.getContent())
                .version(term.getVersion())
                .required(term.getRequired())
                .createdAt(term.getCreatedAt())
                .updatedAt(term.getUpdatedAt())
                .build())
            .toList();

        Map<String, List<TermResponse>> result = Map.of("terms", termResponses);
        return ApiResponse.<Map<String, List<TermResponse>>>success(result);
    }

    @Operation(summary = "소셜 로그인", description = "카카오, 네이버, 구글 등 소셜 로그인")
    @PostMapping("/auth/social")
    public ApiResponse<LoginResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        try {
            LoginResponse response = authService.socialLogin(
                request.getProvider(),
                request.getAccessToken(),
                request.getDeviceId(),
                request.getDeviceType()
            );

            return ApiResponse.<LoginResponse>success(response);
        } catch (Exception e) {
            log.error("소셜 로그인 실패", e);
            return ApiResponse.error(e.getMessage(), "AUTH_001");
        }
    }

    @Operation(summary = "회원가입", description = "회원가입 및 자동 로그인")
    @PostMapping("/auth/signup")
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        try {
            SignupResponse response = authService.signup(request);
            return ApiResponse.<SignupResponse>success(response, "회원가입이 완료되었습니다");
        } catch (Exception e) {
            log.error("회원가입 실패", e);

            if (e.getMessage().contains("전화번호")) {
                return ApiResponse.error(e.getMessage(), "SIGNUP_001");
            } else if (e.getMessage().contains("약관")) {
                return ApiResponse.error(e.getMessage(), "SIGNUP_002");
            }

            return ApiResponse.error(e.getMessage(), "SIGNUP_003");
        }
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰 발급")
    @PostMapping("/auth/refresh")
    public ApiResponse<Map<String, String>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            Map<String, String> tokens = authService.refreshAccessToken(request.getRefreshToken());
            return ApiResponse.<Map<String, String>>success(tokens);
        } catch (Exception e) {
            log.error("토큰 갱신 실패", e);
            return ApiResponse.error("유효하지 않은 리프레시 토큰입니다", "AUTH_002");
        }
    }

    @Operation(summary = "로그아웃", description = "로그아웃 및 리프레시 토큰 삭제")
    @PostMapping("/auth/logout")
    public ApiResponse<Map<String, Boolean>> logout(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody RefreshTokenRequest request
    ) {
        try {
            authService.logout(userId, request.getRefreshToken());
            return ApiResponse.<Map<String, Boolean>>success(Map.of("success", true), "로그아웃되었습니다");
        } catch (Exception e) {
            log.error("로그아웃 실패", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    @Operation(summary = "회원가입 완료 화면", description= "회원가입 완료 화면 정보 조회")
    @GetMapping("/signup/complete")
    public ApiResponse<Map<String, Object>> getSignupComplete(@AuthenticationPrincipal String userId) {
        Map<String, Object> completeData = new HashMap<>();
        completeData.put("message", "회원가입이 완료되었습니다!");
        completeData.put("benefits", List.of("신뢰지수 +10점", "첫 채용공고 무료", "프리미엄 7일 무료"));
        completeData.put("nextSteps", List.of(
            Map.of("title", "사업장 정보 등록", "description", "사업장 정보를 등록하세요", "url", "/business/register")
        ));

        return ApiResponse.success(completeData);
    }
}