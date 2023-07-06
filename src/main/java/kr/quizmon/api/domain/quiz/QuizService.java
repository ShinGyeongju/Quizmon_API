package kr.quizmon.api.domain.quiz;

public interface QuizService {
    QuizDTO.CreateStartResponse createStartQuiz(QuizDTO.CreateRequest requestDto);

}
