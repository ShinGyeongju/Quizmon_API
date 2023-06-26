package kr.quizmon.api.domain.user;

import kr.quizmon.api.global.Util.JwtProvider;
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
    private final JwtProvider jwtProvider;

    @Override
    public UserDTO.CommonResponse createUser(UserDTO.CreateRequest requestDto) {
        // ID 중복 검사
        userRepository
                .findById(requestDto.getId())
                .ifPresent(user -> {
                    throw new CustomApiException(ErrorCode.ALREADY_EXISTS_USER);
                 });

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        requestDto.setPassword(encodedPassword);

        // 사용자 저장
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
    public UserDTO.LoginResponse login(UserDTO.LoginRequest requestDto) {
        Optional<UserEntity> user = userRepository.findById(requestDto.getId());

        // ID 존재 여부 확인
        if (user.isEmpty()) {
            throw new CustomApiException(ErrorCode.INVALID_USER);
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.get().getPassword())) {
            throw new CustomApiException(ErrorCode.INVALID_USER);
        }

        // JWT 토큰 생성
        String token = jwtProvider.createToken(user.get().getId(), user.get().getAuthority());

        return UserDTO.LoginResponse.builder()
                .id(user.get().getId())
                .token(token)
                .build();
    }

    @Override
    public UserDTO.CommonResponse logout(String id) {
        return null;
    }

}
