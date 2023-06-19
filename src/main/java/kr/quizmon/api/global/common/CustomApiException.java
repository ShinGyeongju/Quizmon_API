package kr.quizmon.api.global.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomApiException extends RuntimeException {
    private HttpStatus httpStatus;
    private int code;
    private String message;

    public CustomApiException(ErrorCode errorCode) {
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public CustomApiException(ErrorCode errorCode, String message) {
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.message = message;
    }
}
