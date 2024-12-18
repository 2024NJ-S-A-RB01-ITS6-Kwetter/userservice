package s_a_rb01_its6.userservice.repository.entities;



import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class UserEntity {
    @Id
    private String id;
    @Column(unique = true)
    private String email;
    private String username;
    private String bio;
}
