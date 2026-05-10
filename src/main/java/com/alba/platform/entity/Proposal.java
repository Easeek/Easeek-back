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
@Table(name = "proposals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Proposal {

    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;

    // 제안 보낸 사장
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // 제안 받은 알바
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    // 채용공고 정보
    @Column(name = "job_id", length = 36)
    private String jobId;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "business_name", length = 100)
    private String businessName;

    @Column(columnDefinition = "TEXT")
    private String message;

    // 근무 조건
    @Column(name = "hourly_wage")
    private Integer hourlyWage;

    @Column(name = "work_days", length = 200)
    private String workDays; // ex) "토,일"

    @Column(name = "start_date")
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    @Builder.Default
    private ProposalStatus status = ProposalStatus.PENDING;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ProposalStatus {
        PENDING, ACCEPTED, REJECTED
    }
}