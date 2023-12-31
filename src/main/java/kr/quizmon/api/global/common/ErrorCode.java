package kr.quizmon.api.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    INVALID_VALUE(HttpStatus.BAD_REQUEST, 10300, "유효하지 않은 값입니다."),
    INVALID_ID(HttpStatus.BAD_REQUEST, 10301, "유효하지 않은 아이디입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, 10302, "유효하지 않은 비밀번호입니다."),
    ALREADY_EXISTS_USER(HttpStatus.BAD_REQUEST, 10303, "이미 존재하는 아이디입니다."),
    INVALID_QUIZ_ID(HttpStatus.BAD_REQUEST, 10400, "유효하지 않은 퀴즈 아이디입니다."),
    INVALID_QUIZ_URL_ID(HttpStatus.BAD_REQUEST, 10401, "유효하지 않은 퀴즈 URL 아이디입니다."),
    INVALID_COMMENT_ID(HttpStatus.BAD_REQUEST, 10402, "유효하지 않은 댓글 아이디입니다."),

    // 401 Unauthorized
    INVALID_USER(HttpStatus.UNAUTHORIZED, 11300, "유효하지 않은 사용자입니다."),
    INVALID_OLD_PASSWORD(HttpStatus.UNAUTHORIZED, 11301, "유효하지 않은 현재 비밀번호입니다."),

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
