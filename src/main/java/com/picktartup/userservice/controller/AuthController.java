package com.picktartup.userservice.controller;

// jwt 인증이 필요한 API
import com.picktartup.userservice.common.CommonResult;
import com.picktartup.userservice.service.ResponseService;
import com.picktartup.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 인증 API", description = "사용자 로그아웃과 관련된 API")
@RestController
@RequestMapping("/api/v1/users/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final UserService userService;
    private final ResponseService responseService;

    @Operation(summary = "로그아웃", description = "사용자의 로그아웃과 관련된 API")
    @DeleteMapping("/logout")
    public CommonResult logout(HttpServletRequest request) {
        userService.logout(request);
        return responseService.getSuccessfulResult();
    }

    @Operation(summary = "사용자 정보 조회", description = "userId를 기반으로 사용자 정보를 반환하는 API")
    @GetMapping("/{userId}")
    public CommonResult getUserById(@PathVariable Long userId) {
        return responseService.getSingleResult(userService.getUserById(userId));
    }
}
