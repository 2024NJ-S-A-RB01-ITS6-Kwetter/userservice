package s_a_rb01_its6.userservice.service.impl;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import s_a_rb01_its6.userservice.domain.requests.UserRequest;
import s_a_rb01_its6.userservice.domain.responses.RegisterResponse;
import s_a_rb01_its6.userservice.repository.UserRepository;
import s_a_rb01_its6.userservice.repository.entities.UserDTO;
import s_a_rb01_its6.userservice.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public RegisterResponse createUser(UserRequest userRequest){

        //Check if all of the unique parameters are unique
        if (Boolean.TRUE.equals(userRepository.existsByUsername(userRequest.username()))) {
            throw new EntityExistsException("Username already exists");
        }
        //TODO implement other unique parameter checks

        //TODO other checks regarding user such as password length, email format etc.

        //

        UserDTO user = UserDTO.builder()
                .email(userRequest.email())
                .username(userRequest.username())
                .bio(userRequest.bio())
                .build();



        //TODO call the keycloak service to register the user
        //TODO then save the user in local database
        userRepository.save(user);
        //TODO then generate a rabbitmq message to notify other services that a new user is created

        return RegisterResponse.builder().message("User successfully registered!").build();
    }


    public void deleteUserById(Long id) {

        //TODO check if user is authorized to delete user!!!


        if (userRepository.findById(id).isEmpty()) {
            throw new EntityExistsException("User does not exist");
        }
        //TODO RABBITMQ
        //TODO KEYCLOAK
        userRepository.deleteById(id);
    }


    // username cant be changed because it is used as a unique identifier
    @Transactional
    public void updateUser(UserRequest userRequest) {

        //TODO check if the user updating is the same user as being updated or if they have admin rights.


        userRepository.existsByUsername(userRequest.username());
        if (Boolean.FALSE.equals(userRepository.existsByUsername(userRequest.username()))) {
            throw new EntityExistsException("User does not exist");
        }

        //TODO other checks regarding user such as password length, email format etc.

        UserDTO user = UserDTO.builder()
                .id(userRequest.id())
                .email(userRequest.email())
                .username(userRequest.username())
                .bio(userRequest.bio())
                .build();

        userRepository.save(user);
        //TODO RABBITMQ
    }


    //TODO Create a validator for the userRequest object
}
