package de.paladinsinn.tp.dcis.users.controller;

import de.paladinsinn.tp.dcis.lib.messaging.events.LoggingEventBus;
import de.paladinsinn.tp.dcis.lib.ui.WebUiModelDefaultValueSetter;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserBannedEvent;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserDetainedEvent;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserReleasedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserCreatedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserDeletedEvent;
import de.paladinsinn.tp.dcis.users.client.model.user.User;
import de.paladinsinn.tp.dcis.users.client.model.user.UserImpl;
import de.paladinsinn.tp.dcis.users.store.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class UserController {
    private final WebUiModelDefaultValueSetter uiSetter;
    private final LoggingEventBus bus;
    
    /**
     * The user service to do the heavy lifting of work.
     */
    private final UserService userService;
  
    
    @GetMapping
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "GM", "PLAYER"})
    public String index(
        @RequestParam(value = "namespace", defaultValue = "") final String namespace,
        Authentication authentication,
        Model model
  ) {
        log.entry(model);
        
        model.addAttribute("users", loadUsers(namespace));

        return log.exit(uiSetter.addContextPath("user-list", authentication, model));
    }
    
    private List<User> loadUsers(final String namespace) {
        log.entry(namespace);
        
        List<User> result;
        if (namespace == null || namespace.isEmpty()) {
            result = userService.retrieveUsers(namespace);
        } else {
            result = userService.retrieveUsers();
        }
        
        return log.exit(result);
    }
    
    
    @PostMapping
    public String register(Authentication authentication) {
        log.entry(authentication);
        
        User user = createUserFromAuthentication(authentication);
        
        bus.post(UserCreatedEvent.builder().user(user).build());
        Optional<User> data = userService.retrieveUser(user.getId());
        
        if (data.isEmpty()) {
            throw new IllegalStateException("The user could not be saved!");
        }
        
        return log.exit("redirect:/user/" + data.get().getId());
    }
    
    private static User createUserFromAuthentication(final Authentication authentication) {
        log.entry(authentication);
        
        DefaultOidcUser oidc = (DefaultOidcUser) authentication.getPrincipal();
        
        log.debug("OIDC information. subject={}, issuer={}, username={}", oidc.getSubject(), oidc.getIssuer(), oidc.getPreferredUsername());
        
        return log.exit(UserImpl.builder()
            .id(UUID.fromString(oidc.getSubject()))
            .nameSpace(oidc.getIssuer().toString())
            .name(oidc.getPreferredUsername())
            .build()
        );
    }
    
    
    @GetMapping("/{id}")
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "GM", "PLAYER"})
    public String detail(
        @PathVariable final UUID id,
        Authentication authentication,
        Model model
    ) {
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
    public String update(
        @PathVariable final UUID id,
        @RequestBody User user,
        Authentication authentication,
        Model model
    ) {
        log.entry(id, user, model);
        
        User data = userService.updateUser(id, user);
        model.addAttribute("user", data);
        
        return log.exit(uiSetter.addContextPath("user-create", authentication, model));
    }
    
    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "PLAYER"})
    public String delete(
        @PathVariable final UUID id,
        Authentication authentication,
        Model model
    ) {
        log.entry(id, model);
        
        Optional<User> user = userService.retrieveUser(id);
        user.ifPresent(u -> bus.post(UserDeletedEvent.builder().user(u).build()));
        
        return log.exit(uiSetter.addContextPath("user-delete", authentication, model));
    }
    
    @PutMapping("/{id}/detain")
    @RolesAllowed({"ADMIN","ORGA","JUDGE"})
    public String detain(
        @PathVariable final UUID id,
        @Min(1) @Max(1095) @RequestParam("ttl") long ttl,
        Authentication authentication,
        Model model
    ) {
        log.entry(id, model, authentication);
        
        Optional<User> user = userService.retrieveUser(id);
        user.ifPresentOrElse(
            u -> {
                bus.post(UserDetainedEvent.builder().user(u).days(ttl).build());
                model.addAttribute("user", u);
            },
            () -> {
                throw new IllegalArgumentException("User could not be released!");
            }
        );
        
        return log.exit(uiSetter.addContextPath("user-detain", authentication, model));
    }
    
    @PutMapping("/{id}/ban")
    @RolesAllowed({"ADMIN","ORGA","JUDGE"})
    public String ban(
        @PathVariable final UUID id,
        Authentication authentication,
        Model model
    ) {
        log.entry(id, model, authentication);
        
        Optional<User> user = userService.retrieveUser(id);
        user.ifPresentOrElse(
            u -> {
                bus.post(UserBannedEvent.builder().user(u).build());
                model.addAttribute("user", u);
            },
            () -> {
                throw new IllegalArgumentException("User could not be released!");
            }
        );
        
        return log.exit(uiSetter.addContextPath("user-detain", authentication, model));
    }
    
    @PutMapping("/{id}/release")
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE"})
    public String release(@PathVariable final UUID id, Authentication authentication, Model model) {
        log.entry(id, model);
        
        Optional<User> user = userService.retrieveUser(id);
        user.ifPresentOrElse(
            u -> {
                bus.post(UserReleasedEvent.builder().user(u).build());
                model.addAttribute("user", u);
            },
            () -> {
                throw new IllegalArgumentException("User could not be released!");
            }
        );
        
        return log.exit(uiSetter.addContextPath("user-release", authentication, model));
    }
}
