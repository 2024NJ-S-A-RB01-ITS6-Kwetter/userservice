package s_a_rb01_its6.userservice.service;

import s_a_rb01_its6.userservice.domain.requests.RegisterUserRequest;
import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.domain.responses.RegisterResponse;
import s_a_rb01_its6.userservice.domain.responses.UserResponse;

public interface UserService {

    RegisterResponse createUser(RegisterUserRequest registerUserRequest);

    //TODO add methods to get a profile

    //TODO change methods to use not return void but return a response object
    void updateUser(UserRequest userRequest);

    void deleteUserById(Long id);

    UserResponse getUserById(Long id);
}
