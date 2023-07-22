package kr.quizmon.api.domain.commnet;

import kr.quizmon.api.domain.quiz.QuizEntity;
import kr.quizmon.api.domain.quiz.QuizRepository;
import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final QuizRepository quizRepository;

    @Override
    @Transactional
    public CommentDTO.CommonResponse createComment(CommentDTO.CreateRequest requestDto) {
        // 퀴즈 존재 여부 확인
        QuizEntity quiz = quizRepository.findByQuizId(UUID.fromString(requestDto.getQuizId()))
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_QUIZ_ID));

        // 댓글 저장
        CommentEntity comment = commentRepository.save(requestDto.toEntity(quiz));

        return CommentDTO.CommonResponse.builder()
                .commentId(String.valueOf(comment.getComment_id()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDTO.GetListResponse getCommentList(CommentDTO.CommonRequest commonDto) {
        return null;
    }

    @Override
    @Transactional
    public CommentDTO.CommonResponse deleteComment(CommentDTO.CommonRequest commonDto) {
        return null;
    }

    @Override
    @Transactional
    public CommentDTO.CommonResponse reportComment(CommentDTO.CommonRequest commonDto) {
        return null;
    }
}
