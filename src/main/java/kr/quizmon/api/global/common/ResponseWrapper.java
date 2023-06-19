package kr.quizmon.api.global.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseWrapper {
    private int code;
    private String message;
    private Object result;
}
