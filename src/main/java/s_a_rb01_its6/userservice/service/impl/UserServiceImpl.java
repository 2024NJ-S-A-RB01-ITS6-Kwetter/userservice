package s_a_rb01_its6.userservice.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import s_a_rb01_its6.userservice.domain.requests.RegisterUserRequest;
import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.domain.responses.RegisterResponse;
import s_a_rb01_its6.userservice.domain.responses.UserResponse;
import s_a_rb01_its6.userservice.repository.UserRepository;
import s_a_rb01_its6.userservice.repository.entities.UserEntity;
import s_a_rb01_its6.userservice.service.UserService;
import s_a_rb01_its6.userservice.service.exception.ResourceNotFoundException;
import s_a_rb01_its6.userservice.service.impl.dtoconverter.UserDTOConverter;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    @Transactional
    @Override
    public RegisterResponse createUser(RegisterUserRequest userRequest){

        //Check if all of the unique parameters are unique
        if (Boolean.TRUE.equals(userRepository.existsByUsername(userRequest.username()))) {
            throw new EntityExistsException("Username already exists");
        }
        //TODO implement other unique parameter checks

        //TODO other checks regarding user such as password length, email format etc.


        UserEntity user = UserEntity.builder()
                .email(userRequest.email())
                .username(userRequest.username())
                .build();



        //TODO call the keycloak service to register the user
        keycloakService.registerUserInKeycloak(userRequest.username(), userRequest.password());


        //TODO then save the user in local database
        userRepository.save(user);
        //TODO then generate a rabbitmq message to notify other services that a new user is created

        return RegisterResponse.builder().message("User successfully registered!").build();
    }


    public void deleteUserById(Long id) {
        // Use Optional to safely handle the result
        Optional<UserEntity> user = userRepository.findById(id);

        // Check if the user exists
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist");
        }
        //TODO make sure this is transactional
        userRepository.deleteById(id);

        keycloakService.deleteUserInKeycloak(user.get().getUsername());

        //TODO RABBITMQ
    }

    @Override
    public UserResponse getUserById(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User does not exist"));
        return UserDTOConverter.toUserResponse(user);
    }


    // username cant be changed because it is used as a unique identifier
    @Transactional
    public void updateUser(UserRequest userRequest) {

        //TODO change response type to a response object

        //TODO check if the user updating is the same user as being updated or if they have admin rights.

        //TODO this needs to check if it exists by id since the username is changeable.
        if (Boolean.FALSE.equals(userRepository.existsByUsername(userRequest.username()))) {
            throw new EntityExistsException("User does not exist");
        }

        //TODO other checks regarding user such as password length, email format etc.

        UserEntity user = UserEntity.builder()
                .id(userRequest.id())
                .email(userRequest.email())
                .username(userRequest.username())
                .bio(userRequest.bio())
                .build();

        userRepository.save(user);

        //TODO RABBITMQ
    }


    //TODO Create a validator for the userRequest object
}
