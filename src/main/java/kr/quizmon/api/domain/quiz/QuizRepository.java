package kr.quizmon.api.domain.quiz;

import kr.quizmon.api.domain.user.UserEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<QuizEntity, UUID>, QuizRepositoryCustom {
    Optional<QuizEntity> findByQuizId(UUID id);

//    @Query(value = "SELECT q FROM tb_quiz q WHERE ")
//    List<QuizEntity> findAllByListRequest(Sort sort);

}
