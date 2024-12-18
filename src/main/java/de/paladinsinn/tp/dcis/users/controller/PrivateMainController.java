package de.paladinsinn.tp.dcis.users.controller;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.RolesAllowed;


@Controller
@RequestMapping("/")
@XSlf4j
public class PrivateMainController {

    @GetMapping
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "GM", "PLAYER"})
    public String index(Model model) {
        log.entry(model);

        model.addAttribute("access_level", "Spieler");

        String result = "main";
        log.exit(result);
        return result;
    }

    @GetMapping(path = "/settings")
    @RolesAllowed("PLAYER")
    public String settings(Model model) {
        log.entry(model);

        model.addAttribute("access_level", "Spieler");

        String result = "not-implemented-yet";
        log.exit(result);
        return result;
    }

}
