package com.alba.platform.dto.request;

import com.alba.platform.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "프로필 등록 요청")
public class ProfileCreateRequest {

    @Schema(description = "이름", example = "홍길동", required = true)
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @Schema(description = "전화번호", example = "01012345678", required = true)
    @NotBlank(message = "전화번호는 필수입니다")
    private String phone;

    @Schema(description = "이메일", example = "hong@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/profile.jpg")
    private String profileImage;

    @Schema(description = "닉네임", example = "길동이")
    private String nickname;

    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "성별", example = "M")
    private User.Gender gender;
}