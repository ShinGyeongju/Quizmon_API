package kr.quizmon.api.global.Util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisIO {
    private final RedisTemplate<String, String> logoutRedisTemplate;
    //private final RedisTemplate<String, Quiz> quizRedisTemplate;

    @PostConstruct
    public void connectionTest() {
        try {
            logoutRedisTemplate.opsForValue().get("");
        } catch (RedisConnectionFailureException ex) {
            throw new RedisConnectionFailureException(ex.getMessage());
        }
    }

    public void setLogout(String key, String value, long milliSeconds) {
        logoutRedisTemplate.opsForValue().set(key, value, milliSeconds, TimeUnit.MILLISECONDS);
    }

    public boolean hasLogoutKey(String key) {
        return Boolean.TRUE.equals(logoutRedisTemplate.hasKey(key));
    }

}
