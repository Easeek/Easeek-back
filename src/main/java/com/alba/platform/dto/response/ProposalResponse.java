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
@Schema(description = "제안 응답")
public class ProposalResponse {

    private String proposalId;
    private JobInfo job;
    private UserInfo sender;
    private UserInfo recipient;
    private String status;
    private String message;
    private Conditions conditions;
    private LocalDateTime sentAt;
    private LocalDateTime viewedAt;
    private LocalDateTime expiresAt;
    private Integer daysRemaining;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobInfo {
        private String jobId;
        private String title;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String userId;
        private String name;
        private String profileImage;
        private String businessName;
        private Integer trustScore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Conditions {
        private Integer hourlyWage;
        private List<String> workDays;
        private LocalDate startDate;
    }
}