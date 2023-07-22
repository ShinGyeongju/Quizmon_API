package kr.quizmon.api.domain.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ScoreRepository extends JpaRepository<ScoreEntity, Integer> {
//    interface ScoreDTO {
//        String getQuizId();
//        int getScore();
//    }

//    @Query(value = "SELECT quizId, sum(count) AS score FROM (SELECT quiz_id AS quizId, count(*) AS count FROM tb_score WHERE created_at > (NOW() - interval '1 day') GROUP BY quiz_id UNION ALL SELECT quiz_id AS quizId, count(*) AS count FROM tb_score WHERE created_at > (NOW() - interval '3 hour') GROUP BY quiz_id) AS u GROUP BY quizId", nativeQuery = true)
//    List<ScoreDTO> findAllOrderByPopularity();

    @Modifying
    @Transactional
    @Query(value = "UPDATE tb_quiz AS q SET popularity_score = u.score FROM ( SELECT quiz_id, coalesce(score, 0) AS score FROM tb_quiz AS q LEFT JOIN (SELECT quizId, sum(count)  AS score FROM ( SELECT quiz_id AS quizId, count(*) AS count FROM tb_score WHERE created_at > (NOW() - interval '3 day') GROUP BY quiz_id UNION ALL SELECT quiz_id AS quizId, count(*) AS count FROM tb_score WHERE created_at > (NOW() - interval '3 hour') GROUP BY quiz_id) AS u GROUP BY quizId ) AS a ON q.quiz_id = a.quizId) AS u WHERE q.quiz_id = u.quiz_id", nativeQuery = true)
    void updateAllPopularityScore();

    @Query(value = "SELECT rank FROM (SELECT score_id, rank() OVER (ORDER BY score DESC) FROM tb_score WHERE quiz_id = :quizId) AS s WHERE score_id = :scoreId", nativeQuery = true)
    int findRankByQuizIdAndScoreId(@Param("quizId") UUID quizId, @Param("scoreId") int scoreId);

    int countByQuizEntity(QuizEntity quiz);

}