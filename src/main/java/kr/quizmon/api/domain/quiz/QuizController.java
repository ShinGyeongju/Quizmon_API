package kr.quizmon.api.domain.quiz;

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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;

    // TEST
    @GetMapping("/test")
    @PreAuthorize("isAuthenticated()")
    public String testApi(Authentication auth) {

        //redisIO.deleteQuiz("0691489a-2a9f-42e4-8246-2412c4a401fb");


        return auth.getName();
    }

    /**
     * 이미지 퀴즈 생성
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image")
    public ResponseEntity<ResponseWrapper> createImageQuizApi(@Valid @RequestBody QuizDTO.CreateRequest requestDto, BindingResult bindingResult, Authentication auth) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        // 인가된 사용자 id 설정
        requestDto.setUserId(auth.getName());
        // UUID 설정
        requestDto.setQuizId(UUID.randomUUID());

        QuizDTO.CreateResponse responseBody = quizService.createImageQuiz(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 이미지 퀴즈 수정
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/image")
    public ResponseEntity<ResponseWrapper> updateImageQuizApi(@Valid @RequestBody QuizDTO.UpdateRequest requestDto, BindingResult bindingResult, @PathVariable("id") String quizId, Authentication auth) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        // 인가된 사용자 id 설정
        requestDto.setUserId(auth.getName());
        // UUID 설정
        requestDto.setQuizId(UUID.fromString(quizId));

        QuizDTO.UpdateResponse responseBody = quizService.updateImageQuiz(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 이미지 퀴즈 확인
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/image/check")
    public ResponseEntity<ResponseWrapper> checkImageQuizApi(@PathVariable("id") String quizId, Authentication auth) {
        QuizDTO.CommonRequest commonDto = QuizDTO.CommonRequest.builder()
                .userId(auth.getName())
                .quizId(quizId)
                .build();

        QuizDTO.CheckResponse responseBody = quizService.checkImageQuiz(commonDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper> getQuizApi(@PathVariable("id") String quizId, Authentication auth) {
        String userId = auth != null ? auth.getName() : null;

        QuizDTO.CommonRequest commonDto = QuizDTO.CommonRequest.builder()
                .userId(userId)
                .quizId(quizId)
                .build();

        QuizDTO.GetResponse responseBody = quizService.getQuiz(commonDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 목록 조회
     */
//    @GetMapping("/list")
//    public ResponseEntity<ResponseWrapper> getQuizListApi() {
//
//    }


}
