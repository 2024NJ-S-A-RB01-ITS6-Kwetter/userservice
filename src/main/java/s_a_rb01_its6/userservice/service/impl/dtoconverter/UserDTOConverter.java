package s_a_rb01_its6.userservice.service.impl.dtoconverter;

import s_a_rb01_its6.userservice.domain.User;
import s_a_rb01_its6.userservice.domain.responses.UserResponse;
import s_a_rb01_its6.userservice.repository.entities.UserDTO;

public final class UserDTOConverter {

    public static UserResponse toUserResponse(UserDTO user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .bio(user.getBio())
                .build();
    }

    public static User toUser(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .username(userDTO.getUsername())
                .bio(userDTO.getBio())
                .build();
    }
}
