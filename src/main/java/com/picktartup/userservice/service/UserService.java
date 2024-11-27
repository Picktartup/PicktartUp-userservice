package com.picktartup.userservice.service;

import com.picktartup.userservice.common.CommonResult;
import com.picktartup.userservice.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    CommonResult register(UserDto.SignUpRequest signUpRequest);

    UserDto.AuthResponse login(UserDto.SignInRequest loginRequest);

    void logout(HttpServletRequest request);

    UserDto.UserInfoResponse getUserById(Long userId);

    UserDto.UserValidationResponse validateUser(Long userId);

    UserDto.AuthResponse reissueAccessToken(HttpServletRequest request);
}
