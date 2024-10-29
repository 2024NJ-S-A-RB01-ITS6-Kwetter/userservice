package s_a_rb01_its6.userservice.service;

import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.domain.responses.RegisterResponse;
import s_a_rb01_its6.userservice.domain.responses.UserResponse;

import java.util.List;

public interface UserService {

    RegisterResponse createUser(UserRequest userRequest);

    void updateUser(UserRequest userRequest);

    void deleteUserById(Long id);

}
