package s_a_rb01_its6.userservice.config;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.LOWEST_PRECEDENCE -1)
public class CustomJwtAuthenticationConverter extends JwtAuthenticationConverter {

    public CustomJwtAuthenticationConverter() {
        setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
    }

    //TODO test if this works lol
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Extract "roles" claim and map them to GrantedAuthority
        Collection<GrantedAuthority> authorities = jwt.getClaimAsStringList("roles")
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Add additional authorities if needed
        return authorities;
    }
}