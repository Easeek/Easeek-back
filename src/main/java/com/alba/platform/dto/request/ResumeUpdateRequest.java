package com.alba.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "이력서 수정 요청")
public class ResumeUpdateRequest {

    @Schema(description = "이력서 제목", example = "바리스타 이력서 수정")
    private String title;

    @Schema(description = "대표 이력서 여부", example = "true")
    private Boolean isDefault;

    @Schema(description = "기본 정보")
    private BasicInfo basicInfo;

    @Schema(description = "자기소개", example = "2년 경력 바리스타입니다")
    private String introduction;

    @Schema(description = "희망 근무 조건")
    private PreferredWork preferredWork;

    @Schema(description = "경력 사항 (전체 교체)")
    private List<WorkExperienceInfo> workExperience;

    @Schema(description = "보유 스킬", example = "[\"바리스타\", \"라떼아트\"]")
    private List<String> skills;

    @Schema(description = "자격증 (전체 교체)")
    private List<CertificationInfo> certifications;

    // ── 내부 DTO ─────────────────────────────────────────────────────────────

    @Data
    @Schema(description = "기본 정보")
    public static class BasicInfo {
        private String photo;
        private String name;
        private LocalDate birthDate;
        private String gender;
        private String phone;
        private String email;
    }

    @Data
    @Schema(description = "희망 근무 조건")
    public static class PreferredWork {
        private List<String> positions;
        private List<String> locations;
        private List<String> preferredDays;
        private String preferredTimes;
        private WageRange expectedWage;
    }

    @Data
    @Schema(description = "희망 시급 범위")
    public static class WageRange {
        private Integer min;
        private Integer max;
    }

    @Data
    @Schema(description = "경력 사항")
    public static class WorkExperienceInfo {
        private String businessName;
        private String position;
        private String startDate;
        private String endDate;
        private String description;
    }

    @Data
    @Schema(description = "자격증")
    public static class CertificationInfo {
        private String name;
        private String issuer;
        private LocalDate issueDate;
    }
}