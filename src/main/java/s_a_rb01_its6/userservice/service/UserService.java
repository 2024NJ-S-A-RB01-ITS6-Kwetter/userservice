package s_a_rb01_its6.userservice.service;

import s_a_rb01_its6.userservice.domain.requests.RegisterUserRequest;
import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.domain.responses.DeleteUserResponse;
import s_a_rb01_its6.userservice.domain.responses.RegisterResponse;
import s_a_rb01_its6.userservice.domain.responses.UserResponse;
import s_a_rb01_its6.userservice.domain.responses.UserUpdatedResponse;

import java.util.List;

public interface UserService {

    RegisterResponse createUser(RegisterUserRequest registerUserRequest);

    UserUpdatedResponse updateUser(UserRequest userRequest);

    DeleteUserResponse deleteUserByUserName(String username);

    UserResponse getProfileByUsername(String username);

    List<UserResponse> searchUser(String username);
}
