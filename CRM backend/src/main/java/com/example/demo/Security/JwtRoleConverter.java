package com.example.demo.Security;

import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Component
public class JwtRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String ROLE_CLAIM = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(@Nonnull Jwt jwt) {

        // Extract standard OAuth2 scopes.
        Collection<GrantedAuthority> scopeAuthorities =
                defaultGrantedAuthoritiesConverter.convert(jwt);

        // Extract application-specific roles.
        List<String> roles = jwt.getClaimAsStringList(ROLE_CLAIM);

        Collection<GrantedAuthority> roleAuthorities =
                roles == null
                        ? Collections.emptyList()
                        : roles.stream()
                        .filter(role -> role != null && !role.isBlank())
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .map(role ->
                                (GrantedAuthority) new SimpleGrantedAuthority(
                                        ROLE_PREFIX + role
                                )
                        )
                        .toList();

        // Combine scopes and roles.
        Collection<GrantedAuthority> authorities =
                Stream.concat(
                        scopeAuthorities.stream(),
                        roleAuthorities.stream()
                ).distinct().toList();

        return new JwtAuthenticationToken(jwt, authorities);
    }
}