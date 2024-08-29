package de.paladinsinn.tp.dcis.players.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.RolesAllowed;


@Controller
@RequestMapping("/dcis")
public class DashboardController {
    @GetMapping(path = "/{name}/dashboard")
    @RolesAllowed("PLAYER")
    public String getMethodName(@PathVariable final String name, Model model) {
        model.addAttribute("name", name);

        return "not-implemented-yet";
    }
    
}
