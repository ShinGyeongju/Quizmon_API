package kr.quizmon.api.domain.user;

import jakarta.validation.Valid;
import kr.quizmon.api.global.common.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @PostMapping("/v1")
    public ResponseEntity<CommonResponse> createUser(@Valid @RequestBody UserDTO requestBody) {

        CommonResponse response = CommonResponse.builder()
                .code(200)
                .message("OK")
                .result(requestBody)
                .build();

        return ResponseEntity.ok(response);
    }

}
