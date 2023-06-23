package kr.quizmon.api.domain.user;

public interface UserService {
    UserDTO.CommonResponse createUser(UserDTO.CreateRequest requestDto);
    UserDTO.CommonResponse updateUser(UserDTO.UpdateRequest requestDto);

    UserDTO.CommonResponse deleteUser(UserDTO.DeleteRequest requestDto);
    UserDTO.CommonResponse login(UserDTO.LoginRequest requestDto);
    UserDTO.CommonResponse logout(String id);
}
