package com.opcon.bff.security;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<String> roles = new LinkedHashSet<>();
        roles.addAll(extractRealmRoles(jwt));
        roles.addAll(extractScopeRoles(jwt));

        return roles.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .map(String::toUpperCase)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRealmRoles(Jwt jwt) {
        Object claim = jwt.getClaims().get("realm_access");
        if (!(claim instanceof Map<?, ?> realmAccess)) {
            return List.of();
        }

        Object roles = realmAccess.get("roles");
        if (roles instanceof Collection<?> collection) {
            return collection.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }

        return List.of();
    }

    private List<String> extractScopeRoles(Jwt jwt) {
        String scope = jwt.getClaimAsString("scope");
        if (scope == null || scope.isBlank()) {
            return List.of();
        }

        return List.of(scope.split(" "));
    }
}
