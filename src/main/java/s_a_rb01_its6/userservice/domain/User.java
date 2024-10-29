package s_a_rb01_its6.userservice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@lombok.AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String username;
    private String bio;
}
