package kr.quizmon.api.domain.user;

import jakarta.validation.Valid;
import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import kr.quizmon.api.global.common.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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


    // TEST
    @GetMapping()
    public String testApi() {
        return "TEST API";
    }



    /**
     * 회원 가입
     */
    @PostMapping()
    public ResponseEntity<ResponseWrapper> createUserAPi(@Valid @RequestBody UserDTO.CreateRequest requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        UserDTO.CommonResponse responseBody = userService.createUser(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(HttpStatus.CREATED.value())
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 회원 수정
     */
    @PutMapping("")
    public ResponseEntity<ResponseWrapper> updateUserApi(@Valid @RequestBody UserDTO.UpdateRequest requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        UserDTO.CommonResponse responseBody = userService.updateUser(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }



    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper> loginApi(@Valid @RequestBody UserDTO.LoginRequest requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        UserDTO.CommonResponse responseBody = userService.login(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

}
