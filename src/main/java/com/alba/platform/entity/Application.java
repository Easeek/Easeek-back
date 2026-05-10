package com.alba.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Application {

    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;

    // 지원자 (알바)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    // 채용공고 ID (Job 엔티티 없으므로 String으로 저장)
    @Column(name = "job_id", nullable = false, length = 36)
    private String jobId;

    @Column(name = "job_title", nullable = false, length = 100)
    private String jobTitle;

    @Column(name = "job_position", length = 100)
    private String jobPosition;

    @Column(name = "business_name", length = 100)
    private String businessName;

    // 사장 userId
    @Column(name = "ceo_id", length = 36)
    private String ceoId;

    // 사용한 이력서
    @Column(name = "resume_id", length = 36)
    private String resumeId;

    @Column(name = "resume_title", length = 100)
    private String resumeTitle;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Column(name = "preferred_start_date")
    private LocalDate preferredStartDate;

    @Column(name = "expected_wage")
    private Integer expectedWage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ApplicationStatus {
        PENDING, ACCEPTED, REJECTED
    }
}