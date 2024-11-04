package com.picktartup.userservice.controller;

//jwt 인증이 필요 없는 api

import com.picktartup.userservice.common.CommonResult;
import com.picktartup.userservice.dto.request.UserLoginRequest;
import com.picktartup.userservice.dto.request.UserRequestDto;
import com.picktartup.userservice.dto.response.JWTAuthResponse;
import com.picktartup.userservice.service.ResponseService;
import com.picktartup.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 인증 API", description = "사용자 로그인, 회원가입과 관련된 API")
@RestController
@RequestMapping("/api/v1/users/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
    private final ResponseService responseService;

    @Operation(summary = "헬스체크")
    @GetMapping("/health_check")
    public String healthCheck() {
        // 헬스 체크 정보를 문자열로 생성
        StringBuilder healthStatus = new StringBuilder();
        healthStatus.append("Status: UP\n");
        healthStatus.append("Message: Service is running smoothly");

        return healthStatus.toString(); // 텍스트 형식으로 반환
    }

    @Operation(summary = "사용자 회원가입")
    @PostMapping("/register")
    public CommonResult userRegister(@RequestBody UserRequestDto userRequestDto){
        return userService.register(userRequestDto);
    }

    @Operation(summary = "사용자 로그인")
    @PostMapping("/login")
    public CommonResult userLogin(@RequestBody UserLoginRequest loginRequest){
        JWTAuthResponse token = userService.login(loginRequest);
        return responseService.getSingleResult(token);
    }
}
