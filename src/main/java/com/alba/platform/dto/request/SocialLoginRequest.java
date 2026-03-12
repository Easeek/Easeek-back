package com.alba.platform.dto.request;

import com.alba.platform.entity.SocialAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "소셜 로그인 요청")
public class SocialLoginRequest {

    @Schema(description = "소셜 로그인 제공자", example = "KAKAO", required = true)
    @NotNull(message = "Provider는 필수입니다")
    private SocialAccount.Provider provider;

    @Schema(description = "소셜 로그인 액세스 토큰", example = "kakao_access_token_here", required = true)
    @NotBlank(message = "Access Token은 필수입니다")
    private String accessToken;

    @Schema(description = "기기 ID", example = "device_12345")
    private String deviceId;

    @Schema(description = "기기 타입", example = "iOS")
    private String deviceType;
}