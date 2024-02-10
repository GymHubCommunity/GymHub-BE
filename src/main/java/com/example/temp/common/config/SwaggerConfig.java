package com.example.temp.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@OpenAPIDefinition
@Configuration
@Profile("local")
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
            .title("GymHub")
            .version("v1.0.0");

        String securityRequirementName = "accessToken을 붙여넣으세요, Bearer는 필요 없습니다.";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityRequirementName);

        Components components = new Components()
            .addSecuritySchemes(securityRequirementName, new SecurityScheme()
                .name(securityRequirementName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));

        return new OpenAPI()
            .info(info)
            .addSecurityItem(securityRequirement) // 인증 정보를 Global하게 사용할 수 있게 만듦
            .components(components); // securityRequirementName이 어떤 방식으로 동작하는지 설정함
    }
}
