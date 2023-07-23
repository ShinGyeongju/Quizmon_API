package kr.quizmon.api.domain.commnet;

public interface CommentService {
    CommentDTO.CommonResponse createComment(CommentDTO.CreateRequest requestDto);
    CommentDTO.GetListResponse getCommentList(CommentDTO.GetListRequest requestDto);
    CommentDTO.CommonResponse deleteComment(CommentDTO.CommonRequest commonDto);
    CommentDTO.CommonResponse reportComment(CommentDTO.CommonRequest commonDto);
}
