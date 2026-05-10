package com.alba.platform.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "지원 응답")
public class ApplicationResponse {

    private String applicationId;
    private JobInfo job;
    private ApplicantInfo applicant;
    private ResumeInfo resume;
    private String status;
    private String coverLetter;
    private LocalDate preferredStartDate;
    private Integer expectedWage;
    private LocalDateTime appliedAt;
    private LocalDateTime viewedAt;
    private LocalDateTime respondedAt;
    private Integer daysElapsed;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobInfo {
        private String jobId;
        private String title;
        private String position;
        private String businessName;
        private Integer trustScore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicantInfo {
        private String userId;
        private String name;
        private String profileImage;
        private Integer age;
        private Integer trustScore;
        private Integer experienceYears;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumeInfo {
        private String resumeId;
        private String title;
    }
}