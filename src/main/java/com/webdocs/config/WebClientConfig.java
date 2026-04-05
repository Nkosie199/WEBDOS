package com.webdocs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${webdocs.mynger-api-url}")
    private String myngerApiUrl;

    @Value("${webdocs.deepdiary-api-url}")
    private String deepDiaryApiUrl;

    @Bean("myngerClient")
    public WebClient myngerClient() {
        return WebClient.builder()
                .baseUrl(myngerApiUrl)
                .build();
    }

    @Bean("deepDiaryClient")
    public WebClient deepDiaryClient() {
        return WebClient.builder()
                .baseUrl(deepDiaryApiUrl)
                .build();
    }
}
