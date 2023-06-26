package kr.quizmon.api.domain.user;

import kr.quizmon.api.global.Util.JwtProvider;
import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public UserDTO.CommonResponse updateUser(UserDTO.UpdateRequest requestDto) {
        // ID 존재 여부 확인
        UserEntity user = userRepository.findById(requestDto.getId())
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_USER));

        // 기존 비밀번호 확인
        if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
            throw new CustomApiException(ErrorCode.INVALID_USER);
        }

        // 새 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());

        // 비밀번호 변경
        user.setPassword(encodedPassword);

        return UserDTO.CommonResponse.builder()
                .id(user.getId())
                .build();
    }

    @Override
    public UserDTO.CommonResponse deleteUser(UserDTO.DeleteRequest requestDto) {
        return null;
    }

    @Override
    public UserDTO.LoginResponse login(UserDTO.LoginRequest requestDto) {
        // ID 존재 여부 확인
        UserEntity user = userRepository.findById(requestDto.getId())
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_USER));

        // 비밀번호 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomApiException(ErrorCode.INVALID_USER);
        }

        // JWT 토큰 생성
        String token = jwtProvider.createToken(user.getId(), user.getAuthority());

        return UserDTO.LoginResponse.builder()
                .id(user.getId())
                .token(token)
                .build();
    }

    @Override
    public UserDTO.CommonResponse logout(String id) {
        return null;
    }

}
