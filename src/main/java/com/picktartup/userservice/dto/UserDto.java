package com.picktartup.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResponseDto {
        private Long id;
        private String username;
        private String email;
        private String role;
    }
}
