package de.paladinsinn.tp.dcis.players.configuration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import de.paladinsinn.tp.dcis.players.configuration.security.KeycloakLogoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Configuration
@EnableWebSecurity(debug = false)
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfiguration {
    @Value("${spring.security.oauth2.resourceserver.jwt.issuerUri}")
    private String jwtIssuerUri;

    @Value("${server.servlet.contextPath}")
//    @Value("${spring.mvc.servlet.path}")
    private String contextPath;

    @Bean
	public SecurityFilterChain userSecurityFilterChain(
        HttpSecurity http, 
        HandlerMappingIntrospector introspector, 
        AuthenticationSuccessHandler successHandler,
        GrantedAuthoritiesMapper authoritiesMapper,
        KeycloakLogoutHandler keycloakLogoutHandler
        ) throws Exception {
        MvcRequestMatcher.Builder matcher = new MvcRequestMatcher.Builder(introspector);

		http
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(matcher.pattern("/dcis/**")).authenticated()
                    .requestMatchers(matcher.pattern("/oauth2/**")).authenticated()
                    .requestMatchers(matcher.pattern("/login/**")).authenticated()
                    .requestMatchers(matcher.pattern("/logout/**")).authenticated()
                    .anyRequest().permitAll()
            )
            .oauth2Client(Customizer.withDefaults())
            .oauth2Login(l -> l
                .userInfoEndpoint(u -> u
                    .userAuthoritiesMapper(authoritiesMapper)
                )
                .successHandler(successHandler)
            )
            .logout(l -> l
                .addLogoutHandler(keycloakLogoutHandler)
                .logoutSuccessUrl("/players/")
            )
            .cors(Customizer.withDefaults())
            .csrf(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .rememberMe(Customizer.withDefaults())
            ;

        return http.build();
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		return JwtDecoders.fromOidcIssuerLocation(jwtIssuerUri);
	}

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/dcis/");
        return handler;
    }


    static class KeycloakGroupAuthorityMapper implements GrantedAuthoritiesMapper {

        @Override
        public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
            Set<GrantedAuthority> result = new HashSet<>();

            log.debug("Authority Mapper called. authorities={}", authorities);

            authorities.forEach(authority -> {
                log.debug("Authority mapper working. authority={}", authority);

                if (authority instanceof OidcUserAuthority) {
                    OidcUserAuthority oidc = (OidcUserAuthority) authority;

                    log.debug("Reading roles. oidc={}, groups={}", oidc, oidc.getUserInfo().getClaimAsString("groups"));

                    result.addAll(oidc.getUserInfo().getClaimAsStringList("groups")
                        .stream()
                        .map(r -> { return "ROLE_" + r.toUpperCase(); })
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet())
                    );
                }
            });

            log.trace("Roles mapped. roles={}", result);
            return result;
        }

    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return new KeycloakGroupAuthorityMapper();
    }

    @Bean
    public JwtAuthenticationConverter jwtConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("groups");
        converter.setAuthorityPrefix("ROLE_");
        
        JwtAuthenticationConverter result = new JwtAuthenticationConverter();
        result.setJwtGrantedAuthoritiesConverter(converter);

        return result;
    }
}
