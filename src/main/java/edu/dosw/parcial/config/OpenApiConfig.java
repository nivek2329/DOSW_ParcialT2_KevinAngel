package edu.dosw.parcial.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class OpenApiConfig {

    static {
        io.swagger.v3.core.converter.ModelConverters.getInstance().addConverter(new io.swagger.v3.core.converter.ModelConverter() {
            @Override
            public Schema<?> resolve(io.swagger.v3.core.converter.AnnotatedType type,
                                     io.swagger.v3.core.converter.ModelConverterContext context,
                                     java.util.Iterator<io.swagger.v3.core.converter.ModelConverter> chain) {
                if (type != null && type.getType() != null) {
                    String typeName = type.getType().getTypeName();
                    if (typeName.equals(LocalTime.class.getName()) || typeName.equals("java.time.LocalTime")) {
                        return new io.swagger.v3.oas.models.media.StringSchema().example("10:00").format("time");
                    }
                    if (typeName.equals(LocalDate.class.getName()) || typeName.equals("java.time.LocalDate")) {
                        return new io.swagger.v3.oas.models.media.DateSchema().example("2026-10-25");
                    }
                }
                if (chain.hasNext()) {
                    return chain.next().resolve(type, context, chain);
                }
                return null;
            }
        });
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Bob's Barber API REST")
                        .version("1.0.0")
                        .description("API REST para la gestion de turnos y reservas de la barberia"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
