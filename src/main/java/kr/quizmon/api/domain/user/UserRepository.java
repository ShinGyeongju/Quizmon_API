package kr.quizmon.api.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    //UserEntity save(UserEntity userEntity);
    UserEntity findById(String id);
}
