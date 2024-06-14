package de.paladinsinn.tp.dcis.players.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class WebSecurityConfiguration {
    @Value("${spring.security.oauth2.resourceserver.jwt.issuerUri}")
    private String jwtIssuerUri;

    @Value("${spring.mvc.servlet.path}")
    private String contextPath;

    @Bean
	public SecurityFilterChain userSecurityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder matcher = new MvcRequestMatcher.Builder(introspector).servletPath(contextPath);
		http
            .securityMatcher(matcher.pattern("/dcis/**"))
            .authorizeHttpRequests((authorize) -> authorize
                    .anyRequest().hasAnyAuthority("ADMIN", "ORGA", "JUDGE", "GM", "PLAYER")
                )
                .oauth2Login(l -> l.loginPage("/players/oauth2/authorization/delphi-council"))
                .oauth2Client(Customizer.withDefaults())
                .oauth2ResourceServer((oauth2) -> oauth2
                    .jwt(Customizer.withDefaults())
                );

        return http.build();
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		return JwtDecoders.fromIssuerLocation(jwtIssuerUri);
	}
}
