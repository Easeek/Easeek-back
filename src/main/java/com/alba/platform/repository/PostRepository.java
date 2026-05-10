package com.alba.platform.repository;

import com.alba.platform.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {

    // 내가 작성한 글 목록
    Page<Post> findByAuthorIdAndStatus(String authorId, Post.PostStatus status, Pageable pageable);

    // 카테고리별 내 글 목록
    Page<Post> findByAuthorIdAndCategoryAndStatus(String authorId, String category, Post.PostStatus status, Pageable pageable);

    // 통계
    int countByAuthorIdAndStatus(String authorId, Post.PostStatus status);
}