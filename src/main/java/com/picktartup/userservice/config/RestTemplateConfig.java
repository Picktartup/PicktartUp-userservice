package com.picktartup.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

//퍼플릭 -> 온프레 외부 통신 방식
//단순 조회 기능이기 때문에 Resttemplate
@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate walletServiceRestTemplate() {
        try {
            // SSLContext 설정
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(null, new TrustAllStrategy())
                    .build();

            // SSL Connection Factory 생성
            SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    NoopHostnameVerifier.INSTANCE
            );

            // CloseableHttpClient 생성
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                            .setSSLSocketFactory(sslFactory)
                            .build())
                    .build();

            // HttpComponentsClientHttpRequestFactory 생성
            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);

            return new RestTemplate(requestFactory);
        } catch (Exception e) {
            log.error("Error creating RestTemplate with SSL configuration: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
