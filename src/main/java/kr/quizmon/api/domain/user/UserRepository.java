package kr.quizmon.api.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    //UserEntity save(UserEntity userEntity);
    Optional<UserEntity> findById(String id);

}
