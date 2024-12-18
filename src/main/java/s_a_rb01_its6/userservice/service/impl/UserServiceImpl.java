package s_a_rb01_its6.userservice.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import s_a_rb01_its6.userservice.config.RabbitMQConfig;
import s_a_rb01_its6.userservice.domain.requests.RegisterUserRequest;
import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.domain.responses.DeleteUserResponse;
import s_a_rb01_its6.userservice.domain.responses.RegisterResponse;
import s_a_rb01_its6.userservice.domain.responses.UserResponse;
import s_a_rb01_its6.userservice.domain.responses.UserUpdatedResponse;
import s_a_rb01_its6.userservice.events.UserUpdatedEvent;
import s_a_rb01_its6.userservice.repository.UserRepository;
import s_a_rb01_its6.userservice.repository.entities.UserEntity;
import s_a_rb01_its6.userservice.service.UserService;
import s_a_rb01_its6.userservice.service.impl.dtoconverter.UserDTOConverter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @Override
    public RegisterResponse createUser(RegisterUserRequest userRequest){
        if (Boolean.TRUE.equals(userRepository.existsByUsername(userRequest.username()))) {
            throw new EntityExistsException("Username already exists");
        }
        //check if the email is unique
        if (Boolean.TRUE.equals(userRepository.existsByEmail(userRequest.email()))) {
            throw new EntityExistsException("Email already exists");
        }
        //call keycloak service to register the user
        String keycloakUserId = keycloakService.registerUserInKeycloak(userRequest.username(), userRequest.password(), userRequest.email());
        UserEntity user = UserEntity.builder()
                .id(keycloakUserId)
                .email(userRequest.email())
                .username(userRequest.username())
                .build();

        //save the user in local database
        userRepository.save(user);
        return RegisterResponse.builder().message("User successfully registered!").build();
    }

    @Transactional
    public DeleteUserResponse deleteUserByUserName(String username) {
        // Use Optional to safely handle the result
        Optional<UserEntity> user = userRepository.findByUsername(username);
        // Check if the user exists
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist");
        }
        //delete the user from the database
        userRepository.deleteById(user.get().getId());
        //delete the user from keycloak
        keycloakService.deleteUserInKeycloak(user.get().getId());

        //TODO RABBITMQ
        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_DELETE_EXCHANGE,
                RabbitMQConfig.USER_DELETE_ROUTING_KEY, user.get().getId());

        return DeleteUserResponse.builder().message("User deleted successfully").build();
    }

    @Override
    public UserResponse getProfileByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User does not exist"));
        return UserDTOConverter.toUserResponse(user);
    }

    @Transactional
    public UserUpdatedResponse updateUser(UserRequest userRequest) {
        // Fetch the user or throw if not found
        UserEntity user = userRepository.findById(userRequest.id())
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));
        // Check if the new username or email is already taken
        if (Boolean.FALSE.equals(user.getUsername().equals(userRequest.username()))
                && Boolean.TRUE.equals(userRepository.existsByUsername(userRequest.username()))) {
            throw new EntityExistsException("Username already exists");
        }
        if (Boolean.FALSE.equals(user.getEmail().equals(userRequest.email()))
                && Boolean.TRUE.equals(userRepository.existsByEmail(userRequest.email()))) {
            throw new EntityExistsException("Email already exists");
        }
        // Update the user
        user.setEmail(userRequest.email());
        user.setUsername(userRequest.username());
        user.setBio(userRequest.bio());
        userRepository.save(user);

        //call keycloak service to update the user
        keycloakService.updateUserInKeycloak(user.getId(), userRequest.username(), userRequest.email(), userRequest.password());

        // Publish user update event
        UserUpdatedEvent userUpdateEvent = UserUpdatedEvent.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.USER_UPDATE_EXCHANGE,
                    RabbitMQConfig.USER_UPDATE_ROUTING_KEY, userUpdateEvent);
        } catch (AmqpException e) {
            throw new RuntimeException("Failed to send user update event", e);
        }

        // Return success response
        return UserUpdatedResponse.builder()
                .message("User updated successfully")
                .build();
    }

    //search user
    public List<UserResponse> searchUser(String username) {
        List<UserEntity> users = userRepository.findByUsernameContainingIgnoreCase(username);
        return users.stream().map(UserDTOConverter::toUserResponse).collect(Collectors.toList());
    }
}
