package kr.quizmon.api.domain.user;

public interface UserService {
    UserDTO.CreateResponse createUser(UserDTO.CreateRequest requestDto);
}
