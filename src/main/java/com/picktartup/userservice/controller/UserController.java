package com.picktartup.userservice.controller;

//jwt 인증이 필요 없는 api

import com.picktartup.userservice.common.CommonResult;
import com.picktartup.userservice.common.SingleResult;
import com.picktartup.userservice.dto.UserDto;
import com.picktartup.userservice.service.ResponseService;
import com.picktartup.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Operation(summary = "사용자 회원가입과 관련된 API")
    @PostMapping("/register")
    public SingleResult<Long> userRegister(@RequestBody UserDto.SignUpRequest signUpRequest){
        return userService.register(signUpRequest);
    }

    @Operation(summary = "사용자 로그인과 관련된 API")
    @PostMapping("/login")
    public CommonResult userLogin(@RequestBody UserDto.SignInRequest loginRequest){
        UserDto.AuthResponse token = userService.login(loginRequest);
        return responseService.getSingleResult(token);
    }

    @Operation(summary = "토큰 재발급", description = "리프레쉬 토큰으로 액세스 토큰을 재발급 하는 API")
    @PatchMapping("/reissue")
    public CommonResult reissue(HttpServletRequest request,
                                HttpServletResponse response) {

        UserDto.AuthResponse newAccessToken = userService.reissueAccessToken(request);
        return responseService.getSingleResult(newAccessToken);
    }

}
