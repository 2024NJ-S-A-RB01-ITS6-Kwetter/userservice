package s_a_rb01_its6.userservice.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import s_a_rb01_its6.userservice.config.RabbitMQConfig;
import s_a_rb01_its6.userservice.domain.requests.RegisterUserRequest;
import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.domain.responses.*;
import s_a_rb01_its6.userservice.events.UserUpdatedEvent;
import s_a_rb01_its6.userservice.repository.UserRepository;
import s_a_rb01_its6.userservice.repository.entities.UserEntity;
import java.util.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }


    @Test
    void createUser_HappyFlow() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest("username", "password", "email@example.com");
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(keycloakService.registerUserInKeycloak(request.username(), request.password(), request.email()))
                .thenReturn("keycloakId");
        // Act
        RegisterResponse response = userService.createUser(request);
        // Assert
        assertEquals("User successfully registered!", response.getMessage());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_UsernameExists() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest("username", "password", "email@example.com");
        when(userRepository.existsByUsername(request.username())).thenReturn(true);
        // Act
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> userService.createUser(request));
        // Assert
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(keycloakService, never()).registerUserInKeycloak(anyString(), anyString(), anyString());
    }

    @Test
    void createUser_EmailExists() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest("username", "password", "email@example.com");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        // Act
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> userService.createUser(request));
        // Assert
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(keycloakService, never()).registerUserInKeycloak(anyString(), anyString(), anyString());
    }

    @Test
    void deleteUserByUserName_HappyFlow() {
        // Arrange
        UserEntity user = UserEntity.builder().id("userId").build();
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        // Act
        DeleteUserResponse response = userService.deleteUserByUserName("username");
        // Assert
        assertEquals("User deleted successfully", response.getMessage());
        verify(userRepository).deleteById("userId");
        verify(keycloakService).deleteUserInKeycloak("userId");
        verify(rabbitTemplate).convertAndSend(
                RabbitMQConfig.USER_DELETE_EXCHANGE,
                RabbitMQConfig.USER_DELETE_ROUTING_KEY,
                "userId"
        );
    }

    @Test
    void deleteUserByUserName_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.deleteUserByUserName("username"));
        // Assert
        assertEquals("User does not exist", exception.getMessage());
        verify(userRepository, never()).deleteById(anyString());
        verify(keycloakService, never()).deleteUserInKeycloak(anyString());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
    }

    @Test
    void getProfileByUsername_HappyFlow() {
        // Arrange
        UserEntity user = UserEntity.builder().username("username").email("email@example.com").build();
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.getProfileByUsername("username");

        // Assert
        assertNotNull(response);
        assertEquals("username", response.getUsername());
        assertEquals("email@example.com", response.getEmail());
        verify(userRepository).findByUsername("username");
    }

    @Test
    void getProfileByUsername_DoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.getProfileByUsername("username"));
        // Assert
        assertEquals("User does not exist", exception.getMessage());
        verify(userRepository).findByUsername("username");
    }

    @Test
    void updateUser_HappyFlow() {
        // Arrange
        UserEntity user = UserEntity.builder().id("userId").username("oldUsername").email("oldEmail@example.com").build();
        UserRequest request = new UserRequest("userId", "newEmail@example.com", "newUsername", "newPassword", "bio");

        when(userRepository.findById(request.id())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);

        // Act
        UserUpdatedResponse response = userService.updateUser(request);

        // Assert
        assertEquals("User updated successfully", response.getMessage());
        verify(userRepository).save(user);
        verify(keycloakService).updateUserInKeycloak(
                "userId",
                "newUsername",
                "newEmail@example.com",
                "newPassword"
        );
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.USER_UPDATE_EXCHANGE), // Use eq() for raw values
                eq(RabbitMQConfig.USER_UPDATE_ROUTING_KEY), // Use eq() for raw values
                any(UserUpdatedEvent.class) // Matcher for the third argument
        );
    }

    @Test
    void updateUser_UserDoesNotExist() {
        // Arrange
        UserRequest request = new UserRequest("userId", "newEmail@example.com", "newUsername", "newPassword", "bio");

        when(userRepository.findById(request.id())).thenReturn(Optional.empty());
        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.updateUser(request));
        // Assert
        assertEquals("User does not exist", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(keycloakService, never()).updateUserInKeycloak(anyString(), anyString(), anyString(), anyString());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(UserUpdatedEvent.class));
    }

    @Test
    void updateUser_UsernameExists() {
        // Arrange
        UserEntity user = UserEntity.builder().id("userId").username("oldUsername").email("oldEmail@example.com").build();
        UserRequest request = new UserRequest("userId", "newEmail@example.com", "newUsername", "newPassword", "bio");

        when(userRepository.findById(request.id())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(request.username())).thenReturn(true);
        // Act
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> userService.updateUser(request));
        // Assert
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(keycloakService, never()).updateUserInKeycloak(anyString(), anyString(), anyString(), anyString());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(UserUpdatedEvent.class));
    }

    @Test
    void updateUser_EmailExists() {
        // Arrange
        UserEntity user = UserEntity.builder().id("userId").username("oldUsername").email("oldEmail@example.com").build();
        UserRequest request = new UserRequest("userId", "newEmail@example.com", "newUsername", "newPassword", "bio");

        when(userRepository.findById(request.id())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        // Act
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> userService.updateUser(request));
        // Assert
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(keycloakService, never()).updateUserInKeycloak(anyString(), anyString(), anyString(), anyString());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(UserUpdatedEvent.class));
    }
    @Test
    void updateUser_nullUserName() {
        // Arrange
        UserEntity user = UserEntity.builder().id("userId").username("oldUsername").email("oldEmail@example.com").build();
        UserRequest request = new UserRequest("userId", "newEmail@example.com", null, "newPassword", "bio");

        when(userRepository.findById(request.id())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        // Act
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> userService.updateUser(request));
        // Assert
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(keycloakService, never()).updateUserInKeycloak(anyString(), anyString(), anyString(), anyString());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(UserUpdatedEvent.class));
    }
}