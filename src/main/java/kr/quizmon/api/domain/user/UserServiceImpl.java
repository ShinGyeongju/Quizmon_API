package kr.quizmon.api.domain.user;

import kr.quizmon.api.global.Util.JwtProvider;
import kr.quizmon.api.global.Util.RedisIO;
import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisIO redisIO;

    @Override
    public UserDTO.CheckResponse checkUser(UserDTO.Check checkDto) {
        // ID 중복 검사
        boolean idExists = userRepository.findById(checkDto.getId()).isPresent();

        return UserDTO.CheckResponse.builder()
                .id(checkDto.getId())
                .idExists(idExists)
                .build();
    }

    @Override
    @Transactional
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
    @Transactional
    public UserDTO.CommonResponse deleteUser(UserDTO.DeleteRequest requestDto) {
        // ID 존재 여부 확인
        UserEntity user = userRepository.findById(requestDto.getId())
                .orElseThrow(() -> new CustomApiException(ErrorCode.INVALID_USER));

        // 비밀번호 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomApiException(ErrorCode.INVALID_USER);
        }

        // 사용자 삭제
        userRepository.deleteById(requestDto.getId());

        return UserDTO.CommonResponse.builder()
                .id(user.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
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
    public UserDTO.CommonResponse logout(UserDTO.Logout logoutDto) {
        long Expiration = jwtProvider.getExpiration(logoutDto.getToken()).getTime();
        long now = new Date().getTime();

        // Redis에 token등록
        redisIO.setLogout(logoutDto.getToken(), "logout", Expiration - now);

        return UserDTO.CommonResponse.builder()
                .id(logoutDto.getId())
                .build();
    }

}
