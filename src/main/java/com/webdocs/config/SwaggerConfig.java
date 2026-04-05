package com.webdocs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI webDocsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WebDocs API")
                        .description("WebDocs — Static Site Generator + Unified API Gateway for Mynger and DeepDiary")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("WebDocs")
                                .email("support@webdocs.co.za")
                                .url("https://webdocs.co.za"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
