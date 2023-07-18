package kr.quizmon.api.domain.quiz;

import java.util.List;

public interface QuizRepositoryCustom {
    List<QuizDTO.GetListResponse.Quiz> findAllOrderByCustom(QuizDTO.QuizListQuery queryDto);
}
