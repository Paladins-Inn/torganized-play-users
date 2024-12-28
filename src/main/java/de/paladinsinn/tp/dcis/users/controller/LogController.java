package de.paladinsinn.tp.dcis.users.controller;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.RolesAllowed;


@Controller
@RequestMapping("/")
@XSlf4j
public class LogController {

    @Value("${server.servlet.contextPath}:/users")
    private String contextPath;

    @GetMapping(path = "/log")
    @RolesAllowed("PLAYER")
    public String getMethodName(Model model) {
        log.entry(model);

        model.addAttribute("access_level", "Spieler");
        model.addAttribute("contextPath", contextPath);

        String result = "not-implemented-yet";
        log.exit(result);
        return result;
    }
    
}
