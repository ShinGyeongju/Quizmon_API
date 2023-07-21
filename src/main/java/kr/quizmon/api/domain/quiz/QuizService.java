package kr.quizmon.api.domain.quiz;

public interface QuizService {
    QuizDTO.CreateResponse createImageQuiz(QuizDTO.CreateRequest requestDto);
    QuizDTO.UpdateResponse updateImageQuiz(QuizDTO.UpdateRequest requestDto);
    QuizDTO.CheckResponse checkImageQuiz(QuizDTO.CommonRequest commonDto);
    QuizDTO.CommonResponse deleteQuiz(QuizDTO.CommonRequest commonDto);
    QuizDTO.GetResponse getQuiz(QuizDTO.GetRequest requestDto);
    QuizDTO.GetListResponse getQuizList(QuizDTO.GetListRequest requestDto);
    QuizDTO.CommonResponse reportQuiz(QuizDTO.CommonRequest commonDto);
    QuizDTO.CommonResponse reportResetQuiz(QuizDTO.CommonRequest commonDto);


}
