package com.webdocs.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class WebClientConfig {

    @Value("${webdocs.mynger-api-url}")
    private String myngerApiUrl;

    @Value("${webdocs.deepdiary-api-url}")
    private String deepDiaryApiUrl;

    private HttpClient httpClient() {
        return HttpClient.create()
                // Follow redirects (handles 301/302 transparently)
                .followRedirect(true)
                // 16 MB response buffer (handles large file lists)
                .responseTimeout(java.time.Duration.ofSeconds(30));
    }

    private ExchangeStrategies largeBufferStrategies() {
        return ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
    }

    @Bean("myngerClient")
    public WebClient myngerClient() {
        return WebClient.builder()
                .baseUrl(myngerApiUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .exchangeStrategies(largeBufferStrategies())
                .build();
    }

    @Bean("deepDiaryClient")
    public WebClient deepDiaryClient() {
        return WebClient.builder()
                .baseUrl(deepDiaryApiUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .exchangeStrategies(largeBufferStrategies())
                .build();
    }
}
