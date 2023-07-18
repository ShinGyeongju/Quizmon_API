package kr.quizmon.api.domain.quiz;

import java.util.List;

public interface QuizRepositoryCustom {
    List<QuizDTO.GetListResponse.Quiz> findAllOrderByUpdated_at();
}
