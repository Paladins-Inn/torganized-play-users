package de.kaiserpfalzedv.commons.spring.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakGroupAuthorityMapper implements GrantedAuthoritiesMapper {

    private final JwtConverterProperties jwtProperties;

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> result = new HashSet<>();

        authorities.forEach(authority -> {
            if (authority instanceof OidcUserAuthority) {
                OidcUserAuthority oidc = (OidcUserAuthority) authority;

                log.trace("Reading roles. oidc={}, groups={}", oidc, oidc.getUserInfo().getClaimAsString(jwtProperties.getRoleAttribute()));

                result.addAll(oidc.getUserInfo().getClaimAsStringList(jwtProperties.getRoleAttribute())
                    .stream()
                    .map(r -> { return "ROLE_" + r.toUpperCase(); })
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet())
                );
            }
        });

        log.debug("Roles mapped. roles={}", result);
        return result;
    }

}