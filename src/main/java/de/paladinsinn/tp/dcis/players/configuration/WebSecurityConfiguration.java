package de.paladinsinn.tp.dcis.players.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import de.paladinsinn.tp.dcis.players.configuration.security.JwtConverter;
import de.paladinsinn.tp.dcis.players.configuration.security.KeycloakLogoutHandler;
import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity(debug = true)
@Order(2)
@RequiredArgsConstructor
public class WebSecurityConfiguration {
    @Value("${spring.security.oauth2.resourceserver.jwt.issuerUri}")
    private String jwtIssuerUri;

    @Value("${server.servlet.contextPath}")
//    @Value("${spring.mvc.servlet.path}")
    private String contextPath;

    private final KeycloakLogoutHandler keycloakLogoutHandler;
    private final JwtConverter jwtConverter;

    

    @Bean
	public SecurityFilterChain userSecurityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector, AuthenticationSuccessHandler successHandler) throws Exception {
        MvcRequestMatcher.Builder matcher = new MvcRequestMatcher.Builder(introspector);

		http
            .securityMatchers(m -> m
                .requestMatchers(matcher.pattern("/dcis/**"), matcher.pattern("/oauth2/**"), matcher.pattern("/login/**"), matcher.pattern("/logout/**"))
            )
            .authorizeHttpRequests((authorize) -> authorize
                    .anyRequest().authenticated()
            );
        http
            .oauth2Client(Customizer.withDefaults())
            .cors(Customizer.withDefaults())
            .csrf(Customizer.withDefaults())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .rememberMe(Customizer.withDefaults())
            .oauth2ResourceServer(s -> s.jwt(Customizer.withDefaults()));
        http
            .logout(l -> l
                .addLogoutHandler(keycloakLogoutHandler).logoutSuccessUrl("/players/")
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
            );
        http
            .oauth2Login(l -> l.successHandler(successHandler))
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
}
