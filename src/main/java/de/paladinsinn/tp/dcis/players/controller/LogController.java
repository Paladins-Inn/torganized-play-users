package de.paladinsinn.tp.dcis.players.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.RolesAllowed;


@Controller
@RequestMapping("/dcis")
public class LogController {
    @GetMapping(path = "/log")
    @RolesAllowed("PLAYER")
    public String getMethodName(Model model) {
        model.addAttribute("access_level", "Spieler");

        return "not-implemented-yet";
    }
    
}
