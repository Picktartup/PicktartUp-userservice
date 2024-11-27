package com.picktartup.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

//퍼플릭 -> 온프레 외부 통신 방식
//단순 조회 기능이기 때문에 Resttemplate
@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate walletServiceRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 타임아웃 설정
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        restTemplate.setRequestFactory(factory);

        // 에러 핸들러 추가
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                try {
                    return super.hasError(response);
                } catch (Exception e) {
                    log.error("Error checking response: {}", e.getMessage());
                    return true;
                }
            }
        });

        // 로깅 인터셉터
        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            log.info("=== Wallet Service Request ===");
            log.info("URL: {} {}", request.getMethod(), request.getURI());
            log.info("Headers: {}", request.getHeaders());

            ClientHttpResponse response = execution.execute(request, body);

            log.info("=== Wallet Service Response ===");
            log.info("Status: {}", response.getStatusCode());

            return response;
        }));

        return restTemplate;
    }
}
