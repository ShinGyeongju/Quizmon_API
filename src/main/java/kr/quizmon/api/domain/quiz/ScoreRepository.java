package kr.quizmon.api.domain.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScoreRepository extends JpaRepository<ScoreEntity, Integer> {
    interface ScoreDTO {
        String getQuizId();
        int getScore();
    }

    @Query(value = "SELECT quizId, sum(count) AS score FROM (SELECT quiz_id AS quizId, count(*) AS count FROM tb_score WHERE created_at > (NOW() - interval '1 day') GROUP BY quiz_id UNION ALL SELECT quiz_id AS quizId, count(*) AS count FROM tb_score WHERE created_at > (NOW() - interval '3 hour') GROUP BY quiz_id) AS u GROUP BY quizId", nativeQuery = true)
    List<ScoreDTO> findAllOrderByPopularity();

}