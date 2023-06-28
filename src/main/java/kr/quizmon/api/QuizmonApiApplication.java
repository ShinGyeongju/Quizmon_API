package kr.quizmon.api;

import kr.quizmon.api.global.config.CustomConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication()
@EnableConfigurationProperties(CustomConfig.class)
public class QuizmonApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizmonApiApplication.class, args);
    }

}
