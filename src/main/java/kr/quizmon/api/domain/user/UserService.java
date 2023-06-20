package kr.quizmon.api.domain.user;

public interface UserService {
    UserDTO.CommonResponse createUser(UserDTO.CreateRequest requestDto);
}
