package s_a_rb01_its6.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import s_a_rb01_its6.userservice.domain.requests.RegisterUserRequest;
import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.domain.responses.DeleteUserResponse;
import s_a_rb01_its6.userservice.domain.responses.RegisterResponse;
import s_a_rb01_its6.userservice.domain.responses.UserResponse;
import s_a_rb01_its6.userservice.domain.responses.UserUpdatedResponse;
import s_a_rb01_its6.userservice.service.impl.UserServiceImpl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static s_a_rb01_its6.userservice.config.Constant.API_URL;

@RestController
@RequestMapping(value =API_URL + "/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    // Register user (async)
    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<RegisterResponse>> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        return CompletableFuture.supplyAsync(() -> {
            RegisterResponse registerResponse = userService.createUser(registerUserRequest);
            return ResponseEntity.ok(registerResponse);
        });
    }

    // Update user (async)
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or #userRequest.id.equals(authentication.principal.claims['sub'])")
    public CompletableFuture<ResponseEntity<UserUpdatedResponse>> updateUser(@Valid @RequestBody UserRequest userRequest) {
        return CompletableFuture.supplyAsync(() -> {
            UserUpdatedResponse userUpdatedResponse = userService.updateUser(userRequest);
            return ResponseEntity.ok(userUpdatedResponse);
        });
    }

    // Delete user (async)
    @PreAuthorize("hasRole('ADMIN') or #username.equals(authentication.name)")
    @DeleteMapping("/delete/{username}")
    public CompletableFuture<ResponseEntity<DeleteUserResponse>> deleteUser(@PathVariable String username) {
        return CompletableFuture.supplyAsync(() -> {
            DeleteUserResponse deleteUserResponse = userService.deleteUserByUserName(username);
            return ResponseEntity.ok(deleteUserResponse);
        });
    }

    // Get profile (async)
    @GetMapping("/profile/{username}")
    public CompletableFuture<ResponseEntity<UserResponse>> getProfile(@PathVariable String username) {
        return CompletableFuture.supplyAsync(() -> {
            UserResponse userResponse = userService.getProfileByUsername(username);
            return ResponseEntity.ok(userResponse);
        });
    }

    // Search user (async)
    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<List<UserResponse>>> searchUser(@RequestParam String username) {
        return CompletableFuture.supplyAsync(() -> {
            List<UserResponse> userResponses = userService.searchUser(username);
            return ResponseEntity.ok(userResponses);
        });
    }

}
