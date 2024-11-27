package com.picktartup.userservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Wallet 관련 DTO 클래스.
 */
public class WalletDto {

    /**
     * 지갑 상세 정보 응답 DTO.
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Details {
        private Long userId;                 // 사용자 ID
        private String address;              // 지갑 주소
        private BigDecimal balance;          // 잔액
        private String status;               // 지갑 상태 (enum -> String)
        private LocalDateTime createdAt;     // 생성일
        private LocalDateTime updatedAt;     // 수정일
    }

    /**
     * 공통 응답 래퍼 DTO.
     * @param <T> 래핑할 데이터 타입
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;             // 요청 성공 여부
        private int code;                    // 상태 코드
        private String message;              // 메시지
        private T data;                      // 실제 데이터
    }
}
