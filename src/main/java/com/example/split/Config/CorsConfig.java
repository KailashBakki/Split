package com.example.split.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//import static org.maveric.currencyexchange.constants.SecurityConstants.*;
@EnableWebMvc
@Configuration
public class CorsConfig {

    public static final String[] CORS_ALLOWED_METHODS = {"GET", "POST", "PUT", "DELETE", "PATCH"};
    @Bean
    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("${corsAllowedOrigin}")
//                        .allowedMethods(CORS_ALLOWED_METHODS)
//                        .allowCredentials(true);
//            }
//        };
//    }
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                        .allowCredentials(true);
            }
        };}
}