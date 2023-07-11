package kr.quizmon.api.domain.user;

public interface UserService {
    UserDTO.CheckUserResponse checkUser(UserDTO.Check checkDto);
    UserDTO.CommonResponse createUser(UserDTO.CreateRequest requestDto);
    UserDTO.CommonResponse updateUser(UserDTO.UpdateRequest requestDto);
    UserDTO.CommonResponse deleteUser(UserDTO.DeleteRequest requestDto);
    UserDTO.LoginResponse login(UserDTO.LoginRequest requestDto);
    UserDTO.CommonResponse logout(UserDTO.Logout logoutDto);
}
