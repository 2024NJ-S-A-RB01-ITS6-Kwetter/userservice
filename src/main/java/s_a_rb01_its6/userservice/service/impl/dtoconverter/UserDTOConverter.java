package s_a_rb01_its6.userservice.service.impl.dtoconverter;

import s_a_rb01_its6.userservice.domain.UserDTO;
import s_a_rb01_its6.userservice.domain.responses.UserResponse;
import s_a_rb01_its6.userservice.repository.entities.UserEntity;

public final class UserDTOConverter {

    public static UserResponse toUserResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .bio(user.getBio())
                .build();
    }

//    public static UserDTO toUser(UserEntity userEntity) {
//        return UserDTO.builder()
//                .id(userEntity.getId())
//                .email(userEntity.getEmail())
//                .username(userEntity.getUsername())
//                .bio(userEntity.getBio())
//                .build();
//    }
}
