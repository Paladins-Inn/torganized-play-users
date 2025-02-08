package de.paladinsinn.tp.dcis.users.controller;

import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.OffsetDateTime;


@Controller
@RequestMapping("/test")
@XSlf4j
public class TestController {

    @Value("${server.servlet.contextPath}:/users")
    private String contextPath;

    @GetMapping
    @PermitAll
    public String index(final Authentication authentication, Model model) {
        log.entry(authentication, model);

        logAuthorities(authentication);

        model.addAttribute("date", OffsetDateTime.now());
        model.addAttribute("contextPath", contextPath);
        
        User user = new User();
        model.addAttribute("user", user);
        
        return log.exit("test");
    }
    
    @Data
    public static class User {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        OffsetDateTime date = OffsetDateTime.now();
    }

    private void logAuthorities(Authentication authentication) {
        log.entry(authentication);

        authentication.getAuthorities().forEach(authority -> log.debug("Authority: {}", authority));

        log.exit();
    }
}
