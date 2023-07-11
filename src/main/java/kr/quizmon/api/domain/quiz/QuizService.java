package kr.quizmon.api.domain.quiz;

public interface QuizService {
    QuizDTO.CreateStartResponse createStartQuiz(QuizDTO.CreateRequest requestDto);
    QuizDTO.CreateEndResponse createEndQuiz(QuizDTO.CommonRequest commonDto);
    QuizDTO.UpdateStartResponse updateStartQuiz(QuizDTO.UpdateRequest requestDto);


    QuizDTO.GetResponse getQuiz(QuizDTO.CommonRequest commonDto);
}
