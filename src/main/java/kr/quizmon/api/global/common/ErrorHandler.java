package kr.quizmon.api.global.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    // Common api handler
    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<ResponseWrapper> apiHandler(CustomApiException ex) {
        ResponseWrapper response = ResponseWrapper.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .result(null)
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
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

        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

}
