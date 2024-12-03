package com.picktartup.userservice.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picktartup.userservice.dto.UserDto;
import com.picktartup.userservice.exception.BusinessLogicException;
import com.picktartup.userservice.service.RedisServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserLoggingAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1. 로그인 모니터링
    @Around("execution(* com.picktartup.userservice.service.UserServiceImpl.login(..))")
    public Object monitorLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime loginTime = LocalDateTime.now();
        Object[] args = joinPoint.getArgs();
        UserDto.SignInRequest request = (UserDto.SignInRequest) args[0];

        Map<String, Object> logData = new HashMap<>();
        logData.put("event_type", "login_attempt");
        logData.put("email", request.getEmail());
        logData.put("timestamp", loginTime.toString());
        logData.put("hour", loginTime.getHour());
        logData.put("day_of_week", loginTime.getDayOfWeek().toString());

        try {
            Object result = joinPoint.proceed();
            logData.put("status", "success");
            log.info(objectMapper.writeValueAsString(logData));
            return result;
        } catch (Exception e) {
            logData.put("status", "failed");
            logData.put("error_message", e.getMessage());
            log.error(objectMapper.writeValueAsString(logData));
            throw e;
        }
    }

    // 2. 회원가입 모니터링
    @Around("execution(* com.picktartup.userservice.service.UserServiceImpl.register(..))")
    public Object monitorRegistration(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime startTime = LocalDateTime.now();
        Object[] args = joinPoint.getArgs();
        UserDto.SignUpRequest request = (UserDto.SignUpRequest) args[0];

        Map<String, Object> logData = new HashMap<>();
        logData.put("event_type", "registration");
        logData.put("email", request.getEmail());
        logData.put("timestamp", startTime.toString());
        logData.put("hour", startTime.getHour());
        logData.put("day_of_week", startTime.getDayOfWeek().toString());

        try {
            Object result = joinPoint.proceed();
            long processingTimeMs = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());

            logData.put("status", "success");
            logData.put("processing_time_ms", processingTimeMs);
            log.info(objectMapper.writeValueAsString(logData));
            return result;
        } catch (Exception e) {
            logData.put("status", "failed");
            logData.put("error_message", e.getMessage());
            log.error(objectMapper.writeValueAsString(logData));
            throw e;
        }
    }

    // 3. 토큰 재발급 모니터링
    @Around("execution(* com.picktartup.userservice.service.UserServiceImpl.reissueAccessToken(..))")
    public Object monitorTokenReissue(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime requestTime = LocalDateTime.now();
        Map<String, Object> logData = new HashMap<>();
        logData.put("event_type", "token_reissue");
        logData.put("timestamp", requestTime.toString());
        logData.put("hour", requestTime.getHour());

        try {
            Object result = joinPoint.proceed();
            logData.put("status", "success");
            log.info(objectMapper.writeValueAsString(logData));
            return result;
        } catch (BusinessLogicException e) {
            logData.put("status", "failed");
            logData.put("error_message", e.getMessage());
            logData.put("error_type", "business");
            log.error(objectMapper.writeValueAsString(logData));
            throw e;
        } catch (Exception e) {
            logData.put("status", "failed");
            logData.put("error_message", e.getMessage());
            logData.put("error_type", "system");
            log.error(objectMapper.writeValueAsString(logData));
            throw e;
        }
    }

    // 4. 사용자 조회 모니터링
    @Around("execution(* com.picktartup.userservice.service.UserServiceImpl.getUserById(..))")
    public Object monitorUserRetrieval(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        LocalDateTime requestTime = LocalDateTime.now();

        Map<String, Object> logData = new HashMap<>();
        logData.put("event_type", "user_retrieval");
        logData.put("user_id", userId);
        logData.put("timestamp", requestTime.toString());
        logData.put("hour", requestTime.getHour());
        logData.put("day_of_week", requestTime.getDayOfWeek().toString());

        try {
            Object result = joinPoint.proceed();
            logData.put("status", "success");
            log.info(objectMapper.writeValueAsString(logData));
            return result;
        } catch (Exception e) {
            logData.put("status", "failed");
            logData.put("error_message", e.getMessage());
            log.error(objectMapper.writeValueAsString(logData));
            throw e;
        }
    }
}