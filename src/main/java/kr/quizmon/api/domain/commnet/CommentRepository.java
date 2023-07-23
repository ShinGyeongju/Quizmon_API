package kr.quizmon.api.domain.commnet;

import kr.quizmon.api.domain.quiz.QuizEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    Page<CommentEntity> findByQuizEntity(QuizEntity quiz, Pageable page);

}
