package kr.quizmon.api.domain.quiz;

public interface QuizService {
    QuizDTO.CreateResponse createImageQuiz(QuizDTO.CreateRequest requestDto);
    QuizDTO.UpdateResponse updateImageQuiz(QuizDTO.UpdateRequest requestDto);
    QuizDTO.CheckResponse checkImageQuiz(QuizDTO.CommonRequest commonDto);


    QuizDTO.GetResponse getQuiz(QuizDTO.CommonRequest commonDto);
}
