package s_a_rb01_its6.userservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import s_a_rb01_its6.userservice.repository.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);
}
