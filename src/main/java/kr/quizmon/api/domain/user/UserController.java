package kr.quizmon.api.domain.user;

import jakarta.validation.Valid;
import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import kr.quizmon.api.global.common.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;


    // TEST
    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public String testApi(Authentication auth) {
        System.out.println(auth.getAuthorities());
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
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
    @PreAuthorize("isAuthenticated()")
    @PutMapping("")
    public ResponseEntity<ResponseWrapper> updateUserApi(@Valid @RequestBody UserDTO.UpdateRequest requestDto, BindingResult bindingResult, Authentication auth) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        // 인가된 사용자 id 설정
        requestDto.setId(auth.getName());

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

        UserDTO.LoginResponse responseBody = userService.login(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

}
