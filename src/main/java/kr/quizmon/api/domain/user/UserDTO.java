package kr.quizmon.api.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

public class UserDTO {
    @Getter
    @Builder
    public static class Check {
        private String id;
    }

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "유효하지 않은 ID입니다.")
        @Pattern(regexp="^[0-9a-z]*$" , message="ID는 영(소)문자/숫자 조합만 가능합니다.")
        @Size(min = 4, max = 20, message = "ID는 최소 4, 최대 20글자 까지만 가능합니다.")
        private String id;

        @Setter
        @NotBlank(message = "유효하지 않은 Password입니다.")
        @Size(min = 4, max = 20, message = "Password는 최소 4, 최대 20글자 까지만 가능합니다.")
        private String password;

        public UserEntity toEntity() {
            return UserEntity.builder()
                    .id(id)
                    .password(password)
                    .authority("USER")
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        @Setter
        private String id;

        @NotBlank(message = "유효하지 않은 Password입니다.")
        private String oldPassword;

        @Setter
        @NotBlank(message = "유효하지 않은 Password입니다.")
        @Size(max = 20, message = "Password는 최대 20글자 까지만 가능합니다.")
        private String newPassword;
    }

    @Getter
    @NoArgsConstructor
    public static class DeleteRequest {
        @Setter
        private String id;

        @NotBlank(message = "유효하지 않은 Password입니다.")
        private String password;
    }

    @Getter
    @NoArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "유효하지 않은 사용자입니다.")
        private String id;

        @NotBlank(message = "유효하지 않은 사용자입니다.")
        private String password;
    }

    @Getter
    @Builder
    public static class Logout {
        private String id;
        private String token;
    }

    @Getter
    @Builder
    public static class CommonResponse {
        private String id;
    }

    @Getter
    @Builder
    public static class CheckResponse {
        private String id;
        private boolean valid;
    }

    @Getter
    @Builder
    public static class CheckUserResponse {
        private String id;
        private boolean idExists;
    }

    @Getter
    @Builder
    public static class LoginResponse {
        private String id;
        private String token;
    }
}
