package kr.quizmon.api.domain.user;

import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDTO.CommonResponse createUser(UserDTO.CreateRequest requestDto) {
        try {
            UserEntity user = userRepository.save(requestDto.toEntity());

            return UserDTO.CommonResponse.builder()
                    .id(user.getId())
                    .build();
        } catch (Exception ex) {
            log.error(ex.toString());
            throw new CustomApiException(ErrorCode.ALREADY_EXISTS_USER);
        }

    }

}
