package com.alba.platform.controller;

import com.alba.platform.dto.response.ApiResponse;
import com.alba.platform.service.ApplicationProposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "지원/제안/게시글", description = "지원, 제안, 게시글 관련 API")
public class ApplicationProposalController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationProposalController.class);

    private final ApplicationProposalService service;

    public ApplicationProposalController(ApplicationProposalService service) {
        this.service = service;
    }

    // ── 1. 사장: 받은 지원 목록 ───────────────────────────────────────────────

    @Operation(summary = "받은 지원 목록 (사장)", description = "내 채용공고에 지원한 알바생 목록. 사장 권한 필요")
    @GetMapping("/applications/received")
    public ApiResponse<Map<String, Object>> getReceivedApplications(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        try {
            Map<String, Object> result = service.getReceivedApplications(userId, status, page, limit);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("받은 지원 목록 조회 실패 - userId: {}", userId, e);
            return ApiResponse.error(e.getMessage(), "APP_001");
        }
    }

    // ── 2. 알바: 내가 지원한 목록 ────────────────────────────────────────────

    @Operation(summary = "지원한 공고 목록 (알바)", description = "내가 지원한 채용공고 목록. 알바 권한 필요")
    @GetMapping("/applications/sent")
    public ApiResponse<Map<String, Object>> getSentApplications(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        try {
            Map<String, Object> result = service.getSentApplications(userId, status, page, limit);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("지원한 목록 조회 실패 - userId: {}", userId, e);
            return ApiResponse.error(e.getMessage(), "APP_002");
        }
    }

    // ── 3. 알바: 지원 내역 히스토리 ──────────────────────────────────────────

    @Operation(summary = "지원 내역 조회 (알바)", description = "지원 전체 히스토리 조회")
    @GetMapping("/applications/history")
    public ApiResponse<Map<String, Object>> getApplicationHistory(
            @AuthenticationPrincipal String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        try {
            Map<String, Object> result = service.getApplicationHistory(userId, page, limit);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("지원 내역 히스토리 조회 실패 - userId: {}", userId, e);
            return ApiResponse.error(e.getMessage(), "APP_003");
        }
    }

    // ── 4. 사장: 보낸 제안 목록 ───────────────────────────────────────────────

    @Operation(summary = "보낸 제안 목록 (사장)", description = "내가 알바생에게 보낸 제안 목록. 사장 권한 필요")
    @GetMapping("/proposals/sent")
    public ApiResponse<Map<String, Object>> getSentProposals(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        try {
            Map<String, Object> result = service.getSentProposals(userId, status, page, limit);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("보낸 제안 목록 조회 실패 - userId: {}", userId, e);
            return ApiResponse.error(e.getMessage(), "PROP_001");
        }
    }

    // ── 5. 알바: 받은 제안 목록 ───────────────────────────────────────────────

    @Operation(summary = "받은 제안 목록 (알바)", description = "사장님이 보낸 제안 목록. 알바 권한 필요")
    @GetMapping("/proposals/received")
    public ApiResponse<Map<String, Object>> getReceivedProposals(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        try {
            Map<String, Object> result = service.getReceivedProposals(userId, status, page, limit);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("받은 제안 목록 조회 실패 - userId: {}", userId, e);
            return ApiResponse.error(e.getMessage(), "PROP_002");
        }
    }

    // ── 6. 내가 작성한 글 목록 ────────────────────────────────────────────────

    @Operation(summary = "내가 작성한 글 목록", description = "내가 작성한 게시글 목록 조회. 사장/알바 공통")
    @GetMapping("/posts/my")
    public ApiResponse<Map<String, Object>> getMyPosts(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        try {
            Map<String, Object> result = service.getMyPosts(userId, category, page, limit);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("내 게시글 목록 조회 실패 - userId: {}", userId, e);
            return ApiResponse.error(e.getMessage(), "POST_001");
        }
    }
}