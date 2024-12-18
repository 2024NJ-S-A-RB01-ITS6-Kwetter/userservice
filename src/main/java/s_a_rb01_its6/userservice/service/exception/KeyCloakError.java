package s_a_rb01_its6.userservice.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class KeyCloakError extends RuntimeException {
    public KeyCloakError(String message) {
        super(message);
    }
}