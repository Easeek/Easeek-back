package com.alba.platform.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 응답")
public class SignupResponse {

    @Schema(description = "성공 여부", example = "true")
    private Boolean success;

    @Schema(description = "사용자 ID")
    private String userId;

    @Schema(description = "액세스 토큰")
    private String accessToken;

    @Schema(description = "리프레시 토큰")
    private String refreshToken;

    @Schema(description = "리다이렉트 URL", example = "/ceo/main")
    private String redirectUrl;

    @Schema(description = "메시지", example = "회원가입이 완료되었습니다")
    private String message;
}