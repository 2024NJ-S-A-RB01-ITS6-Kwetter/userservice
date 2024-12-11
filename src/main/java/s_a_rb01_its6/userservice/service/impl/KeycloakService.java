package s_a_rb01_its6.userservice.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;

@Service
public class KeycloakService {

    //TODO: MAKE THESE COME FROM @VALUE
    private final String serverUrl = "http://keycloak:8080/";
    private final String realm = "kwetter";  // The realm to authenticate in
    private final String clientId = "admin-cli";  // Use 'admin-cli' for administrative tasks
    //private final String clientSecret = "";       // Optional if you're using 'admin-cli'
    private final String adminUsername = "admin"; // Admin username
    private final String adminPassword = "admin"; // Admin password

    // Use the builder to obtain the Keycloak admin token
    private Keycloak keycloak;

    @PostConstruct
    public void KeycloakAdminService() {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")  // Authenticate in the 'master' realm
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

    public void registerUserInKeycloak(String username, String password) {
        // Create the user representation
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEnabled(true);
        user.setCredentials(Collections.singletonList(createPasswordCredentials(password)));
        user.setRealmRoles(Collections.singletonList("user")); // Assign roles to the user

        // Obtain the Keycloak 'users' resource for the specified realm
        UsersResource usersResource = keycloak.realm(realm).users();

        // Create the user in Keycloak
        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatus());
        }

        // Optionally, you can retrieve the user’s Keycloak ID here for further use
    }

    //delete user in keycloak
    public void deleteUserInKeycloak(String username) {
        UsersResource usersResource = keycloak.realm(realm).users();
        List<UserRepresentation> users = usersResource.search(username);
        if (users == null || users.isEmpty()) {
            throw new RuntimeException("User not found in Keycloak: " + username);
        }
        String userId = users.get(0).getId();
        UserResource userResource = usersResource.get(userId);
        userResource.remove();
    }

    public void updateUserInKeycloak(String currentUsername, String newUsername, String newEmail) {
        UsersResource usersResource = keycloak.realm(realm).users();

        List<UserRepresentation> users = usersResource.search(currentUsername);
        if (users == null || users.isEmpty()) {
            throw new RuntimeException("User not found in Keycloak: " + currentUsername);
        }

        UserRepresentation userRepresentation = users.get(0);  // Fetch the user representation
        userRepresentation.setUsername(newUsername);  // Update username
        userRepresentation.setEmail(newEmail);        // Update email

        // Validate new username and email
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("New username cannot be null or empty.");
        }
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("New email cannot be null or empty.");
        }

        UserResource userResource = usersResource.get(userRepresentation.getId());
        try {
            userResource.update(userRepresentation);  // Send the updated data to Keycloak
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user in Keycloak: " + e.getMessage());
        }
    }


    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);  // Password is not temporary
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        return passwordCred;
    }
}
