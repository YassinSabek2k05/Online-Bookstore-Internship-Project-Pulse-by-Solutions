package com.pulsebysolutions.onlinebookstoreinternshipproject.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String COOKIE_SCHEME = "user_token";

    @Bean
    public OpenAPI bookstoreOpenAPI() {

        // Auth travels in an httpOnly cookie, so declare it as an apiKey in
        // COOKIE — logging in through /api/auth/login makes the browser send
        // it on every later "Try it out" call automatically.
        SecurityScheme cookieAuth = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name(COOKIE_SCHEME);

        return new OpenAPI()
                .info(new Info()
                        .title("Online Bookstore API")
                        .version("v1")
                        .description("""
                                Books, users and administrators for the Online Bookstore.

                                Sign in with POST /api/auth/login first — it sets the
                                user_token cookie that every protected endpoint requires."""))
                .components(new Components().addSecuritySchemes(COOKIE_SCHEME, cookieAuth))
                .addSecurityItem(new SecurityRequirement().addList(COOKIE_SCHEME));
    }
}
