package com.alba.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "이력서 등록 요청")
public class ResumeCreateRequest {

    @Schema(description = "이력서 제목", example = "바리스타 이력서", required = true)
    @NotBlank(message = "이력서 제목은 필수입니다")
    private String title;

    @Schema(description = "대표 이력서 여부", example = "true")
    private Boolean isDefault = false;

    @Schema(description = "기본 정보")
    private BasicInfo basicInfo;

    @Schema(description = "자기소개", example = "성실하고 밝은 성격입니다")
    private String introduction;

    @Schema(description = "희망 근무 조건")
    private PreferredWork preferredWork;

    @Schema(description = "경력 사항")
    private List<WorkExperienceInfo> workExperience;

    @Schema(description = "보유 스킬", example = "[\"바리스타\", \"라떼아트\"]")
    private List<String> skills;

    @Schema(description = "자격증")
    private List<CertificationInfo> certifications;

    // ── 내부 DTO ─────────────────────────────────────────────────────────────

    @Data
    @Schema(description = "기본 정보")
    public static class BasicInfo {

        @Schema(description = "프로필 사진 URL")
        private String photo;

        @Schema(description = "이름", example = "김민수")
        private String name;

        @Schema(description = "생년월일", example = "2000-03-15")
        private LocalDate birthDate;

        @Schema(description = "성별", example = "M")
        private String gender;

        @Schema(description = "전화번호", example = "010-9876-5432")
        private String phone;

        @Schema(description = "이메일", example = "minsu@example.com")
        private String email;
    }

    @Data
    @Schema(description = "희망 근무 조건")
    public static class PreferredWork {

        @Schema(description = "희망 직종", example = "[\"바리스타\"]")
        private List<String> positions;

        @Schema(description = "희망 근무 지역", example = "[\"강남구\"]")
        private List<String> locations;

        @Schema(description = "희망 근무 요일", example = "[\"월\", \"화\"]")
        private List<String> preferredDays;

        @Schema(description = "희망 근무 시간", example = "09:00-18:00")
        private String preferredTimes;

        @Schema(description = "희망 시급")
        private WageRange expectedWage;
    }

    @Data
    @Schema(description = "희망 시급 범위")
    public static class WageRange {

        @Schema(description = "최소 시급", example = "11000")
        private Integer min;

        @Schema(description = "최대 시급", example = "13000")
        private Integer max;
    }

    @Data
    @Schema(description = "경력 사항")
    public static class WorkExperienceInfo {

        @Schema(description = "사업장 이름", example = "스타벅스", required = true)
        @NotBlank(message = "사업장 이름은 필수입니다")
        private String businessName;

        @Schema(description = "담당 직무", example = "바리스타", required = true)
        @NotBlank(message = "담당 직무는 필수입니다")
        private String position;

        @Schema(description = "시작 연월", example = "2022-01")
        private String startDate;

        @Schema(description = "종료 연월", example = "2024-12")
        private String endDate;

        @Schema(description = "업무 설명", example = "음료 제조 및 매장 관리")
        private String description;
    }

    @Data
    @Schema(description = "자격증")
    public static class CertificationInfo {

        @Schema(description = "자격증 이름", example = "바리스타 2급", required = true)
        @NotBlank(message = "자격증 이름은 필수입니다")
        private String name;

        @Schema(description = "발급 기관", example = "한국커피협회")
        private String issuer;

        @Schema(description = "취득일", example = "2022-06-15")
        private LocalDate issueDate;
    }
}