package com.alba.platform.dto.response;

import com.alba.platform.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로필 조회 응답")
public class ProfileResponse {

    @Schema(description = "사용자 ID")
    private String userId;

    @Schema(description = "사용자 타입", example = "ceo")
    private String userType;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "전화번호", example = "01012345678")
    private String phone;

    @Schema(description = "이메일", example = "hong@example.com")
    private String email;

    @Schema(description = "프로필 이미지 URL")
    private String profileImage;

    @Schema(description = "닉네임", example = "길동이")
    private String nickname;

    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "성별", example = "M")
    private User.Gender gender;

    @Schema(description = "신뢰지수", example = "87")
    private Integer trustScore;

    @Schema(description = "레벨", example = "신뢰")
    private String level;

    @Schema(description = "사업장 정보")
    private BusinessInfo business;

    @Schema(description = "통계 정보")
    private StatsInfo stats;

    @Schema(description = "배지 목록")
    private List<BadgeInfo> badges;

    @Schema(description = "가입일", example = "2025-06-15")
    private LocalDate joinedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessInfo {
        private String businessId;
        private String businessName;
        private Boolean verified;
        private String address;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsInfo {
        private Integer totalPosts;
        private Integer totalJobs;
        private Integer activeJobs;
        private Integer totalHires;
        private Integer reviewCount;
        private Double averageRating;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BadgeInfo {
        private String id;
        private String name;
    }
}