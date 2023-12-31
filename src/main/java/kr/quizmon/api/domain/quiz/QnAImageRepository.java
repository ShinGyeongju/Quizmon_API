package kr.quizmon.api.domain.quiz;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface QnAImageRepository extends JpaRepository<QnAImageEntity, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM tb_qna_image t WHERE t.quizEntity.quizId = :quizId")
    void deleteAllByQuizId(@Param("quizId") UUID quizId);

    List<QnAImageEntity> findAllByQuizEntity_QuizId(UUID quizId);

}
