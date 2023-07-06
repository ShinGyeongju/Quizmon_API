package kr.quizmon.api.domain.quiz;

import kr.quizmon.api.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<QuizEntity, UUID> {
    Optional<QuizEntity> findByQuizId(UUID id);

}
