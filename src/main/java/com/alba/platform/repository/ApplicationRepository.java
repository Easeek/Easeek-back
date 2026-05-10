package com.alba.platform.repository;

import com.alba.platform.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {

    // 사장: 내 채용공고에 받은 지원 목록
    Page<Application> findByCeoIdAndStatus(String ceoId, Application.ApplicationStatus status, Pageable pageable);
    Page<Application> findByCeoId(String ceoId, Pageable pageable);

    // 알바: 내가 지원한 목록
    Page<Application> findByApplicantIdAndStatus(String applicantId, Application.ApplicationStatus status, Pageable pageable);
    Page<Application> findByApplicantId(String applicantId, Pageable pageable);

    // 통계
    int countByCeoId(String ceoId);
    int countByCeoIdAndStatus(String ceoId, Application.ApplicationStatus status);
    int countByApplicantId(String applicantId);
    int countByApplicantIdAndStatus(String applicantId, Application.ApplicationStatus status);
}