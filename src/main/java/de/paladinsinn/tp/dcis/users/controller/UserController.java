package de.paladinsinn.tp.dcis.users.controller;

import de.paladinsinn.tp.dcis.commons.ui.WebUiModelDefaultValueSetter;
import de.paladinsinn.tp.dcis.domain.users.model.User;
import de.paladinsinn.tp.dcis.domain.users.model.UserImpl;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserJPA;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserRepository;
import de.paladinsinn.tp.dcis.users.domain.service.UserLogService;
import de.paladinsinn.tp.dcis.domain.users.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0
 */
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@XSlf4j
public class UserController {

    private final WebUiModelDefaultValueSetter uiSetter;
    
    /**
     * The user service to do the heavy lifting of work.
     */
    private final UserService userService;
    
    /**
     * The repository
     */
    private final UserRepository userRepository;
    
    private final UserLogService userLogService;
    
    private final String applicationName;

    
    @GetMapping
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "GM", "PLAYER"})
    public String index(Authentication authentication, Model model) {
        log.entry(model);
        
        List<UserJPA> data = userRepository.findAll();
        
        model.addAttribute("users", data);

        return log.exit(uiSetter.addContextPath("user-list", authentication, model));
    }
    
    
    @PostMapping
    public String register(Authentication authentication) {
        log.entry(authentication);
        
        DefaultOidcUser oidc = (DefaultOidcUser) authentication.getPrincipal();
        
        log.info("OIDC information. subject={}, issuer={}, username={}", oidc.getSubject(), oidc.getIssuer(), oidc.getPreferredUsername());
        
        User user = UserImpl.builder()
            .id(UUID.fromString(oidc.getSubject()))
            .nameSpace(oidc.getIssuer().toString())
            .name(oidc.getPreferredUsername())
            .build();
        
        User data = userService.createUser(user);
        log.info("Created user. user={}", data);
        
        userLogService.log(data, applicationName, "user.created", "User created from OIDC data. issuer=" + oidc.getIssuer()
            + ", username=" + oidc.getPreferredUsername());
        
        return log.exit("redirect:/user/" + data.getId());
    }


    @GetMapping("/{id}")
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "GM", "PLAYER"})
    public String detail(@PathVariable final UUID id, Authentication authentication, Model model) {
        log.entry(id, model);
        
        Optional<User> user = userService.retrieveUser(id);
        
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else {
            model.addAttribute("id", id);
            return log.exit("user-not-found");
        }
        
        return log.exit(uiSetter.addContextPath("user-detail", authentication, model));
    }
    
    @PutMapping("/{id}")
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "GM", "PLAYER"})
    public String create(@PathVariable final UUID id, @RequestBody User user, Authentication authentication, Model model) {
        log.entry(id, user, model);
        
        User data = userService.updateUser(id, user);
        model.addAttribute("user", data);
        
        return log.exit(uiSetter.addContextPath("user-create", authentication, model));
    }
    
    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "PLAYER"})
    public String delete(@PathVariable final UUID id, Authentication authentication, Model model) {
        log.entry(id, model);
        
        userService.deleteUser(id);
        
        return log.exit(uiSetter.addContextPath("user-delete", authentication, model));
    }
    
    @PutMapping("/{id}/detain")
    @RolesAllowed({"ADMIN","ORGA","JUDGE"})
    public String detain(@PathVariable final UUID id, @RequestParam("ttl") long ttl, Authentication authentication, Model model) {
        log.entry(id, model);
        
        Optional<User> data = userService.detainUser(id, ttl);
        if (data.isEmpty()) {
            log.warn("User can't be detained: user not found. user={}", id);
            return log.exit("user-not-found");
        }
        
        model.addAttribute("user", data.get());
        
        return log.exit(uiSetter.addContextPath("user-detain", authentication, model));
    }
    
    @PutMapping("/{id}/release")
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE"})
    public String release(@PathVariable final UUID id, Authentication authentication, Model model) {
        log.entry(id, model);
        
        Optional<User> data = userService.releaseUser(id);
        if (data.isEmpty()) {
            log.warn("User can't be released: user not found. user={}", id);
            return log.exit("user-not-found");
        }
        
        model.addAttribute("user", data.get());
        
        return log.exit(uiSetter.addContextPath("user-release", authentication, model));
    }
}
