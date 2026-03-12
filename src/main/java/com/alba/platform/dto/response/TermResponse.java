package com.alba.platform.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "약관 정보")
public class TermResponse {

    @Schema(description = "약관 ID", example = "service")
    private String id;

    @Schema(description = "약관 제목", example = "서비스 이용약관")
    private String title;

    @Schema(description = "약관 내용")
    private String content;

    @Schema(description = "약관 버전", example = "1.0")
    private String version;

    @Schema(description = "필수 약관 여부", example = "true")
    private Boolean required;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시")
    private LocalDateTime updatedAt;
}