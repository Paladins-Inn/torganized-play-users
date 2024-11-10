/*
 * Copyright (c) 2024. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.users.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import de.kaiserpfalzedv.commons.spring.security.EnableKeycloakSecurityIntegration;
import de.kaiserpfalzedv.commons.spring.security.KeycloakGroupAuthorityMapper;
import de.kaiserpfalzedv.commons.spring.security.KeycloakLogoutHandler;
import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity()
@EnableKeycloakSecurityIntegration
@Order(2)
@RequiredArgsConstructor
public class WebSecurityConfiguration {
    @Value("${spring.security.oauth2.client.provider.sso.issuer-uri}")
    private String issuerUri;


    @Bean
	public SecurityFilterChain userSecurityFilterChain(
        HttpSecurity http, 
        HandlerMappingIntrospector introspector, 
        AuthenticationSuccessHandler successHandler,
        KeycloakGroupAuthorityMapper authoritiesMapper,
        KeycloakLogoutHandler keycloakLogoutHandler
        ) throws Exception {
        MvcRequestMatcher.Builder matcher = new MvcRequestMatcher.Builder(introspector);

		http
            .authorizeHttpRequests(a -> a
                    .requestMatchers(matcher.pattern("/dcis/**")).authenticated()
                    .anyRequest().permitAll()
            )
            .oauth2Login(l -> l
                .authorizationEndpoint(Customizer.withDefaults())
                .tokenEndpoint(Customizer.withDefaults())
                .userInfoEndpoint(u -> u.userAuthoritiesMapper(authoritiesMapper))
            )
            .logout(l -> l
                .addLogoutHandler(keycloakLogoutHandler)
                .logoutSuccessUrl(issuerUri + "/protocol/openid-connect/logout")
            )
            .cors(Customizer.withDefaults())
            .csrf(Customizer.withDefaults())
            
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .rememberMe(Customizer.withDefaults())
            ;

        return http.build();
	}

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/dcis/");
        return handler;
    }
}
