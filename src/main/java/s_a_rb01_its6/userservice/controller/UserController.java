package s_a_rb01_its6.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import s_a_rb01_its6.userservice.domain.UserDTO;
import s_a_rb01_its6.userservice.domain.requests.RegisterUserRequest;
import s_a_rb01_its6.userservice.domain.requests.SearchUserRequest;
import s_a_rb01_its6.userservice.domain.requests.UpdateUserRequest;
import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.domain.responses.DeleteUserResponse;
import s_a_rb01_its6.userservice.domain.responses.RegisterResponse;
import s_a_rb01_its6.userservice.domain.responses.UserResponse;
import s_a_rb01_its6.userservice.domain.responses.UserUpdatedResponse;
import s_a_rb01_its6.userservice.service.impl.UserServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static s_a_rb01_its6.userservice.config.Constant.API_URL;

@RestController
@RequestMapping(value =API_URL + "/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    public static final String CURRENT_PAGE = "currentPage";
    public static final String TOTAL_PAGES = "totalPages";
    public static final String PAGE_SIZE = "pageSize";
    public static final String TOTAL_ELEMENTS = "totalElements";
    public static final String USERS = "users";

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
    @PreAuthorize("hasRole('ADMIN') or #updateUserRequest.id.equals(authentication.principal.claims['sub'])")
    public CompletableFuture<ResponseEntity<UserUpdatedResponse>> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return CompletableFuture.supplyAsync(() -> {
            UserUpdatedResponse userUpdatedResponse = userService.updateUser(updateUserRequest);
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

    // Search posts (async)
    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> searchPost(
            @RequestParam String query,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return CompletableFuture.supplyAsync(() -> {
            SearchUserRequest searchUserRequest = new SearchUserRequest(page, size, query);
            Page<UserDTO> users = userService.searchUsers(searchUserRequest);
            Map<String, Object> response = new HashMap<>();
            response.put(CURRENT_PAGE, users.getNumber());
            response.put(TOTAL_PAGES, users.getTotalPages());
            response.put(PAGE_SIZE, users.getSize());
            response.put(TOTAL_ELEMENTS, users.getTotalElements());
            response.put(USERS, users.getContent());

            return ResponseEntity.ok(response);
        });
    }

}
