package com.alba.platform.controller;

import com.alba.platform.dto.request.ProfileCreateRequest;
import com.alba.platform.dto.response.ApiResponse;
import com.alba.platform.dto.response.ProfileResponse;
import com.alba.platform.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "프로필", description = "프로필 관련 API")
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(summary = "프로필 등록", description = "회원가입 후 프로필 정보를 등록합니다.")
    @PostMapping("/user/profile")
    public ApiResponse<Map<String, Object>> createProfile(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody ProfileCreateRequest request
    ) {
        try {
            ProfileResponse response = profileService.createOrUpdateProfile(userId, request);

            return ApiResponse.success(
                    Map.of(
                            "success", true,
                            "userId", response.getUserId()
                    ),
                    "프로필 등록 완료"
            );
        } catch (Exception e) {
            log.error("프로필 등록 실패", e);

            if (e.getMessage().contains("전화번호")) {
                return ApiResponse.error(e.getMessage(), "PROFILE_001");
            } else if (e.getMessage().contains("이메일")) {
                return ApiResponse.error(e.getMessage(), "PROFILE_002");
            }

            return ApiResponse.error(e.getMessage(), "PROFILE_003");
        }
    }

    @Operation(summary = "프로필 조회", description = "내 프로필 정보를 조회합니다.")
    @GetMapping("/profile")
    public ApiResponse<ProfileResponse> getProfile(@AuthenticationPrincipal String userId) {
        try {
            ProfileResponse response = profileService.getProfile(userId);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("프로필 조회 실패", e);
            return ApiResponse.error(e.getMessage(), "PROFILE_004");
        }
    }
}