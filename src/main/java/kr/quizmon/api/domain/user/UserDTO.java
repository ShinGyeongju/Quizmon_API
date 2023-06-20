package kr.quizmon.api.domain.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDTO {
    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "유효하지 않은 ID입니다.")
        private String id;

        @NotBlank(message = "유효하지 않은 Password입니다.")
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
    public static class PutRequest {
        @NotBlank
        private String oldPassword;

        @NotBlank
        private String newPassword;

        // TODO: toEntity
    }

    @Getter
    @NoArgsConstructor
    public static class DeleteRequest {
        @NotBlank
        private String password;

        // TODO: toEntity
    }

    @Getter
    @Builder
    public static class CommonResponse {
        private String id;
    }
}
