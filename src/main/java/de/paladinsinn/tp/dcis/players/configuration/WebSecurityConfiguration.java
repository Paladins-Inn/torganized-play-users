package de.paladinsinn.tp.dcis.players.configuration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


import de.paladinsinn.tp.dcis.players.services.KeycloakLogoutHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfiguration {
    private static final String GROUPS = "groups";
    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";

    private final KeycloakLogoutHandler logoutHandler;
    
    @Bean
    @Order(1)
    public SecurityFilterChain springSecurity(HttpSecurity http) throws Exception {
        http
            .securityMatcher(new FreeMatcher())
            .authorizeHttpRequests(r -> r
                .requestMatchers("/oauth2/**", "/login**", "/error**")
                .permitAll());

        return http.build();
    }

    private static class FreeMatcher implements RequestMatcher {
        @Override
        public boolean matches(HttpServletRequest request) {
            return request.getLocalName().startsWith("/error") 
                    || request.getLocalName().startsWith("/login")
                    || request.getLocalAddr().startsWith("/oauth2/**");
        }
    }


    @Bean
    @Order(2)
    public SecurityFilterChain observabilitySecurity(HttpSecurity http) throws Exception {
        http
            .securityMatcher(new AntPathRequestMatcher("/actuator/**"))
            .authorizeHttpRequests(r -> r
            .requestMatchers(new BasicRequestMatcher()).hasRole("OBSERVER")
            .requestMatchers("/actuator/**").authenticated()
        ).httpBasic(Customizer.withDefaults());
 
        return http.build();
    }

    private static class BasicRequestMatcher implements RequestMatcher {
        @Override
        public boolean matches(HttpServletRequest request) {
            String auth = request.getHeader("Authorization");
            return (auth != null && auth.toLowerCase().startsWith("basic") && request.getLocalName().startsWith("/actuator"));
        }
    }

        
    @Bean
    @Order(3)
    public SecurityFilterChain playerSecurity(HttpSecurity http) throws Exception {
        http
            .securityMatcher(new AntPathRequestMatcher("/players/**"))
            .authorizeHttpRequests(r -> r
                .requestMatchers("/players/api/**").hasAuthority("API")
                .requestMatchers("/players/dcis/**").hasAnyAuthority("ADMIN", "ORGA", "JUDGE", "GM", "PLAYER")
                .requestMatchers("/players/**").permitAll()
                .anyRequest().authenticated()
            );

        http
            .oauth2ResourceServer(a -> a.jwt(Customizer.withDefaults()))
            .oauth2Login(Customizer.withDefaults())
            .logout(l -> l.addLogoutHandler(logoutHandler).logoutSuccessUrl("/players/"));
 
        return http.build();
    }

    @SuppressWarnings("unchecked")
    @Bean
    public GrantedAuthoritiesMapper keycloakAuthorityMapper() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            var authority = authorities.iterator().next();
            boolean isOidc = authority instanceof OidcUserAuthority;

            if (isOidc) {
                var oidcUserAuthority = (OidcUserAuthority) authority;
                var userInfo = oidcUserAuthority.getUserInfo();

                if (userInfo.hasClaim(REALM_ACCESS_CLAIM)) {
                    var realmAccess = userInfo.getClaimAsMap(REALM_ACCESS_CLAIM);
                    var roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                } else if (userInfo.hasClaim(GROUPS)) {
                    Collection<String> roles = (Collection<String>) userInfo.getClaim(GROUPS);
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                }
            } else {
                var oauth2UserAuthority = (OAuth2UserAuthority) authority;
                Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();

                if (userAttributes.containsKey(REALM_ACCESS_CLAIM)) {
                    Map<String, Object> realmAccess = (Map<String, Object>) userAttributes.get(REALM_ACCESS_CLAIM);
                    Collection<String> roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                }
            }

            log.info("Berechtigungen berechnet. user={}, sub={}, roles={}", "./.", "./.", mappedAuthorities);
            return mappedAuthorities;
        };
    }

    private Collection<GrantedAuthority> generateAuthoritiesFromClaim(Collection<String> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
    }
}
