package de.paladinsinn.tp.dcis.players.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.jayway.jsonpath.JsonPath;

import de.paladinsinn.tp.dcis.players.configuration.security.JwtGrantedAuthoritiesConverter;
import de.paladinsinn.tp.dcis.players.configuration.security.KeycloakLogoutHandler;
import groovy.transform.ToString;
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
        JwtConverter jwtConverter,
        KeycloakLogoutHandler keycloakLogoutHandler
        ) throws Exception {
        MvcRequestMatcher.Builder matcher = new MvcRequestMatcher.Builder(introspector);

        log.info("Security filter chain created. jwtConverter={}", jwtConverter);

		http
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(matcher.pattern("/dcis/**")).authenticated()
                    .requestMatchers(matcher.pattern("/oauth2/**")).authenticated()
                    .requestMatchers(matcher.pattern("/login/**")).authenticated()
                    .requestMatchers(matcher.pattern("/logout/**")).authenticated()
                    .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(j -> j.jwtAuthenticationConverter(jwtConverter)))
            .oauth2Client(Customizer.withDefaults())
            .oauth2Login(l -> l.successHandler(successHandler))
            .logout(l -> l
                .addLogoutHandler(keycloakLogoutHandler).logoutSuccessUrl("/players/")
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
		return JwtDecoders.fromIssuerLocation(jwtIssuerUri);
	}

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/dcis/");
        return handler;
    }

    @Bean
    public JwtConverter jwtConverter(JwtGrantedAuthoritiesConverter authoritiesConverter) {
        return new JwtConverter(authoritiesConverter);
    }

    @ToString(includeNames = true)
    static class JwtConverter implements Converter<Jwt, JwtAuthenticationToken> {
        private final JwtGrantedAuthoritiesConverter authoritiesConverter;

        public JwtConverter(final JwtGrantedAuthoritiesConverter authoritiesConverter) {
            this.authoritiesConverter = authoritiesConverter;

            log.info("JwtConverter created. authoritiesConverter={}", authoritiesConverter);
        }

        @Override
        public JwtAuthenticationToken convert(Jwt jwt) {
            log.info("JWT received: jwt={}", jwt.getClaims());

            final var authorities = authoritiesConverter.convert(jwt);
            log.debug("authorities={}", authorities);

            final String username = JsonPath.read(jwt.getClaims(), "preferred_username");
            return new JwtAuthenticationToken(jwt, authorities, username);
        }
    }
}
