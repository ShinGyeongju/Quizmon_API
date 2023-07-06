package kr.quizmon.api.domain.quiz;

import jakarta.validation.Valid;
import kr.quizmon.api.global.Util.S3Manager;
import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import kr.quizmon.api.global.common.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;
    private final S3Manager s3Manager;

    private final List<String> QUIZ_TYPE = List.of("IMAGE", "SOUND");

    // TEST
    @GetMapping("/test")
    public String testApi(@RequestPart(value = "file") MultipartFile file) {


        //String quizId = UUID.randomUUID().toString();
        String quizId = "test";


        return quizId;
    }

    /**
     * 퀴즈 생성 시작
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/start")
    public ResponseEntity<ResponseWrapper> createQuizStartApi(@Valid @RequestBody QuizDTO.CreateRequest requestDto, BindingResult bindingResult, Authentication auth) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        // 퀴즈 종류 유효성 검증
        if (!QUIZ_TYPE.contains(requestDto.getType().toUpperCase())) {
            throw new CustomApiException(ErrorCode.INVALID_VALUE);
        }

        // 인가된 사용자 id 설정
        requestDto.setUserId(auth.getName());
        // UUID 설정
        requestDto.setQuizId(UUID.randomUUID());

        QuizDTO.CreateStartResponse responseBody = quizService.createStartQuiz(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 퀴즈 생성 완료
     */

}
