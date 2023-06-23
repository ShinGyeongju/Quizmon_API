package kr.quizmon.api.domain.user;

import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO.CommonResponse createUser(UserDTO.CreateRequest requestDto) {
        // ID 중복 검사
        userRepository
                .findById(requestDto.getId())
                .ifPresent(user -> {
                    throw new CustomApiException(ErrorCode.ALREADY_EXISTS_USER);
                 });

        // Password encoding
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        requestDto.setPassword(encodedPassword);
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
        Optional<UserEntity> user = userRepository.findById(requestDto.getId());

        // ID 존재 여부 확인
        if (user.isEmpty()) {
            throw new CustomApiException(ErrorCode.INVALID_USER);
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.get().getPassword())) {
            throw new CustomApiException(ErrorCode.INVALID_USER);
        }





        return UserDTO.CommonResponse.builder()
                .id(user.get().getId())
                .build();
    }

    @Override
    public UserDTO.CommonResponse logout(String id) {
        return null;
    }

}
