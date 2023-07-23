package kr.quizmon.api.domain.commnet;

import io.lettuce.core.dynamic.annotation.Param;
import kr.quizmon.api.domain.quiz.QuizEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    Page<CommentEntity> findByQuizEntity(QuizEntity quiz, Pageable page);

    @Query("SELECT comment_id FROM tb_comment WHERE comment_id = :commentId")
    Optional<Integer> findById(@Param("commentId") int commentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM tb_comment WHERE comment_id = :commentId")
    void deleteById(@Param("commentId") int commentId);
}
