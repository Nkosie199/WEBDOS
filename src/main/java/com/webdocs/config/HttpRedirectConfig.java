package com.webdocs.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Adds a plain HTTP connector on port 5554 that redirects all traffic
 * to the HTTPS port 5555.
 */
@Configuration
public class HttpRedirectConfig {

    @Value("${server.port:5555}")
    private int httpsPort;

    private static final int HTTP_PORT = 5554;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> httpRedirectCustomizer() {
        return factory -> {
            Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
            connector.setScheme("http");
            connector.setPort(HTTP_PORT);
            connector.setSecure(false);
            connector.setRedirectPort(httpsPort);
            factory.addAdditionalTomcatConnectors(connector);
        };
    }
}
