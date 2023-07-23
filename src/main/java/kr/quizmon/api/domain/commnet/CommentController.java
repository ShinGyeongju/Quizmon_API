package kr.quizmon.api.domain.commnet;

import jakarta.validation.Valid;
import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import kr.quizmon.api.global.common.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 생성
     */
    @PostMapping("/{id}")
    public ResponseEntity<ResponseWrapper> createCommentApi(@Valid @RequestBody CommentDTO.CreateRequest requestDto, @PathVariable("id") String quizId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        // quizId 설정
        requestDto.setQuizId(quizId);

        CommentDTO.CommonResponse responseBody = commentService.createComment(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 댓글 목록 조회
     */
    @GetMapping("/{id}/list")
    public ResponseEntity<ResponseWrapper> getCommentListApi(@Valid CommentDTO.GetListRequest requestDto, @PathVariable("id") String quizId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        // quizId 설정
        requestDto.setQuizId(quizId);

        CommentDTO.GetListResponse responseBody = commentService.getCommentList(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 삭제
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper> deleteCommentApi(@PathVariable("id") String commentId, Authentication auth) {
        // 관리자 권한 확인
        if (!auth.getAuthorities().toArray()[0].toString().equals("ADMIN")) throw new CustomApiException(ErrorCode.FORBIDDEN_USER);

        CommentDTO.CommonRequest commonDto = CommentDTO.CommonRequest.builder()
                .commentId(commentId)
                .build();

        CommentDTO.CommonResponse responseBody = commentService.deleteComment(commonDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }


}
