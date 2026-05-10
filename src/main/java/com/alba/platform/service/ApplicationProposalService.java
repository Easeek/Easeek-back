package com.alba.platform.service;

import com.alba.platform.dto.response.ApplicationResponse;
import com.alba.platform.dto.response.PostResponse;
import com.alba.platform.dto.response.ProposalResponse;
import com.alba.platform.entity.Application;
import com.alba.platform.entity.Post;
import com.alba.platform.entity.Proposal;
import com.alba.platform.repository.ApplicationRepository;
import com.alba.platform.repository.PostRepository;
import com.alba.platform.repository.ProposalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApplicationProposalService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationProposalService.class);

    private final ApplicationRepository applicationRepository;
    private final ProposalRepository proposalRepository;
    private final PostRepository postRepository;

    public ApplicationProposalService(
            ApplicationRepository applicationRepository,
            ProposalRepository proposalRepository,
            PostRepository postRepository) {
        this.applicationRepository = applicationRepository;
        this.proposalRepository = proposalRepository;
        this.postRepository = postRepository;
    }

    // ── 1. 사장: 받은 지원 목록 ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getReceivedApplications(String userId, String status, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

        Page<Application> result;
        if (status != null && !status.isBlank()) {
            Application.ApplicationStatus appStatus = Application.ApplicationStatus.valueOf(status.toUpperCase());
            result = applicationRepository.findByCeoIdAndStatus(userId, appStatus, pageable);
        } else {
            result = applicationRepository.findByCeoId(userId, pageable);
        }

        List<ApplicationResponse> applications = result.getContent().stream()
                .map(this::toApplicationResponse)
                .collect(Collectors.toList());

        // 통계
        int total = applicationRepository.countByCeoId(userId);
        int pending = applicationRepository.countByCeoIdAndStatus(userId, Application.ApplicationStatus.PENDING);
        int accepted = applicationRepository.countByCeoIdAndStatus(userId, Application.ApplicationStatus.ACCEPTED);
        int rejected = applicationRepository.countByCeoIdAndStatus(userId, Application.ApplicationStatus.REJECTED);

        log.info("받은 지원 목록 조회 - userId: {}", userId);

        return Map.of(
                "totalCount", result.getTotalElements(),
                "applications", applications,
                "pagination", Map.of(
                        "currentPage", page,
                        "totalPages", result.getTotalPages(),
                        "hasNext", result.hasNext()
                ),
                "stats", Map.of(
                        "total", total,
                        "pending", pending,
                        "accepted", accepted,
                        "rejected", rejected
                )
        );
    }

    // ── 2. 알바: 내가 지원한 목록 ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getSentApplications(String userId, String status, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

        Page<Application> result;
        if (status != null && !status.isBlank()) {
            Application.ApplicationStatus appStatus = Application.ApplicationStatus.valueOf(status.toUpperCase());
            result = applicationRepository.findByApplicantIdAndStatus(userId, appStatus, pageable);
        } else {
            result = applicationRepository.findByApplicantId(userId, pageable);
        }

        List<ApplicationResponse> applications = result.getContent().stream()
                .map(this::toApplicationResponse)
                .collect(Collectors.toList());

        int total = applicationRepository.countByApplicantId(userId);
        int pending = applicationRepository.countByApplicantIdAndStatus(userId, Application.ApplicationStatus.PENDING);
        int accepted = applicationRepository.countByApplicantIdAndStatus(userId, Application.ApplicationStatus.ACCEPTED);
        int rejected = applicationRepository.countByApplicantIdAndStatus(userId, Application.ApplicationStatus.REJECTED);

        log.info("지원한 목록 조회 - userId: {}", userId);

        return Map.of(
                "totalCount", result.getTotalElements(),
                "applications", applications,
                "pagination", Map.of(
                        "currentPage", page,
                        "totalPages", result.getTotalPages(),
                        "hasNext", result.hasNext()
                ),
                "stats", Map.of(
                        "total", total,
                        "pending", pending,
                        "accepted", accepted,
                        "rejected", rejected
                )
        );
    }

    // ── 3. 알바: 지원 내역 히스토리 ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getApplicationHistory(String userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Application> result = applicationRepository.findByApplicantId(userId, pageable);

        List<Map<String, Object>> applications = result.getContent().stream()
                .map(app -> Map.of(
                        "applicationId", app.getId(),
                        "job", Map.of(
                                "jobId", app.getJobId(),
                                "title", app.getJobTitle(),
                                "businessName", app.getBusinessName() != null ? app.getBusinessName() : ""
                        ),
                        "status", app.getStatus().name().toLowerCase(),
                        "appliedAt", app.getCreatedAt().toString(),
                        "respondedAt", app.getRespondedAt() != null ? app.getRespondedAt().toString() : ""
                ))
                .collect(Collectors.toList());

        log.info("지원 내역 히스토리 조회 - userId: {}", userId);

        return Map.of(
                "totalCount", result.getTotalElements(),
                "applications", applications,
                "pagination", Map.of(
                        "currentPage", page,
                        "totalPages", result.getTotalPages(),
                        "hasNext", result.hasNext()
                )
        );
    }

    // ── 4. 사장: 보낸 제안 목록 ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getSentProposals(String userId, String status, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

        Page<Proposal> result;
        if (status != null && !status.isBlank()) {
            Proposal.ProposalStatus propStatus = Proposal.ProposalStatus.valueOf(status.toUpperCase());
            result = proposalRepository.findBySenderIdAndStatus(userId, propStatus, pageable);
        } else {
            result = proposalRepository.findBySenderId(userId, pageable);
        }

        List<ProposalResponse> proposals = result.getContent().stream()
                .map(this::toProposalResponse)
                .collect(Collectors.toList());

        int total = proposalRepository.countBySenderId(userId);
        int pending = proposalRepository.countBySenderIdAndStatus(userId, Proposal.ProposalStatus.PENDING);
        int accepted = proposalRepository.countBySenderIdAndStatus(userId, Proposal.ProposalStatus.ACCEPTED);

        log.info("보낸 제안 목록 조회 - userId: {}", userId);

        return Map.of(
                "totalCount", result.getTotalElements(),
                "proposals", proposals,
                "pagination", Map.of(
                        "currentPage", page,
                        "totalPages", result.getTotalPages(),
                        "hasNext", result.hasNext()
                ),
                "stats", Map.of(
                        "total", total,
                        "pending", pending,
                        "accepted", accepted
                )
        );
    }

    // ── 5. 알바: 받은 제안 목록 ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getReceivedProposals(String userId, String status, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

        Page<Proposal> result;
        if (status != null && !status.isBlank()) {
            Proposal.ProposalStatus propStatus = Proposal.ProposalStatus.valueOf(status.toUpperCase());
            result = proposalRepository.findByRecipientIdAndStatus(userId, propStatus, pageable);
        } else {
            result = proposalRepository.findByRecipientId(userId, pageable);
        }

        List<ProposalResponse> proposals = result.getContent().stream()
                .map(this::toProposalResponse)
                .collect(Collectors.toList());

        int total = proposalRepository.countByRecipientId(userId);
        int pending = proposalRepository.countByRecipientIdAndStatus(userId, Proposal.ProposalStatus.PENDING);
        int accepted = proposalRepository.countByRecipientIdAndStatus(userId, Proposal.ProposalStatus.ACCEPTED);
        int rejected = proposalRepository.countByRecipientIdAndStatus(userId, Proposal.ProposalStatus.REJECTED);

        log.info("받은 제안 목록 조회 - userId: {}", userId);

        return Map.of(
                "totalCount", result.getTotalElements(),
                "proposals", proposals,
                "pagination", Map.of(
                        "currentPage", page,
                        "totalPages", result.getTotalPages(),
                        "hasNext", result.hasNext()
                ),
                "stats", Map.of(
                        "total", total,
                        "pending", pending,
                        "accepted", accepted,
                        "rejected", rejected
                )
        );
    }

    // ── 6. 내가 작성한 글 목록 ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getMyPosts(String userId, String category, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

        Page<Post> result;
        if (category != null && !category.isBlank()) {
            result = postRepository.findByAuthorIdAndCategoryAndStatus(userId, category, Post.PostStatus.PUBLISHED, pageable);
        } else {
            result = postRepository.findByAuthorIdAndStatus(userId, Post.PostStatus.PUBLISHED, pageable);
        }

        List<PostResponse> posts = result.getContent().stream()
                .map(this::toPostResponse)
                .collect(Collectors.toList());

        log.info("내 게시글 목록 조회 - userId: {}", userId);

        return Map.of(
                "totalCount", result.getTotalElements(),
                "posts", posts,
                "pagination", Map.of(
                        "currentPage", page,
                        "totalPages", result.getTotalPages(),
                        "hasNext", result.hasNext()
                )
        );
    }

    // ── Private 변환 메서드 ───────────────────────────────────────────────────

    private ApplicationResponse toApplicationResponse(Application app) {
        return ApplicationResponse.builder()
                .applicationId(app.getId())
                .job(ApplicationResponse.JobInfo.builder()
                        .jobId(app.getJobId())
                        .title(app.getJobTitle())
                        .position(app.getJobPosition())
                        .build())
                .applicant(ApplicationResponse.ApplicantInfo.builder()
                        .userId(app.getApplicant().getId())
                        .name(app.getApplicant().getName())
                        .profileImage(app.getApplicant().getProfileImage())
                        .build())
                .resume(app.getResumeId() != null ? ApplicationResponse.ResumeInfo.builder()
                        .resumeId(app.getResumeId())
                        .title(app.getResumeTitle())
                        .build() : null)
                .status(app.getStatus().name().toLowerCase())
                .coverLetter(app.getCoverLetter())
                .preferredStartDate(app.getPreferredStartDate())
                .expectedWage(app.getExpectedWage())
                .appliedAt(app.getCreatedAt())
                .viewedAt(app.getViewedAt())
                .respondedAt(app.getRespondedAt())
                .daysElapsed((int) ChronoUnit.DAYS.between(app.getCreatedAt(), LocalDateTime.now()))
                .build();
    }

    private ProposalResponse toProposalResponse(Proposal prop) {
        long daysRemaining = prop.getExpiresAt() != null
                ? ChronoUnit.DAYS.between(LocalDateTime.now(), prop.getExpiresAt())
                : 0;

        return ProposalResponse.builder()
                .proposalId(prop.getId())
                .job(ProposalResponse.JobInfo.builder()
                        .jobId(prop.getJobId())
                        .title(prop.getJobTitle())
                        .build())
                .sender(ProposalResponse.UserInfo.builder()
                        .userId(prop.getSender().getId())
                        .name(prop.getSender().getName())
                        .profileImage(prop.getSender().getProfileImage())
                        .businessName(prop.getBusinessName())
                        .build())
                .recipient(ProposalResponse.UserInfo.builder()
                        .userId(prop.getRecipient().getId())
                        .name(prop.getRecipient().getName())
                        .profileImage(prop.getRecipient().getProfileImage())
                        .build())
                .status(prop.getStatus().name().toLowerCase())
                .message(prop.getMessage())
                .conditions(ProposalResponse.Conditions.builder()
                        .hourlyWage(prop.getHourlyWage())
                        .workDays(prop.getWorkDays() != null
                                ? Arrays.asList(prop.getWorkDays().split(","))
                                : List.of())
                        .startDate(prop.getStartDate())
                        .build())
                .sentAt(prop.getCreatedAt())
                .viewedAt(prop.getViewedAt())
                .expiresAt(prop.getExpiresAt())
                .daysRemaining((int) Math.max(0, daysRemaining))
                .build();
    }

    private PostResponse toPostResponse(Post post) {
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .contentPreview(post.getContent() != null && post.getContent().length() > 100
                        ? post.getContent().substring(0, 100) + "..."
                        : post.getContent())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .viewCount(post.getViewCount())
                .status(post.getStatus().name().toLowerCase())
                .createdAt(post.getCreatedAt())
                .build();
    }
}