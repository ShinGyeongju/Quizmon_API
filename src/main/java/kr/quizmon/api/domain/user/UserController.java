package kr.quizmon.api.domain.user;

import jakarta.validation.Valid;
import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import kr.quizmon.api.global.common.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    /**
     * 회원 가입 API
     * @param UserDTO.CreateRequest
     * @return ResponseEntity<ResponseWrapper>
     */
    @PostMapping("/v1")
    public ResponseEntity<ResponseWrapper> postUser(@Valid @RequestBody UserDTO.CreateRequest requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            throw new CustomApiException(ErrorCode.INVALID_VALUE, errors.get(0).getDefaultMessage());
        }

        UserDTO.CommonResponse responseBody = userService.createUser(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 회원 수정 API
     * @param
     * @return
     */
    @PutMapping("/v1")
    public ResponseEntity<ResponseWrapper> putUser(@Valid @RequestBody UserDTO.)

}
