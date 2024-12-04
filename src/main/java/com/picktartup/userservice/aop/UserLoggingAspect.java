package com.picktartup.userservice.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserLoggingAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 모든 Controller의 API 호출 정보 로깅
    @Around("execution(* com.picktartup.userservice.controller.*.*(..))")
    public Object logAllAPIs(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        LocalDateTime requestTime = LocalDateTime.now();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Map<String, Object> logData = new HashMap<>();
        logData.put("timestamp", requestTime.format(DateTimeFormatter.ISO_DATE_TIME));
        logData.put("request_id", UUID.randomUUID().toString());
        logData.put("http_method", request.getMethod());
        logData.put("uri", request.getRequestURI());
        logData.put("api_path", request.getRequestURI().replaceAll("/\\d+", "/{id}"));
        logData.put("client_ip", request.getRemoteAddr());
        logData.put("api_name", ((MethodSignature) joinPoint.getSignature()).getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName());

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            logData.put("response_time_ms", executionTime);
            logData.put("status", "success");
            log.info(objectMapper.writeValueAsString(logData));
            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            logData.put("response_time_ms", executionTime);
            logData.put("status", "failed");
            logData.put("error_message", e.getMessage());
            log.error(objectMapper.writeValueAsString(logData));
            throw e;
        }
    }

    // 로그인 모니터링 (특정 API)
    @Around("execution(* com.picktartup.userservice.service.UserServiceImpl.login(..))")
    public Object monitorLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Map<String, Object> logData = new HashMap<>();
        logData.put("event_type", "login_attempt");

        return logAndProceed(joinPoint, logData);
    }

    // 회원가입 모니터링
    @Around("execution(* com.picktartup.userservice.service.UserServiceImpl.register(..))")
    public Object monitorRegistration(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Map<String, Object> logData = new HashMap<>();
        logData.put("event_type", "registration");

        return logAndProceed(joinPoint, logData);
    }

    // 토큰 재발급 모니터링
    @Around("execution(* com.picktartup.userservice.service.UserServiceImpl.reissueAccessToken(..))")
    public Object monitorTokenReissue(ProceedingJoinPoint joinPoint) throws Throwable {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event_type", "token_reissue");

        return logAndProceed(joinPoint, logData);
    }

    // 공통 로깅 로직
    private Object logAndProceed(ProceedingJoinPoint joinPoint, Map<String, Object> additionalLogData) throws Throwable {
        long startTime = System.currentTimeMillis();
        LocalDateTime requestTime = LocalDateTime.now();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Map<String, Object> logData = new HashMap<>(additionalLogData);
        logData.put("timestamp", requestTime.format(DateTimeFormatter.ISO_DATE_TIME));
        logData.put("request_id", UUID.randomUUID().toString());
        logData.put("http_method", request.getMethod());
        logData.put("uri", request.getRequestURI());
        logData.put("api_path", request.getRequestURI().replaceAll("/\\d+", "/{id}"));
        logData.put("client_ip", request.getRemoteAddr());
        logData.put("api_name", ((MethodSignature) joinPoint.getSignature()).getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName());

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            logData.put("response_time_ms", executionTime);
            logData.put("status", "success");
            log.info(objectMapper.writeValueAsString(logData));
            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            logData.put("response_time_ms", executionTime);
            logData.put("status", "failed");
            logData.put("error_message", e.getMessage());
            log.error(objectMapper.writeValueAsString(logData));
            throw e;
        }
    }
}
