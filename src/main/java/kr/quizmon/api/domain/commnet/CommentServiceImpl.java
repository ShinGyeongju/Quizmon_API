package kr.quizmon.api.domain.commnet;

import kr.quizmon.api.domain.quiz.QuizEntity;
import kr.quizmon.api.domain.quiz.QuizRepository;
import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public CommentDTO.GetListResponse getCommentList(CommentDTO.GetListRequest requestDto) {
        // 퀴즈 존재 여부 확인
        QuizEntity quiz = quizRepository.findByQuizId(UUID.fromString(requestDto.getQuizId()))
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_QUIZ_ID));

        // 퀴즈 순번 설정
        int count = requestDto.getCount() != null ? requestDto.getCount() : 10;

        // Pagination 설정
        Pageable page = PageRequest.of(requestDto.getPage() - 1, count, Sort.by("createdAt").descending());

        // DB 조회
        Page<CommentEntity> commentEntities = commentRepository.findByQuizEntity(quiz, page);

        // 응답 배열 생성
        CommentDTO.GetListResponse.Comment[] comments = commentEntities.map(comment ->
                CommentDTO.GetListResponse.Comment.builder()
                        .commentId(String.valueOf(comment.getComment_id()))
                        .score(comment.getScore())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .stream()
                .toArray(CommentDTO.GetListResponse.Comment[]::new);

        return CommentDTO.GetListResponse.builder()
                .totalPage(commentEntities.getTotalPages())
                .currentPage(commentEntities.getPageable().getPageNumber() + 1)
                .countPerPage(commentEntities.getPageable().getPageSize())
                .commentArray(comments)
                .build();
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
