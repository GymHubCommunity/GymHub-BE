package com.example.temp.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SwaggerConfigTest {

    SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
    }

    @Test
    void testCustomOpenAPI() {
        String securityRequirementName = "accessToken을 붙여넣으세요, Bearer는 필요 없습니다.";
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("GymHub");
        assertThat(openAPI.getInfo().getVersion()).isNotNull();
        Map<String, SecurityScheme> securitySchemes = openAPI.getComponents().getSecuritySchemes();
        SecurityScheme securityScheme = securitySchemes.get(securityRequirementName);

        assertThat(securityScheme.getName()).isEqualTo(securityRequirementName);
        assertThat(securityScheme.getType()).isEqualTo(Type.HTTP);
        assertThat(securityScheme.getScheme()).isEqualTo("bearer");
        assertThat(securityScheme.getBearerFormat()).isEqualTo("JWT");
    }

}