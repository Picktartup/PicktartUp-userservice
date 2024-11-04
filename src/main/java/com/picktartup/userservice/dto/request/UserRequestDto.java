package com.picktartup.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserRequestDto {

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
    @Schema(description = "암호화된 비밀번호", example = "Password123!")
    private String password;
}
