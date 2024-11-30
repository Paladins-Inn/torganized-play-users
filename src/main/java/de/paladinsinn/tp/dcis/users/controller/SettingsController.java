package de.paladinsinn.tp.dcis.users.controller;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.RolesAllowed;


@Controller
@RequestMapping("/dcis")
@XSlf4j
public class SettingsController {
    @GetMapping(path = "/settings")
    @RolesAllowed("PLAYER")
    public String getMethodName(Model model) {
        log.entry(model);

        model.addAttribute("access_level", "Spieler");

        String result = "not-implemented-yet";
        log.exit(result);
        return result;
    }
    
}
