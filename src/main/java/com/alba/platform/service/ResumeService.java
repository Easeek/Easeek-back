package com.alba.platform.service;

import com.alba.platform.dto.request.ResumeCreateRequest;
import com.alba.platform.dto.request.ResumeUpdateRequest;
import com.alba.platform.dto.response.ResumeResponse;
import com.alba.platform.entity.Resume;
import com.alba.platform.entity.User;
import com.alba.platform.repository.ResumeRepository;
import com.alba.platform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private static final Logger log = LoggerFactory.getLogger(ResumeService.class);

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    // ── 1. 이력서 등록 ────────────────────────────────────────────────────────

    @Transactional
    public ResumeResponse createResume(String userId, ResumeCreateRequest request) {
        User user = findActiveUser(userId);

        // isDefault=true 로 등록하면 기존 대표 이력서 해제
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefaultResume(userId);
        }

        Resume resume = Resume.builder()
                .user(user)
                .title(request.getTitle())
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()))
                .introduction(request.getIntroduction())
                .build();

        // basicInfo 매핑
        if (request.getBasicInfo() != null) {
            resume.setPhotoUrl(request.getBasicInfo().getPhoto());
        }

        // 희망 근무 조건 매핑
        if (request.getPreferredWork() != null) {
            ResumeCreateRequest.PreferredWork pw = request.getPreferredWork();
            resume.setPreferredPositions(listToString(pw.getPositions()));
            resume.setPreferredLocations(listToString(pw.getLocations()));
            resume.setPreferredDays(listToString(pw.getPreferredDays()));
            resume.setPreferredTimes(pw.getPreferredTimes());
            if (pw.getExpectedWage() != null) {
                resume.setExpectedWageMin(pw.getExpectedWage().getMin());
                resume.setExpectedWageMax(pw.getExpectedWage().getMax());
            }
        }

        // 스킬 매핑
        resume.setSkills(listToString(request.getSkills()));

        Resume saved = resumeRepository.save(resume);

        // 경력 사항 저장
        if (request.getWorkExperience() != null) {
            List<Resume.WorkExperience> experiences = request.getWorkExperience().stream()
                    .map(exp -> Resume.WorkExperience.builder()
                            .resume(saved)
                            .businessName(exp.getBusinessName())
                            .position(exp.getPosition())
                            .startDate(exp.getStartDate())
                            .endDate(exp.getEndDate())
                            .description(exp.getDescription())
                            .build())
                    .collect(Collectors.toList());
            saved.getWorkExperiences().addAll(experiences);
        }

        // 자격증 저장
        if (request.getCertifications() != null) {
            List<Resume.Certification> certs = request.getCertifications().stream()
                    .map(cert -> Resume.Certification.builder()
                            .resume(saved)
                            .name(cert.getName())
                            .issuer(cert.getIssuer())
                            .issueDate(cert.getIssueDate())
                            .build())
                    .collect(Collectors.toList());
            saved.getCertifications().addAll(certs);
        }

        Resume finalSaved = resumeRepository.save(saved);
        log.info("이력서 등록 완료 - userId: {}, resumeId: {}", userId, finalSaved.getId());

        return toResponse(finalSaved);
    }

    // ── 2. 이력서 목록 조회 ───────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ResumeResponse> getMyResumes(String userId) {
        findActiveUser(userId); // 유저 존재 확인

        List<Resume> resumes = resumeRepository
                .findByUserIdAndStatusOrderByIsDefaultDescUpdatedAtDesc(
                        userId, Resume.ResumeStatus.ACTIVE);

        return resumes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── 3. 이력서 상세 조회 ───────────────────────────────────────────────────

    @Transactional
    public ResumeResponse getResumeDetail(String userId, String resumeId) {
        Resume resume = resumeRepository
                .findByIdAndUserIdAndStatus(resumeId, userId, Resume.ResumeStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("이력서를 찾을 수 없습니다."));

        // 조회수 증가
        resume.setViewCount(resume.getViewCount() + 1);
        resumeRepository.save(resume);

        log.info("이력서 상세 조회 - userId: {}, resumeId: {}", userId, resumeId);
        return toResponse(resume);
    }

    // ── 4. 이력서 수정 ────────────────────────────────────────────────────────

    @Transactional
    public ResumeResponse updateResume(String userId, String resumeId, ResumeUpdateRequest request) {
        Resume resume = resumeRepository
                .findByIdAndUserIdAndStatus(resumeId, userId, Resume.ResumeStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("이력서를 찾을 수 없습니다."));

        // 대표 이력서로 변경 시 기존 대표 해제
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefaultResume(userId);
            resume.setIsDefault(true);
        }

        // null 이 아닌 필드만 업데이트 (Partial Update)
        if (request.getTitle() != null) {
            resume.setTitle(request.getTitle());
        }
        if (request.getIntroduction() != null) {
            resume.setIntroduction(request.getIntroduction());
        }
        if (request.getBasicInfo() != null && request.getBasicInfo().getPhoto() != null) {
            resume.setPhotoUrl(request.getBasicInfo().getPhoto());
        }

        // 희망 근무 조건 업데이트
        if (request.getPreferredWork() != null) {
            ResumeUpdateRequest.PreferredWork pw = request.getPreferredWork();
            if (pw.getPositions() != null) resume.setPreferredPositions(listToString(pw.getPositions()));
            if (pw.getLocations() != null) resume.setPreferredLocations(listToString(pw.getLocations()));
            if (pw.getPreferredDays() != null) resume.setPreferredDays(listToString(pw.getPreferredDays()));
            if (pw.getPreferredTimes() != null) resume.setPreferredTimes(pw.getPreferredTimes());
            if (pw.getExpectedWage() != null) {
                if (pw.getExpectedWage().getMin() != null) resume.setExpectedWageMin(pw.getExpectedWage().getMin());
                if (pw.getExpectedWage().getMax() != null) resume.setExpectedWageMax(pw.getExpectedWage().getMax());
            }
        }

        // 스킬 업데이트
        if (request.getSkills() != null) {
            resume.setSkills(listToString(request.getSkills()));
        }

        // 경력 사항 전체 교체
        if (request.getWorkExperience() != null) {
            resume.getWorkExperiences().clear();
            List<Resume.WorkExperience> experiences = request.getWorkExperience().stream()
                    .map(exp -> Resume.WorkExperience.builder()
                            .resume(resume)
                            .businessName(exp.getBusinessName())
                            .position(exp.getPosition())
                            .startDate(exp.getStartDate())
                            .endDate(exp.getEndDate())
                            .description(exp.getDescription())
                            .build())
                    .collect(Collectors.toList());
            resume.getWorkExperiences().addAll(experiences);
        }

        // 자격증 전체 교체
        if (request.getCertifications() != null) {
            resume.getCertifications().clear();
            List<Resume.Certification> certs = request.getCertifications().stream()
                    .map(cert -> Resume.Certification.builder()
                            .resume(resume)
                            .name(cert.getName())
                            .issuer(cert.getIssuer())
                            .issueDate(cert.getIssueDate())
                            .build())
                    .collect(Collectors.toList());
            resume.getCertifications().addAll(certs);
        }

        Resume updated = resumeRepository.save(resume);
        log.info("이력서 수정 완료 - userId: {}, resumeId: {}", userId, resumeId);

        return toResponse(updated);
    }

    // ── 5. 이력서 삭제 (소프트 삭제) ─────────────────────────────────────────

    @Transactional
    public void deleteResume(String userId, String resumeId) {
        Resume resume = resumeRepository
                .findByIdAndUserIdAndStatus(resumeId, userId, Resume.ResumeStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("이력서를 찾을 수 없습니다."));

        resume.setStatus(Resume.ResumeStatus.DELETED);
        resumeRepository.save(resume);

        log.info("이력서 삭제 완료 - userId: {}, resumeId: {}", userId, resumeId);
    }

    // ── Private 헬퍼 메서드 ───────────────────────────────────────────────────

    private User findActiveUser(String userId) {
        return userRepository.findByIdAndStatus(userId, User.UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    // 기존 대표 이력서 해제
    private void clearDefaultResume(String userId) {
        resumeRepository
                .findByUserIdAndIsDefaultTrueAndStatus(userId, Resume.ResumeStatus.ACTIVE)
                .ifPresent(existing -> {
                    existing.setIsDefault(false);
                    resumeRepository.save(existing);
                });
    }

    // List → 쉼표 구분 String
    private String listToString(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return String.join(",", list);
    }

    // 쉼표 구분 String → List
    private List<String> stringToList(String str) {
        if (str == null || str.isBlank()) return List.of();
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    // Entity → Response 변환
    private ResumeResponse toResponse(Resume resume) {
        return ResumeResponse.builder()
                .resumeId(resume.getId())
                .title(resume.getTitle())
                .isDefault(resume.getIsDefault())
                .introduction(resume.getIntroduction())
                .basicInfo(ResumeResponse.BasicInfo.builder()
                        .photo(resume.getPhotoUrl())
                        .build())
                .preferredWork(ResumeResponse.PreferredWork.builder()
                        .positions(stringToList(resume.getPreferredPositions()))
                        .locations(stringToList(resume.getPreferredLocations()))
                        .preferredDays(stringToList(resume.getPreferredDays()))
                        .preferredTimes(resume.getPreferredTimes())
                        .expectedWageMin(resume.getExpectedWageMin())
                        .expectedWageMax(resume.getExpectedWageMax())
                        .build())
                .workExperience(resume.getWorkExperiences().stream()
                        .map(exp -> ResumeResponse.WorkExperienceInfo.builder()
                                .id(exp.getId())
                                .businessName(exp.getBusinessName())
                                .position(exp.getPosition())
                                .startDate(exp.getStartDate())
                                .endDate(exp.getEndDate())
                                .description(exp.getDescription())
                                .build())
                        .collect(Collectors.toList()))
                .skills(stringToList(resume.getSkills()))
                .certifications(resume.getCertifications().stream()
                        .map(cert -> ResumeResponse.CertificationInfo.builder()
                                .id(cert.getId())
                                .name(cert.getName())
                                .issuer(cert.getIssuer())
                                .issueDate(cert.getIssueDate())
                                .build())
                        .collect(Collectors.toList()))
                .stats(ResumeResponse.StatsInfo.builder()
                        .viewCount(resume.getViewCount())
                        .applicationCount(resume.getApplicationCount())
                        .build())
                .updatedAt(resume.getUpdatedAt())
                .createdAt(resume.getCreatedAt())
                .build();
    }
}