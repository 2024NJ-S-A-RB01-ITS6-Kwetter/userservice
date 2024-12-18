package s_a_rb01_its6.userservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import s_a_rb01_its6.userservice.repository.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<UserEntity> findById(String id);

    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findByUsernameContainingIgnoreCase (String username);

    void deleteById(String id);
}
