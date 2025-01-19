package s_a_rb01_its6.userservice.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.repository.PagingAndSortingRepository;
import s_a_rb01_its6.userservice.repository.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>, PagingAndSortingRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<UserEntity> findById(String id);

    Optional<UserEntity> findByUsername(String username);

        Page<UserEntity> findByUsernameContainingIgnoreCase(String query, Pageable pageable);

    void deleteById(String id);
}
