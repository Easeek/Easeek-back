package com.alba.platform.controller;

import com.alba.platform.dto.request.ResumeCreateRequest;
import com.alba.platform.dto.request.ResumeUpdateRequest;
import com.alba.platform.dto.response.ApiResponse;
import com.alba.platform.dto.response.ResumeResponse;
import com.alba.platform.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resume")
@Tag(name = "이력서", description = "이력서 관련 API (알바 권한)")
public class ResumeController {

    private static final Logger log = LoggerFactory.getLogger(ResumeController.class);

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // ── 1. 이력서 등록 ────────────────────────────────────────────────────────

    @Operation(summary = "이력서 등록", description = "새 이력서를 작성합니다. 알바 권한 필요")
    @PostMapping
    public ApiResponse<Map<String, Object>> createResume(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody ResumeCreateRequest request
    ) {
        try {
            ResumeResponse response = resumeService.createResume(userId, request);

            return ApiResponse.success(
                    Map.of(
                            "success", true,
                            "resumeId", response.getResumeId(),
                            "url", "/resume/" + response.getResumeId(),
                            "tips", List.of("사진 추가하면 지원 성공률 40% 증가")
                    ),
                    "이력서 등록 완료"
            );
        } catch (Exception e) {
            log.error("이력서 등록 실패 - userId: {}", userId, e);
            return ApiResponse.error(e.getMessage(), "RESUME_001");
        }
    }

    // ── 2. 이력서 목록 조회 ───────────────────────────────────────────────────

    @Operation(summary = "이력서 목록 조회", description = "내 이력서 목록을 조회합니다. 대표 이력서가 맨 앞에 옵니다.")
    @GetMapping
    public ApiResponse<Map<String, Object>> getMyResumes(
            @AuthenticationPrincipal String userId
    ) {
        try {
            List<ResumeResponse> resumes = resumeService.getMyResumes(userId);

            // 대표 이력서 ID 추출
            String defaultResumeId = resumes.stream()
                    .filter(r -> Boolean.TRUE.equals(r.getIsDefault()))
                    .map(ResumeResponse::getResumeId)
                    .findFirst()
                    .orElse(null);

            return ApiResponse.success(
                    Map.of(
                            "totalCount", resumes.size(),
                            "resumes", resumes,
                            "defaultResumeId", defaultResumeId != null ? defaultResumeId : ""
                    )
            );
        } catch (Exception e) {
            log.error("이력서 목록 조회 실패 - userId: {}", userId, e);
            return ApiResponse.error(e.getMessage(), "RESUME_002");
        }
    }

    // ── 3. 이력서 상세 조회 ───────────────────────────────────────────────────

    @Operation(summary = "이력서 상세 조회", description = "특정 이력서의 상세 정보를 조회합니다. 조회 시 viewCount +1")
    @GetMapping("/{resumeId}")
    public ApiResponse<ResumeResponse> getResumeDetail(
            @AuthenticationPrincipal String userId,
            @PathVariable String resumeId
    ) {
        try {
            ResumeResponse response = resumeService.getResumeDetail(userId, resumeId);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("이력서 상세 조회 실패 - userId: {}, resumeId: {}", userId, resumeId, e);
            return ApiResponse.error(e.getMessage(), "RESUME_003");
        }
    }

    // ── 4. 이력서 수정 ────────────────────────────────────────────────────────

    @Operation(summary = "이력서 수정", description = "기존 이력서를 수정합니다. 보낸 필드만 업데이트됩니다.")
    @PutMapping("/{resumeId}")
    public ApiResponse<Map<String, Object>> updateResume(
            @AuthenticationPrincipal String userId,
            @PathVariable String resumeId,
            @RequestBody ResumeUpdateRequest request
    ) {
        try {
            ResumeResponse response = resumeService.updateResume(userId, resumeId, request);

            return ApiResponse.success(
                    Map.of(
                            "success", true,
                            "resumeId", response.getResumeId(),
                            "updatedAt", response.getUpdatedAt().toString()
                    ),
                    "이력서 수정 완료"
            );
        } catch (Exception e) {
            log.error("이력서 수정 실패 - userId: {}, resumeId: {}", userId, resumeId, e);
            return ApiResponse.error(e.getMessage(), "RESUME_004");
        }
    }

    // ── 5. 이력서 삭제 ────────────────────────────────────────────────────────

    @Operation(summary = "이력서 삭제", description = "이력서를 삭제합니다. (소프트 삭제)")
    @DeleteMapping("/{resumeId}")
    public ApiResponse<Map<String, Object>> deleteResume(
            @AuthenticationPrincipal String userId,
            @PathVariable String resumeId
    ) {
        try {
            resumeService.deleteResume(userId, resumeId);

            return ApiResponse.success(
                    Map.of(
                            "success", true,
                            "resumeId", resumeId
                    ),
                    "이력서 삭제 완료"
            );
        } catch (Exception e) {
            log.error("이력서 삭제 실패 - userId: {}, resumeId: {}", userId, resumeId, e);
            return ApiResponse.error(e.getMessage(), "RESUME_005");
        }
    }
}