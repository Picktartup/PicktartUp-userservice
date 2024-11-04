package com.picktartup.userservice.service;

import com.picktartup.userservice.common.CommonResult;
import com.picktartup.userservice.dto.request.UserLoginRequest;
import com.picktartup.userservice.dto.request.UserRequestDto;
import com.picktartup.userservice.dto.response.JWTAuthResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    CommonResult register(UserRequestDto userRequestDto);

    JWTAuthResponse login(UserLoginRequest loginRequest);

    void logout(HttpServletRequest request);
}
