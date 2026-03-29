package com.alba.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Resume {

    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;

    // User와 N:1 관계 (한 유저가 여러 이력서 보유 가능)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    // 대표 이력서 여부
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    // 기본 정보
    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(length = 200)
    private String introduction;

    // 희망 근무 조건
    @Column(name = "preferred_positions", length = 500)
    private String preferredPositions; // JSON 배열을 String으로 저장 ex) "바리스타,서빙"

    @Column(name = "preferred_locations", length = 500)
    private String preferredLocations; // ex) "강남구,서초구"

    @Column(name = "preferred_days", length = 200)
    private String preferredDays; // ex) "월,화,수"

    @Column(name = "preferred_times", length = 100)
    private String preferredTimes; // ex) "09:00-18:00"

    @Column(name = "expected_wage_min")
    private Integer expectedWageMin;

    @Column(name = "expected_wage_max")
    private Integer expectedWageMax;

    // 스킬 (쉼표 구분 문자열)
    @Column(length = 500)
    private String skills; // ex) "바리스타,라떼아트"

    // 통계
    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "application_count")
    @Builder.Default
    private Integer applicationCount = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ResumeStatus status = ResumeStatus.ACTIVE;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 경력 사항 (1:N 관계)
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkExperience> workExperiences = new ArrayList<>();

    // 자격증 (1:N 관계)
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Certification> certifications = new ArrayList<>();

    public enum ResumeStatus {
        ACTIVE, DELETED
    }

    // ── 경력 사항 내부 Entity ──────────────────────────────────────────────────
    @Entity
    @Table(name = "work_experiences")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EntityListeners(AuditingEntityListener.class)
    public static class WorkExperience {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "resume_id", nullable = false)
        private Resume resume;

        @Column(name = "business_name", nullable = false, length = 100)
        private String businessName;

        @Column(nullable = false, length = 100)
        private String position;

        @Column(name = "start_date", length = 7) // ex) "2022-01"
        private String startDate;

        @Column(name = "end_date", length = 7) // ex) "2024-12"
        private String endDate;

        @Column(length = 500)
        private String description;
    }

    // ── 자격증 내부 Entity ────────────────────────────────────────────────────
    @Entity
    @Table(name = "certifications")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EntityListeners(AuditingEntityListener.class)
    public static class Certification {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "resume_id", nullable = false)
        private Resume resume;

        @Column(nullable = false, length = 100)
        private String name;

        @Column(length = 100)
        private String issuer;

        @Column(name = "issue_date")
        private LocalDate issueDate;
    }
}