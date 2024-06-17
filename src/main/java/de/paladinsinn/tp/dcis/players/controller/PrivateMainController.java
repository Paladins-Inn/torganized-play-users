package de.paladinsinn.tp.dcis.players.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/dcis/")
@Slf4j
public class PrivateMainController {

    @GetMapping
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "GM", "PLAYER"})
    public String getMethodName(Model model) {
        model.addAttribute("access_level", "Spieler");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Request answered. user={}, authorities={}, details={}, principal={}", 
            authentication.getName(),
            authentication.getAuthorities(), authentication.getDetails(), authentication.getPrincipal().getClass().getCanonicalName());

        DefaultOidcUser user = (DefaultOidcUser) authentication.getPrincipal();
        log.info("Userinfo. claims={}", user.getUserInfo().getClaims());

        return "main";
    }
    
}
