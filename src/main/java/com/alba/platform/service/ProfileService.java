package com.alba.platform.service;

import com.alba.platform.dto.request.ProfileCreateRequest;
import com.alba.platform.dto.response.ProfileResponse;
import com.alba.platform.entity.User;
import com.alba.platform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 프로필 등록/수정
     */
    @Transactional
    public ProfileResponse createOrUpdateProfile(String userId, ProfileCreateRequest request) {
        User user = userRepository.findByIdAndStatus(userId, User.UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        validatePhone(userId, request.getPhone());
        validateEmail(userId, request.getEmail());

        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setProfileImage(request.getProfileImage());
        user.setNickname(request.getNickname());
        user.setBirthDate(request.getBirthDate());
        user.setGender(request.getGender());

        User savedUser = userRepository.save(user);

        log.info("프로필 등록/수정 완료 - userId: {}", userId);

        return toProfileResponse(savedUser);
    }

    /**
     * 프로필 조회
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String userId) {
        User user = userRepository.findByIdAndStatus(userId, User.UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return toProfileResponse(user);
    }

    private void validatePhone(String currentUserId, String phone) {
        userRepository.findByPhone(phone)
                .ifPresent(foundUser -> {
                    if (!foundUser.getId().equals(currentUserId)) {
                        throw new RuntimeException("이미 등록된 전화번호입니다.");
                    }
                });
    }

    private void validateEmail(String currentUserId, String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        userRepository.findByEmail(email)
                .ifPresent(foundUser -> {
                    if (!foundUser.getId().equals(currentUserId)) {
                        throw new RuntimeException("이미 등록된 이메일입니다.");
                    }
                });
    }

    private ProfileResponse toProfileResponse(User user) {
        return ProfileResponse.builder()
                .userId(user.getId())
                .userType(user.getUserType().name().toLowerCase())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())

                // 현재 프로젝트 기준 실데이터 연동 불가 영역
                .trustScore(0)
                .level("일반")
                .business(ProfileResponse.BusinessInfo.builder()
                        .businessId(null)
                        .businessName(null)
                        .verified(false)
                        .address(null)
                        .build())
                .stats(ProfileResponse.StatsInfo.builder()
                        .totalPosts(0)
                        .totalJobs(0)
                        .activeJobs(0)
                        .totalHires(0)
                        .reviewCount(0)
                        .averageRating(0.0)
                        .build())
                .badges(List.of())

                .joinedAt(user.getCreatedAt() != null ? user.getCreatedAt().toLocalDate() : null)
                .build();
    }
}