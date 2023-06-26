package kr.quizmon.api.global.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    // Common handler
    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<ResponseWrapper> commonHandler(CustomApiException ex) {
        ResponseWrapper response = ResponseWrapper.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .result(null)
                .build();

        log.error(ex.toString());

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    // Access denied handler
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseWrapper> accessDeniedHandler(AccessDeniedException ex) {
        ErrorCode error = ErrorCode.FORBIDDEN_USER;

        ResponseWrapper response = ResponseWrapper.builder()
                .code(error.getCode())
                .message(error.getMessage())
                .result(null)
                .build();

        log.error(ex.toString());

        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }
    // Default handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper> defaultHandler(Exception ex) {
        ErrorCode error = ErrorCode.INTERNAL_ERROR;

        ResponseWrapper response = ResponseWrapper.builder()
                .code(error.getCode())
                .message(error.getMessage())
                .result(null)
                .build();

        log.error(ex.toString());

        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

}
