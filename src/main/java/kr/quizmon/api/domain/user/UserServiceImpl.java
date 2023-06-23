package kr.quizmon.api.domain.user;

import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDTO.CommonResponse createUser(UserDTO.CreateRequest requestDto) {
        // ID 중복 검사
        if (userRepository.findById(requestDto.getId()) != null) {
            throw new CustomApiException(ErrorCode.ALREADY_EXISTS_USER);
        }

        UserEntity user = userRepository.save(requestDto.toEntity());

        return UserDTO.CommonResponse.builder()
                .id(user.getId())
                .build();
    }

    @Override
    public UserDTO.CommonResponse updateUser(UserDTO.UpdateRequest requestDto) {
        return null;
    }

    @Override
    public UserDTO.CommonResponse deleteUser(UserDTO.DeleteRequest requestDto) {
        return null;
    }

    @Override
    public UserDTO.CommonResponse login(UserDTO.LoginRequest requestDto) {
        return null;
    }

    @Override
    public UserDTO.CommonResponse logout(String id) {
        return null;
    }

}
