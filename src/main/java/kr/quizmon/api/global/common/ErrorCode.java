package kr.quizmon.api.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    INVALID_VALUE(HttpStatus.BAD_REQUEST, 10300, "유효하지 않은 값입니다."),
    ALREADY_EXISTS_USER(HttpStatus.BAD_REQUEST, 10301, "이미 존재하는 ID입니다."),

    // 401 Unauthorized
    INVALID_USER(HttpStatus.UNAUTHORIZED, 11300, "유효하지 않은 사용자입니다."),

    // 403 Forbidden
    FORBIDDEN_USER(HttpStatus.FORBIDDEN, 12300, "권한이 없는 사용자입니다."),


    // 404 Not Found


    // 409 Conflict


    // 500 Internal Server Error
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 15300, "서버 내부 오류입니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
