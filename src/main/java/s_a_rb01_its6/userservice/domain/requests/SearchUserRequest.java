package s_a_rb01_its6.userservice.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchUserRequest {
    @NotBlank
    private int page;
    @NotBlank
    private int size;
    @NotBlank
    private String query;
}
