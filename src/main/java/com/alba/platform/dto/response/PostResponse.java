package com.alba.platform.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 응답")
public class PostResponse {

    private String postId;
    private String title;
    private String category;
    private String contentPreview;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private String status;
    private LocalDateTime createdAt;
}