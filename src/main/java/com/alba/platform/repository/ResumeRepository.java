package com.alba.platform.repository;

import com.alba.platform.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, String> {

    // 특정 유저의 활성 이력서 전체 조회
    List<Resume> findByUserIdAndStatusOrderByIsDefaultDescUpdatedAtDesc(
            String userId, Resume.ResumeStatus status);

    // 특정 유저의 대표 이력서 조회
    Optional<Resume> findByUserIdAndIsDefaultTrueAndStatus(
            String userId, Resume.ResumeStatus status);

    // 특정 유저의 이력서 단건 조회 (본인 확인용)
    Optional<Resume> findByIdAndUserIdAndStatus(
            String id, String userId, Resume.ResumeStatus status);

    // 특정 유저의 활성 이력서 개수
    int countByUserIdAndStatus(String userId, Resume.ResumeStatus status);
}