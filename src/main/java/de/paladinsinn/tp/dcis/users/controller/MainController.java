package de.paladinsinn.tp.dcis.users.controller;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.RolesAllowed;


@Controller
@RequestMapping("/")
@XSlf4j
public class MainController {

    @GetMapping
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "GM", "PLAYER"})
    public String index(final Authentication authentication, Model model) {
        log.entry(authentication, model);

        logAuthorities(authentication);

        model.addAttribute("access_level", "Spieler");

        String result = "main";
        return log.exit(result);
    }

    private void logAuthorities(Authentication authentication) {
        log.entry(authentication);

        authentication.getAuthorities().forEach(authority -> log.debug("Authority: {}", authority));

        log.exit();
    }

    @GetMapping(path = "/settings")
    @RolesAllowed("PLAYER")
    public String settings(Model model) {
        log.entry(model);

        model.addAttribute("access_level", "Spieler");

        String result = "not-implemented-yet";
        return log.exit(result);
    }

}
