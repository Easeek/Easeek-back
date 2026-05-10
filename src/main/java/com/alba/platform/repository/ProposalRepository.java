package com.alba.platform.repository;

import com.alba.platform.entity.Proposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, String> {

    // 사장: 내가 보낸 제안 목록
    Page<Proposal> findBySenderIdAndStatus(String senderId, Proposal.ProposalStatus status, Pageable pageable);
    Page<Proposal> findBySenderId(String senderId, Pageable pageable);

    // 알바: 내가 받은 제안 목록
    Page<Proposal> findByRecipientIdAndStatus(String recipientId, Proposal.ProposalStatus status, Pageable pageable);
    Page<Proposal> findByRecipientId(String recipientId, Pageable pageable);

    // 통계
    int countBySenderId(String senderId);
    int countBySenderIdAndStatus(String senderId, Proposal.ProposalStatus status);
    int countByRecipientId(String recipientId);
    int countByRecipientIdAndStatus(String recipientId, Proposal.ProposalStatus status);
}