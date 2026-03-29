package com.alba.platform.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이력서 응답")
public class ResumeResponse {

    @Schema(description = "이력서 ID")
    private String resumeId;

    @Schema(description = "이력서 제목", example = "바리스타 이력서")
    private String title;

    @Schema(description = "대표 이력서 여부", example = "true")
    private Boolean isDefault;

    @Schema(description = "기본 정보")
    private BasicInfo basicInfo;

    @Schema(description = "자기소개")
    private String introduction;

    @Schema(description = "희망 근무 조건")
    private PreferredWork preferredWork;

    @Schema(description = "경력 사항")
    private List<WorkExperienceInfo> workExperience;

    @Schema(description = "보유 스킬")
    private List<String> skills;

    @Schema(description = "자격증")
    private List<CertificationInfo> certifications;

    @Schema(description = "통계")
    private StatsInfo stats;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    // ── 내부 DTO ─────────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicInfo {
        private String photo;
        private String name;
        private LocalDate birthDate;
        private String gender;
        private String phone;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreferredWork {
        private List<String> positions;
        private List<String> locations;
        private List<String> preferredDays;
        private String preferredTimes;
        private Integer expectedWageMin;
        private Integer expectedWageMax;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkExperienceInfo {
        private Long id;
        private String businessName;
        private String position;
        private String startDate;
        private String endDate;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertificationInfo {
        private Long id;
        private String name;
        private String issuer;
        private LocalDate issueDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsInfo {
        private Integer viewCount;
        private Integer applicationCount;
    }
}