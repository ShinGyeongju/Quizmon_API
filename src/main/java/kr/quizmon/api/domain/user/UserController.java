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

import java.util.Objects;

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
     * 토큰 확인
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/check")
    public ResponseEntity<ResponseWrapper> checkApi(Authentication auth) {
        UserDTO.CheckResponse responseBody = UserDTO.CheckResponse.builder()
                .id(auth.getName())
                .valid(true)
                .build();

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * ID 확인
     */
    @GetMapping("/{id}/check")
    public ResponseEntity<ResponseWrapper> checkUserApi(@PathVariable("id") String id) {
        UserDTO.Check checkDto = UserDTO.Check.builder()
                .id(id)
                .build();

        UserDTO.CheckUserResponse responseBody = userService.checkUser(checkDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 회원 가입
     */
    @PostMapping()
    public ResponseEntity<ResponseWrapper> createUserAPi(@Valid @RequestBody UserDTO.CreateRequest requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            String errorCode = Objects.requireNonNull(error.getCodes())[1];
            String errorArgs = errorCode.split("\\.")[1];

            switch (errorArgs) {
                case "id" -> throw new CustomApiException(ErrorCode.INVALID_ID);
                case "password" -> throw new CustomApiException(ErrorCode.INVALID_PASSWORD);
                default -> throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
            }
        }

        UserDTO.CommonResponse responseBody = userService.createUser(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 회원 탈퇴
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete")
    public ResponseEntity<ResponseWrapper> deleteUserApi(@Valid @RequestBody UserDTO.DeleteRequest requestDto, BindingResult bindingResult, Authentication auth) {
        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().get(0);
            throw new CustomApiException(ErrorCode.INVALID_VALUE, error.getDefaultMessage());
        }

        // 인가된 사용자 id 설정
        requestDto.setId(auth.getName());

        UserDTO.CommonResponse responseBody = userService.deleteUser(requestDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
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
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/logout")
    public ResponseEntity<ResponseWrapper> logoutApi(@RequestHeader("Authorization") String token, Authentication auth) {
        // 인가된 사용자 id 및 JWT 토큰 설정
        UserDTO.Logout logoutDto = UserDTO.Logout.builder()
                .id(auth.getName())
                .token(token)
                .build();

        UserDTO.CommonResponse responseBody = userService.logout(logoutDto);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(200)
                .message("OK")
                .result(responseBody)
                .build();

        return ResponseEntity.ok(response);
    }

}
