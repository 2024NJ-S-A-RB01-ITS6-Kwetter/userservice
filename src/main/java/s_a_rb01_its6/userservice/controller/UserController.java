package s_a_rb01_its6.userservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

import static s_a_rb01_its6.userservice.config.Constant.API_URL;

@RestController
@RequestMapping(value =API_URL + "/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;



    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        RegisterResponse registerResponse = userService.createUser(registerUserRequest);
        return ResponseEntity.ok(registerResponse);
    }

    //update user
    @PutMapping("/update")
// Check if the user updating is the same user as being updated or if they have admin rights
    @PreAuthorize("hasRole('ADMIN') or #userRequest.id.equals(authentication.principal.claims['sub'])")
    public ResponseEntity<UserUpdatedResponse> updateUser(@Valid @RequestBody UserRequest userRequest) {
        UserUpdatedResponse userUpdatedResponse = userService.updateUser (userRequest);
        return ResponseEntity.ok(userUpdatedResponse);
    }

    //delete user
    @PreAuthorize("hasRole('ADMIN') or #username.equals(authentication.name)")
    @DeleteMapping("/delete/{username}")
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable String username) {
        DeleteUserResponse deleteUserResponse = userService.deleteUserByUserName(username);
        return ResponseEntity.ok(deleteUserResponse);
    }

    //get profile
    @GetMapping("/profile/{username}")
    public ResponseEntity<UserResponse> getProfile(@PathVariable String username) {
        UserResponse userResponse = userService.getProfileByUsername(username);
        return ResponseEntity.ok(userResponse);
    }

    //search user
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUser(@RequestParam String username) {
        List<UserResponse> userResponses = userService.searchUser(username);
        return ResponseEntity.ok(userResponses);
    }

}
