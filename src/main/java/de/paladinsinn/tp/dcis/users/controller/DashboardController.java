package de.paladinsinn.tp.dcis.users.controller;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.RolesAllowed;


@Controller
@RequestMapping("/dcis")
@XSlf4j
public class DashboardController {
    @GetMapping(path = "/{name}/dashboard")
    @RolesAllowed("PLAYER")
    public String getMethodName(@PathVariable final String name, Model model) {
        log.entry(name, model);

        model.addAttribute("name", name);

        String result = "not-implemented-yet";
        log.exit(result);
        return result;
    }
    
}
