package de.paladinsinn.tp.dcis.users.controller;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.PermitAll;


@Controller
@RequestMapping("/public/")
@XSlf4j
public class MainController {
    @GetMapping
    @PermitAll
    public String getMethodName(Model model) {
        log.entry(model);

        model.addAttribute("access_level", "Spieler");

        String result = "index";
        log.exit(result);
        return result;
    }
    
}
