package kr.quizmon.api.domain.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
//@AllArgsConstructor
//@Builder
public class UserDTO {
    @NotBlank
    private String id;

    @NotBlank
    private String password;
}
