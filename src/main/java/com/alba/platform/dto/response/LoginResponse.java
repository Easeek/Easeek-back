package com.alba.platform.dto.response;

import com.alba.platform.entity.SocialAccount;
import com.alba.platform.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 응답")
public class LoginResponse {

    @Schema(description = "신규 사용자 여부", example = "false")
    private Boolean isNewUser;

    @Schema(description = "액세스 토큰")
    private String accessToken;

    @Schema(description = "리프레시 토큰")
    private String refreshToken;

    @Schema(description = "사용자 타입", example = "CEO")
    private User.UserType userType;

    @Schema(description = "사용자 ID")
    private String userId;

    @Schema(description = "프로필 완성 여부", example = "true")
    private Boolean profileComplete;

    @Schema(description = "임시 사용자 ID (신규 사용자인 경우)")
    private String tempUserId;

    @Schema(description = "소셜 로그인 제공자 데이터 (신규 사용자인 경우)")
    private ProviderData providerData;

    @Data
    @Builder
    @Schema(description = "소셜 로그인 제공자 데이터")
    public static class ProviderData {
        @Schema(description = "제공자", example = "KAKAO")
        private SocialAccount.Provider provider;

        @Schema(description = "제공자 ID")
        private String providerId;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "프로필 이미지 URL")
        private String profileImage;
    }
}