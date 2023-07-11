package kr.quizmon.api.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "custom.properties")
public class CustomConfig {
    // CORS
    private boolean allow_cors;

    // JWT
    private String jwt_Cookie_name;
    private String jwt_secret_key;
    private int jwt_expiration_hour;

    // HMAC
    private String hmac_header;
    private String hmac_secret_key;
    private int hmac_expiration_minute;

}
