package kr.quizmon.api.domain.user;

import kr.quizmon.api.global.common.CustomApiException;
import kr.quizmon.api.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDTO.CreateResponse createUser(UserDTO.CreateRequest requestDto) {
        try {
            UserEntity user = userRepository.save(requestDto.toEntity());

            return UserDTO.CreateResponse.builder()
                    .id(user.getId())
                    .build();
        } catch (Exception ex) {
            throw new CustomApiException(ErrorCode.ALREADY_EXISTS_USER);
        }

    }

}
