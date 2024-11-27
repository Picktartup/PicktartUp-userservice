package com.picktartup.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

public class UserDto {

    // === 회원 가입 관련 DTO ===
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class SignUpRequest {
        @NotBlank(message = "아이디를 입력해주세요.")
        @Size(min = 5, message = "아이디는 5자 이상이어야 합니다.")
        @Schema(description = "아이디", example = "user123")
        private String username;

        @Email(message = "유효한 이메일을 입력해주세요.")
        @NotBlank(message = "이메일을 입력해주세요.")
        @Size(min = 5, message = "이메일은 5자 이상이어야 합니다.")
        @Schema(description = "이메일", example = "user@example.com")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        @Schema(description = "비밀번호", example = "Password123!")
        private String password;
    }

    // === 로그인 관련 DTO ===
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignInRequest {
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "유효한 이메일을 입력해주세요.")
        @Schema(description = "이메일", example = "user@example.com")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        @Schema(description = "비밀번호", example = "Password123!")
        private String password;
    }

    // === 인증 응답 DTO ===
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String tokenType;
        private String accessToken;
        private String refreshToken;
        private Long accessTokenExpireDate;
    }

    // === 사용자 정보 응답 DTO ===
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoResponse {
        private Long id;
        private String username;
        private String email;
        private String role;

        // Wallet 관련 필드
        private String walletAddress;
        private BigDecimal walletBalance;
        private String walletStatus;
    }
}