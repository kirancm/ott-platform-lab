package com.opcon.bff.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class KeycloakRealmRoleConverterTest {

    private final KeycloakRealmRoleConverter converter = new KeycloakRealmRoleConverter();

    @Test
    void shouldExtractRealmRolesFromJwt() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("realm_access", Map.of("roles", java.util.List.of("admin", "operator")))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ADMIN", "ROLE_OPERATOR");
    }
}
