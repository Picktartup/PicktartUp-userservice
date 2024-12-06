package com.picktartup.userservice.webclient;

import com.picktartup.userservice.dto.WalletDto;
import com.picktartup.userservice.entity.WalletStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceClient {

    @Value("${service.wallet.url}")
    private String walletServiceUrl;

    private final RestTemplate walletServiceRestTemplate;

    @CircuitBreaker(name = "walletService", fallbackMethod = "getWalletByUserIdFallback")
    public WalletDto.Details getWalletByUserId(Long userId) {
        try {
            String url = walletServiceUrl + "/api/v1/wallets/user/" + userId;
            log.info("Calling wallet service. URL: {}", url);

            // ResponseWrapper를 사용하여 response.data에 접근
            ResponseEntity<WalletDto.ApiResponse<WalletDto.Details>> response =
                    walletServiceRestTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<WalletDto.ApiResponse<WalletDto.Details>>() {}
                    );

            log.info("Wallet service response status: {}", response.getStatusCode());

            if (response.getBody() != null && response.getBody().getData() != null) {
                WalletDto.Details walletResponse = response.getBody().getData();
                // Wallet 서비스의 Response를 UserService의 WalletResponseDto로 변환
                return convertToWalletResponseDto(walletResponse);
            }

            log.warn("No wallet data received for userId: {}", userId);
            return new WalletDto.Details();

        } catch (Exception e) {
            log.error("Failed to fetch wallet info for user {}: {}", userId, e.getMessage());
            return new WalletDto.Details();
        }
    }

    private WalletDto.Details convertToWalletResponseDto(WalletDto.Details response) {
        return WalletDto.Details.builder()
                .userId(response.getUserId())
                .address(response.getAddress())
                .balance(response.getBalance())
                .status(response.getStatus() != null ? response.getStatus().toString() : null)
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }

    private WalletDto.Details getWalletByUserIdFallback(Long userId, Exception ex) {
        log.warn("Circuit breaker fallback called for userId {}: {}", userId, ex.getMessage());
        return createEmptyWalletResponse(userId);
    }

    private WalletDto.Details createEmptyWalletResponse(Long userId) {
        return WalletDto.Details.builder()
                .userId(userId)
                .status(String.valueOf(WalletStatus.INACTIVE))
                .balance(BigDecimal.ZERO)
                .build();
    }

    // HTTP 헤더 설정 메소드 추가
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
