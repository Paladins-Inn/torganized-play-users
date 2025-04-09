package de.paladinsinn.tp.dcis.users.controller;

import de.paladinsinn.tp.dcis.commons.ui.WebUiModelDefaultValueSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.RolesAllowed;


@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@XSlf4j
public class UserDashboardController {
    private final WebUiModelDefaultValueSetter uiSetter;

    @GetMapping(path = "/{id}")
    @RolesAllowed("PLAYER")
    public String getMethodName(@PathVariable final String id, Authentication authentication, Model model) {
        log.entry(id, authentication, model);


        String result = "dashboard";
        return log.exit(uiSetter.addContextPath(result, authentication, model));
    }
}
