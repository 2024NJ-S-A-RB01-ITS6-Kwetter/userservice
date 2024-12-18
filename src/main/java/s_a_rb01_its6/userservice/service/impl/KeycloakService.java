package s_a_rb01_its6.userservice.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import s_a_rb01_its6.userservice.service.exception.KeyCloakError;


import java.util.Collections;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

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

    public String registerUserInKeycloak(String username, String password , String email) {
        // Create the user representation
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);
        user.setCredentials(Collections.singletonList(createPasswordCredentials(password)));
        user.setRealmRoles(Collections.singletonList("user")); // Assign roles to the user

        // Obtain the Keycloak 'users' resource for the specified realm
        UsersResource usersResource = keycloak.realm(realm).users();

        // Create the user in Keycloak
        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            throw new KeyCloakError("Failed to create user in Keycloak: " + response);
        }
        // Extract the Keycloak user ID from the response's Location header
        String location = response.getHeaderString("Location"); // e.g., "/realms/{realm}/users/{id}"
        if (location == null) {
            throw new KeyCloakError("Failed to retrieve user ID from Keycloak response");
        }
        // Extract the user ID (UUID) from the location header
        return location.substring(location.lastIndexOf('/') + 1); // Return the Keycloak-generated user ID (UUID)
    }

    //delete user in keycloak
    public void deleteUserInKeycloak(String userId) {
        UsersResource usersResource = keycloak.realm(realm).users();
        UserResource userResource = usersResource.get(userId);
        userResource.remove();
    }

    public void updateUserInKeycloak(String userId, String newUsername, String newEmail, String password) {
        // Validate new username and email before making Keycloak requests
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("New username cannot be null or empty.");
        }
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("New email cannot be null or empty.");
        }

        // Get the UsersResource for the realm
        UsersResource usersResource = keycloak.realm(realm).users();

        try {
            // Fetch the current UserRepresentation from Keycloak
            UserResource userResource = usersResource.get(userId);
            UserRepresentation userRepresentation = userResource.toRepresentation();

            // Update fields
            userRepresentation.setUsername(newUsername);
            userRepresentation.setEmail(newEmail);
            userRepresentation.setCredentials(Collections.singletonList(createPasswordCredentials(password)));
            userRepresentation.setEnabled(true); // Ensure the user is enabled
            userResource.update(userRepresentation);
            //log the user out of all sessions
            keycloak.realm(realm).users().get(userId).logout();
        } catch (Exception e) {
            throw new KeyCloakError("Failed to update user in Keycloak: " + e.getMessage());
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
