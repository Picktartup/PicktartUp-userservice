package com.picktartup.userservice.controller;

// jwt 인증이 필요한 API
import com.picktartup.userservice.common.CommonResult;
import com.picktartup.userservice.common.SingleResult;
import com.picktartup.userservice.dto.UserDto;
import com.picktartup.userservice.service.ResponseService;
import com.picktartup.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 인증 API", description = "사용자 로그아웃과 관련된 API")
@Slf4j
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

    @Operation(
            summary = "사용자 정보 조회",
            description = "userId를 기반으로 사용자 정보와 지갑 정보를 함께 반환하는 API"
    )
    @GetMapping("/{userId}")
    public SingleResult<UserDto.UserInfoResponse> getUserById(@PathVariable Long userId) {
        try {
            UserDto.UserInfoResponse response = userService.getUserById(userId);
            return responseService.getSingleResult(response);
        } catch (Exception e) {
            throw e;
        }
    }

    // 사용자 존재 여부 확인용 간단 API
    @GetMapping("/{userId}/validation")
    public SingleResult<UserDto.UserValidationResponse> validateUser(@PathVariable Long userId) {
        UserDto.UserValidationResponse validation = userService.validateUser(userId);
        return responseService.getSingleResult(validation);
    }

}
