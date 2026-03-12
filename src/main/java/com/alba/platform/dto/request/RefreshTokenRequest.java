package com.alba.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "토큰 갱신 요청")
public class RefreshTokenRequest {

    @Schema(description = "리프레시 토큰", example = "refresh_token_here", required = true)
    @NotBlank(message = "리프레시 토큰은 필수입니다")
    private String refreshToken;
}