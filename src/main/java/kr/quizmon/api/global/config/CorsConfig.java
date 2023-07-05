package kr.quizmon.api.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {
    private final CustomConfig customConfig;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return customConfig.isAllow_cors()
                ? new WebMvcConfigurer() {
                    @Override
                    public void addCorsMappings(CorsRegistry registry) {
                        registry.addMapping("/**").allowedOrigins("*");
                    }
                }
                : new WebMvcConfigurer(){};
    }
}
