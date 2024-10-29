package s_a_rb01_its6.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.service.UserService;
import s_a_rb01_its6.userservice.service.impl.UserServiceImpl;

import static s_a_rb01_its6.userservice.config.Constant.API_URL;

@RestController
@RequestMapping(value =API_URL + "/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.createUser(userRequest).getMessage());
    }

    //update user
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserRequest userRequest) {
        userService.updateUser(userRequest);
        return ResponseEntity.ok("User updated successfully");
    }

    //delete user
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    //get profile

    //search user



}
