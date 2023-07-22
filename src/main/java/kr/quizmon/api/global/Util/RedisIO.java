package kr.quizmon.api.global.Util;

import jakarta.annotation.PostConstruct;
import kr.quizmon.api.domain.quiz.QuizDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisIO {
    private final RedisTemplate<String, String> defaultRedisTemplate;
    private final RedisTemplate<String, QuizDTO.CreateRedis> quizRedisTemplate;

    @PostConstruct
    public void connectionTest() {
        try {
            defaultRedisTemplate.opsForValue().get("");
        } catch (RedisConnectionFailureException ex) {
            throw new RedisConnectionFailureException(ex.getMessage());
        }
    }

    public void setLogout(String key, String value, long milliSeconds) {
        defaultRedisTemplate.opsForValue().set(key, value, milliSeconds, TimeUnit.MILLISECONDS);
    }

    public void setQuiz(String key, QuizDTO.CreateRedis value, long milliSeconds) {
        quizRedisTemplate.opsForValue().set(key, value, milliSeconds, TimeUnit.MILLISECONDS);
    }

    public QuizDTO.CreateRedis getQuiz(String key) {
        return quizRedisTemplate.opsForValue().get(key);
    }

    public void setQuizReport(String key, long milliSeconds) {
        defaultRedisTemplate.opsForValue().set(key, "quizReport", milliSeconds, TimeUnit.MILLISECONDS);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(defaultRedisTemplate.hasKey(key));
    }

    public void deleteValue(String key) {
        defaultRedisTemplate.delete(key);
    }




}
