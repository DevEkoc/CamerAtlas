package com.devekoc.camerAtlas.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "CamerAtlas API",
                version = "1.0",
                description = "API documentaire du découpage administratif du Cameroun (régions, départements, arrondissements, quartiers, autorités, affectations et frontières)."
        )
)
public class OpenApiConfig {
}
