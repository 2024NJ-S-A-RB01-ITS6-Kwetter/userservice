package s_a_rb01_its6.userservice.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import s_a_rb01_its6.userservice.domain.requests.RegisterUserRequest;
import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.domain.responses.RegisterResponse;
import s_a_rb01_its6.userservice.domain.responses.UserResponse;
import s_a_rb01_its6.userservice.service.UserService;
import s_a_rb01_its6.userservice.service.impl.UserServiceImpl;

import static s_a_rb01_its6.userservice.config.Constant.API_URL;

@RestController
@RequestMapping(value =API_URL + "/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        return ResponseEntity.ok(userService.createUser(registerUserRequest));
    }

    //update user
    //TODO change resposeEntity to UpdateUserResponse and request to UpdateUserRequest
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserRequest userRequest) {
        userService.updateUser(userRequest);
        return ResponseEntity.ok("User updated successfully");
    }

    //delete user
    //TODO MAKE SURE IT IS ADMIN OR THE USER SELF DELETING
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.claims['sub']")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    //get profile
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserResponse> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //search user



}
