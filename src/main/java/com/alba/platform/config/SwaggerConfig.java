package com.alba.platform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // Security Scheme 설정 (JWT)
        SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList("Bearer Authentication");

        return new OpenAPI()
            .components(new Components().addSecuritySchemes("Bearer Authentication", securityScheme))
            .security(List.of(securityRequirement))
            .info(apiInfo())
            .servers(List.of(
                new Server().url("http://localhost:8080").description("로컬 개발 서버"),
                new Server().url("https://api.alba-platform.com").description("프로덕션 서버")
            ));
    }

    private Info apiInfo() {
        return new Info()
            .title("알바 플랫폼 API")
            .description("알바 채용 플랫폼 백엔드 API 문서")
            .version("1.0.0")
            .contact(new Contact()
                .name("조영호")
                .email("youngh@example.com")
                .url("https://github.com/youngh"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT"));
    }
}