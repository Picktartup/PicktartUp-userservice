package com.picktartup.userservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionList {

    // 공통 예외
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),
    NOT_FOUND(404, "요청한 자원을 찾을 수 없습니다."),
    CLIENT_ERROR(400, "클라이언트 오류가 발생했습니다."),

    // 지갑 관련 예외
    WALLET_NOT_FOUND(404, "지갑을 찾을 수 없습니다."),
    WALLET_SERVICE_ERROR(500, "지갑 서비스 오류가 발생했습니다."),
    WALLET_SERVICE_CLIENT_ERROR(400, "지갑 서비스 요청이 잘못되었습니다."),

    // 사용자 관련 예외
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(409, "이미 사용 중인 이메일입니다."),
    USERNAME_ALREADY_EXISTS(409, "이미 사용 중인 아이디입니다."),
    INVALID_PASSWORD(400, "잘못된 비밀번호입니다."),

    // 인증 관련 예외
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),
    TOKEN_INVALID(401, "유효하지 않은 토큰입니다."),
    AUTHENTICATION_FAILED(401, "인증에 실패했습니다."),

    // 데이터베이스 관련 예외
    DATABASE_ERROR(500, "데이터베이스 오류가 발생했습니다."),
    DUPLICATE_ENTRY(409, "중복된 데이터가 존재합니다."),
    DATA_NOT_FOUND(404, "데이터를 찾을 수 없습니다.");

    private final int code;
    private final String message;
}
