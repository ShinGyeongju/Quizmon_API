package kr.quizmon.api.domain.commnet;

import jakarta.validation.Valid;
import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import kr.quizmon.api.global.common.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


}
