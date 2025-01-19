package s_a_rb01_its6.userservice.service;

import org.springframework.data.domain.Page;
import s_a_rb01_its6.userservice.domain.UserDTO;
import s_a_rb01_its6.userservice.domain.requests.RegisterUserRequest;
import s_a_rb01_its6.userservice.domain.requests.SearchUserRequest;
import s_a_rb01_its6.userservice.domain.requests.UpdateUserRequest;
import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.domain.responses.DeleteUserResponse;
import s_a_rb01_its6.userservice.domain.responses.RegisterResponse;
import s_a_rb01_its6.userservice.domain.responses.UserResponse;
import s_a_rb01_its6.userservice.domain.responses.UserUpdatedResponse;

import java.util.List;

public interface UserService {

    RegisterResponse createUser(RegisterUserRequest registerUserRequest);

    UserUpdatedResponse updateUser(UpdateUserRequest userRequest);

    DeleteUserResponse deleteUserByUserName(String username);

    UserResponse getProfileByUsername(String username);

    Page<UserDTO> searchUsers(SearchUserRequest searchPostRequest);
}
