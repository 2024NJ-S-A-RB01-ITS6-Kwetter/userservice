package s_a_rb01_its6.userservice.service.impl;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.domain.responses.RegisterResponse;
import s_a_rb01_its6.userservice.repository.UserRepository;
import s_a_rb01_its6.userservice.repository.entities.UserDTO;
import s_a_rb01_its6.userservice.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public RegisterResponse createUser(UserRequest userRequest){
        //check if user exists
        if (Boolean.TRUE.equals(userRepository.existsByUsername(userRequest.username()))) {
            throw new EntityExistsException("Username already exists");
        }

        //TODO other checks regarding user such as password length, email format etc.


        UserDTO user = UserDTO.builder()
                .email(userRequest.email())
                .username(userRequest.username())
                .bio(userRequest.bio())
                .build();

        //TODO send to rabbitmq that user is created as well as to keycloak

        userRepository.save(user);
        return RegisterResponse.builder().message("User successfully registered!").build();
    }


    public void deleteUserById(Long id) {

        //TODO check if user is authorized to delete user!!!


        if (userRepository.findById(id).isEmpty()) {
            throw new EntityExistsException("User does not exist");
        }
        //TODO RABBITMQ
        //TODO KEYCLOAK
        userRepository.deleteById(id);
    }


    // username cant be changed because it is used as a unique identifier
    @Transactional
    public void updateUser(UserRequest userRequest) {
        userRepository.existsByUsername(userRequest.username());
        if (Boolean.FALSE.equals(userRepository.existsByUsername(userRequest.username()))) {
            throw new EntityExistsException("User does not exist");
        }
        //get user from db

        //TODO other checks regarding user such as password length, email format etc.
        //TODO make sure the user is changing their own profile not someone else's

        UserDTO user = UserDTO.builder()
                .id(userRequest.id())
                .email(userRequest.email())
                .username(userRequest.username())
                .bio(userRequest.bio())
                .build();

        userRepository.save(user);
    }
}
