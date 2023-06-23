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
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    /**
     * 회원 가입 API
     */
    @PostMapping()
    public ResponseEntity<ResponseWrapper> createUser(@Valid @RequestBody UserDTO.CreateRequest requestDto, BindingResult bindingResult) {
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
     */
    @PutMapping("/v1")
    public ResponseEntity<ResponseWrapper> updateUser(@Valid @RequestBody UserDTO.UpdateRequest requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            throw new CustomApiException(ErrorCode.INVALID_VALUE, errors.get(0).getDefaultMessage());
        }

        UserDTO.CommonResponse responseBody = userService.updateUser(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

}
