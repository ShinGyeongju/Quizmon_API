package kr.quizmon.api.domain.quiz;

import java.util.List;

public interface QuizRepositoryCustom {
    List<QuizEntity> findAllOrderByUpdated_at();
}
