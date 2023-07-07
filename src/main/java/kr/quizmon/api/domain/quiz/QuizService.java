package kr.quizmon.api.domain.quiz;

public interface QuizService {
    QuizDTO.CreateStartResponse createStartQuiz(QuizDTO.CreateRequest requestDto);
    QuizDTO.CreateEndResponse createEndQuiz(QuizDTO.CommonRequest commonDto);



    QuizDTO.GetResponse getQuiz(QuizDTO.CommonRequest commonDto);
}
