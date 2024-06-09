package de.paladinsinn.tp.dcis.players.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakLogoutHandler implements LogoutHandler {
    private final RestTemplate restTemplate;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        logoutFromKeycloak((OidcUser)auth.getPrincipal());
    }

    private void logoutFromKeycloak(OidcUser user) {
        String endSessionEndpoint = user.getIssuer() + "/protocol/openid-connect/logout";
        UriComponentsBuilder uri = UriComponentsBuilder
            .fromUriString(endSessionEndpoint)
            .queryParam("id_token_hint", user.getIdToken().getTokenValue());

            ResponseEntity<String> logoutResponse = restTemplate.getForEntity(uri.toUriString(), String.class);

            if (logoutResponse.getStatusCode().is2xxSuccessful()) {
                log.info("User logged out from SSO provider. provider={}, user={}, sub={}", user.getIssuer(), user.getPreferredUsername(), user.getSubject());
            } else {
                log.error("Could not logout user from SSO. provider={}, user={}, sub={}", user.getIssuer(), user.getPreferredUsername(), user.getSubject());
            }
    }
}
