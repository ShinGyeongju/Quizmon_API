package kr.quizmon.api.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDTO {
    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "유효하지 않은 ID입니다.")
        @Pattern(regexp="^[0-9a-zA-Z]*$" , message="ID는 영문자/숫자 조합만 가능합니다.")
        @Size(min = 4, max = 20, message = "ID는 최소 4, 최대 20글자 까지만 가능합니다.")
        private String id;

        @NotBlank(message = "유효하지 않은 Password입니다.")
        @Size(max = 20, message = "Password는 최대 20글자 까지만 가능합니다.")
        private String password;

        public UserEntity toEntity() {
            return UserEntity.builder()
                    .id(id)
                    .password(password)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "유효하지 않은 Password입니다.")
        private String oldPassword;

        @NotBlank(message = "유효하지 않은 Password입니다.")
        private String newPassword;

        // TODO: toEntity
    }

    @Getter
    @NoArgsConstructor
    public static class DeleteRequest {
        @NotBlank(message = "유효하지 않은 Password입니다.")
        private String password;

        // TODO: toEntity
    }

    @Getter
    @NoArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "유효하지 않은 ID입니다.")
        private String id;

        @NotBlank(message = "유효하지 않은 Password입니다.")
        private String password;

        // TODO: toEntity
    }

    @Getter
    @Builder
    public static class CommonResponse {
        private String id;
    }
}
