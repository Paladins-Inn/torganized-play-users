package de.paladinsinn.tp.dcis.users.controller;

import de.paladinsinn.tp.dcis.domain.users.model.User;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserJPA;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserRepository;
import de.paladinsinn.tp.dcis.users.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${server.servlet.contextPath}:/users")
    private String contextPath;
    
    /**
     * The user service to do the heavy lifting of work.
     */
    private final UserService userService;
    
    /**
     * The repository
     */
    private final UserRepository userRepository;

    
    @GetMapping
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "GM", "PLAYER"})
    public String index(Model model) {
        log.entry(model);
        
        List<UserJPA> data = userRepository.findAll();
        
        model.addAttribute("users", data);

        return log.exit(addContextPath("user-list", model));
    }
    
    
    /**
     * Adds the context path to the model to be used there.
     *
     * @param result The result to return.
     * @param model The model the contextPath should be added to.
     * @return The result string.
     */
    private String addContextPath(final String result, Model model) {
        model.addAttribute("contextPath", contextPath);
        return result;
    }
    
    
    @PostMapping
    @RolesAllowed({"ADMIN", "ORGA"})
    public String create(@RequestBody User user, Model model) {
        log.entry(user, model);
        
        User data = userService.createUser(user);
        model.addAttribute("user", data);
        
        model.addAttribute("contextPath", contextPath);
        return log.exit(addContextPath("user-create", model));
    }


    @GetMapping("/{id}")
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "GM", "PLAYER"})
    public String detail(@PathVariable final UUID id, Model model) {
        log.entry(id, model);
        
        Optional<User> user = userService.retrieveUser(id);
        
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else {
            model.addAttribute("id", id);
            return log.exit("user-not-found");
        }
        
        return log.exit(addContextPath("user-detail", model));
    }
    
    @PutMapping("/{id}")
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "GM", "PLAYER"})
    public String create(@PathVariable final UUID id, @RequestBody User user, Model model) {
        log.entry(id, user, model);
        
        User data = userService.updateUser(id, user);
        model.addAttribute("user", data);
        
        return log.exit(addContextPath("user-create", model));
    }
    
    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "PLAYER"})
    public String delete(@PathVariable final UUID id, Model model) {
        log.entry(id, model);
        
        userService.deleteUser(id);
        
        return log.exit(addContextPath("user-delete", model));
    }
    
    @PutMapping("/{id}/detain")
    @RolesAllowed({"ADMIN","ORGA","JUDGE"})
    public String detain(@PathVariable final UUID id, @RequestParam("ttl") long ttl, Model model) {
        log.entry(id, model);
        
        Optional<User> data = userService.detainUser(id, ttl);
        if (data.isEmpty()) {
            log.warn("User can't be detained: user not found. user={}", id);
            return log.exit("user-not-found");
        }
        
        model.addAttribute("user", data.get());
        
        return log.exit(addContextPath("user-detain", model));
    }
    
    @PutMapping("/{id}/release")
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE"})
    public String release(@PathVariable final UUID id, Model model) {
        log.entry(id, model);
        
        Optional<User> data = userService.releaseUser(id);
        if (data.isEmpty()) {
            log.warn("User can't be released: user not found. user={}", id);
            return log.exit("user-not-found");
        }
        
        model.addAttribute("user", data.get());
        
        return log.exit(addContextPath("user-release", model));
    }
}
