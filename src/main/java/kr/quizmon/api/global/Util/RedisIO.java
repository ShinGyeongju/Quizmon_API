package kr.quizmon.api.global.Util;

import jakarta.annotation.PostConstruct;
import kr.quizmon.api.domain.quiz.QuizEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisIO {
    private final RedisTemplate<String, String> logoutRedisTemplate;
    private final RedisTemplate<String, QuizEntity> quizRedisTemplate;

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

    public void setQuiz(String key, QuizEntity value, long milliSeconds) {
        quizRedisTemplate.opsForValue().set(key, value, milliSeconds, TimeUnit.MILLISECONDS);
    }

    public QuizEntity getQuiz(String key) {
        QuizEntity quiz = quizRedisTemplate.opsForValue().get(key);
        quizRedisTemplate.delete(key);
        return quiz;
    }


}
