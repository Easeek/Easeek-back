package com.alba.platform.dto.request;

import com.alba.platform.entity.SocialAccount;
import com.alba.platform.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Schema(description = "회원가입 요청")
public class SignupRequest {

    @Schema(description = "사용자 타입", example = "CEO", required = true)
    @NotNull(message = "사용자 타입은 필수입니다")
    private User.UserType userType;

    @Schema(description = "소셜 ID", example = "kakao_123456", required = true)
    @NotBlank(message = "소셜 ID는 필수입니다")
    private String socialId;

    @Schema(description = "소셜 로그인 제공자", example = "KAKAO", required = true)
    @NotNull(message = "Provider는 필수입니다")
    private SocialAccount.Provider provider;

    @Schema(description = "프로필 정보", required = true)
    @NotNull(message = "프로필 정보는 필수입니다")
    private ProfileInfo profile;

    @Schema(description = "약관 동의 정보", required = true)
    @NotNull(message = "약관 동의는 필수입니다")
    private Map<String, Boolean> agreedTerms;

    @Schema(description = "소셜 로그인 토큰")
    private String providerToken;

    @Data
    @Schema(description = "프로필 정보")
    public static class ProfileInfo {
        @Schema(description = "이름", example = "홍길동", required = true)
        @NotBlank(message = "이름은 필수입니다")
        private String name;

        @Schema(description = "전화번호", example = "01012345678", required = true)
        @NotBlank(message = "전화번호는 필수입니다")
        private String phone;

        @Schema(description = "이메일", example = "hong@example.com")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        private String email;

        @Schema(description = "프로필 이미지 URL")
        private String profileImage;

        @Schema(description = "닉네임", example = "길동이")
        private String nickname;

        @Schema(description = "생년월일", example = "1990-01-01")
        private LocalDate birthDate;

        @Schema(description = "성별", example = "M")
        private User.Gender gender;
    }
}