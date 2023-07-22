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

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;

    private final List<Short> VALID_LIMIT_TIME = List.of((short)3, (short)4, (short)5, (short)7, (short)10);

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

        if (!VALID_LIMIT_TIME.contains(requestDto.getLimitTime())) {
            throw new CustomApiException(ErrorCode.INVALID_VALUE, "유효하지 않은 제한 시간입니다.");
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

        if (!VALID_LIMIT_TIME.contains(requestDto.getLimitTime())) {
            throw new CustomApiException(ErrorCode.INVALID_VALUE, "유효하지 않은 제한 시간입니다.");
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
     * 퀴즈 삭제
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper> deleteQuizApi(@PathVariable("id") String quizId, Authentication auth) {
        QuizDTO.CommonRequest commonDto = QuizDTO.CommonRequest.builder()
                .userId(auth.getName())
                .quizId(quizId)
                .build();

        QuizDTO.CommonResponse responseBody = quizService.deleteQuiz(commonDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 퀴즈 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper> getQuizApi(@PathVariable("id") String urlId, @Valid QuizDTO.GetRequest requestDto, BindingResult bindingResult, Authentication auth) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        // 사용자 인증 정보 설정
        String userId = auth != null ? auth.getName() : null;
        String userAuthority = auth != null ? auth.getAuthorities().toArray()[0].toString() : "ANONYMOUS";

        requestDto.setUserId(userId);
        requestDto.setUserAuthority(userAuthority);
        requestDto.setUrlId(urlId);

        QuizDTO.GetResponse responseBody = quizService.getQuiz(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 퀴즈 목록 조회
     */
    @GetMapping("/list")
    public ResponseEntity<ResponseWrapper> getQuizListApi(@Valid QuizDTO.GetListRequest requestDto, BindingResult bindingResult, Authentication auth) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        // 사용자 id 설정
        String userAuthority = auth != null ? auth.getAuthorities().toArray()[0].toString() : "ANONYMOUS";
        String userId = auth != null ? auth.getName() : null;
        requestDto.setUserId(userId);

        // 유효성 검사
        if (requestDto.getSort().equals("4") && !userAuthority.equals("ADMIN")) throw new CustomApiException(ErrorCode.FORBIDDEN_USER);
        if (requestDto.getUserOnly() != null && requestDto.getUserOnly() && userId == null) throw new CustomApiException(ErrorCode.FORBIDDEN_USER);


        QuizDTO.GetListResponse responseBody = quizService.getQuizList(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 퀴즈 신고
     */
    @GetMapping("/{id}/report")
    public ResponseEntity<ResponseWrapper> reportApi(@PathVariable("id") String quizId) {
        QuizDTO.CommonRequest commonDto = QuizDTO.CommonRequest.builder()
                .quizId(quizId)
                .build();

        QuizDTO.CommonResponse responseBody = quizService.reportQuiz(commonDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 퀴즈 신고 초기화
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/report/reset")
    public ResponseEntity<ResponseWrapper> reportResetApi(@PathVariable("id") String quizId, Authentication auth) {
        // 관리자 권한 확인
        if (!auth.getAuthorities().toArray()[0].toString().equals("ADMIN")) throw new CustomApiException(ErrorCode.FORBIDDEN_USER);

        QuizDTO.CommonRequest commonDto = QuizDTO.CommonRequest.builder()
                .quizId(quizId)
                .build();

        QuizDTO.CommonResponse responseBody = quizService.reportResetQuiz(commonDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 퀴즈 플레이 결과 등록
     */
    @PostMapping("/{id}/play/result")
    public ResponseEntity<ResponseWrapper> createScoreApi(@Valid @RequestBody QuizDTO.PlayResultRequest requestDto, BindingResult bindingResult, @PathVariable("id") String quizId) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        // 유효성 검사
        if (requestDto.getAnswerCount() > requestDto.getQuestionCount()) throw new CustomApiException(ErrorCode.INVALID_VALUE, "유효하지 않은 정답 개수입니다.");

        // quizId 설정
        requestDto.setQuizId(quizId);

        QuizDTO.PlayResultResponse responseBody = quizService.createPlayResult(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
