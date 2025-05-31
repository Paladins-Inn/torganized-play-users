package de.paladinsinn.tp.dcis.users.controller;

import de.kaiserpfalzedv.commons.spring.events.SpringEventBus;
import de.kaiserpfalzedv.commons.users.domain.model.role.KpRole;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.*;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserBannedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserDeletedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserDetainedEvent;
import de.kaiserpfalzedv.commons.users.domain.services.*;
import de.kaiserpfalzedv.commons.users.store.service.DbUserReadService;
import de.paladinsinn.tp.dcis.lib.ui.WebUiModelDefaultValueSetter;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0
 */
@Controller
@Import({
    WebUiModelDefaultValueSetter.class,
})
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class UserController {
  private final WebUiModelDefaultValueSetter uiSetter;
  private final SpringEventBus bus;
  private final RoleReadService roleReadService;
  
  /**
   * The user service to do the heavy lifting of work.
   */
  private final DbUserReadService readService;
  private final UserManagementService userManagementService;
  private final UserReadService<User> userReadService;
  
  
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
      result = userReadService.findByNamespace(namespace);
    } else {
      result = userReadService.findAll();
    }
    
    return log.exit(result);
  }
  
  
  @PostMapping
  public String register(Authentication authentication) {
    log.entry(authentication);
    
    User user = createUserFromAuthentication(authentication);
    
    bus.post(UserCreatedEvent.builder().user(user).build());
    Optional<User> data = userReadService.findById(user.getId());
    
    if (data.isEmpty()) {
      throw new IllegalStateException("The user could not be saved!");
    }
    
    return log.exit("redirect:/user/" + data.get().getId());
  }
  
  private static User createUserFromAuthentication(final Authentication authentication) {
    log.entry(authentication);
    
    DefaultOidcUser oidc = (DefaultOidcUser) authentication.getPrincipal();
    
    log.debug("OIDC information. subject={}, issuer={}, username={}", oidc.getSubject(), oidc.getIssuer(), oidc.getPreferredUsername());
    
    return log.exit(KpUserDetails.builder()
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
    
    Optional<User> user = readService.findById(id);
    
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
    
    Optional<User> existing = readService.findById(id);
    existing.ifPresentOrElse(
        u -> generateChangeEvents(u, user),
        () -> {
          try {
            log.info("Creating new user. id='{}', user={}", id, user);
            userManagementService.create(user);
          } catch (UserCantBeCreatedException e) {
            throw new IllegalStateException(e);
          }
        }
    );
    
    model.addAttribute("user", user);
    
    return log.exit(uiSetter.addContextPath("user-create", authentication, model));
  }
  
  private void generateChangeEvents(final User existing, final User user) {
    log.entry(existing, user);
    
    log.info("Modifying user data. id='{}', existing={}, new={}", existing.getId(), existing, user);
    
    manageNameChanges(existing, user);
    manageSubjectChanges(existing, user);
    manageContactChanges(existing, user);
    manageRoleChanges(existing, user);
    
    log.exit();
  }
  
  private void manageNameChanges(final User existing, final User user) {
    if (
        !existing.getNameSpace().equals(user.getNameSpace())
            && !existing.getName().equals(user.getName())
    ) {
      bus.post(UserNamespaceAndNameModificationEvent.builder().user(user).build());
    } else if (!existing.getNameSpace().equals(user.getNameSpace())) {
      bus.post(UserNamespaceModificationEvent.builder().user(user).build());
    } else if (!existing.getName().equals(user.getName())) {
      bus.post(UserNameModificationEvent.builder().user(user).build());
    }
  }
  
  private void manageSubjectChanges(final User existing, final User user) {
    if (
        !existing.getSubject().equals(user.getSubject())
        || !existing.getIssuer().equals(user.getIssuer())
    ) {
      bus.post(UserSubjectModificationEvent.builder().user(user).build());
    }
  }
  
  private void manageContactChanges(final User existing, final User user) {
    if (!existing.getEmail().equals(user.getEmail())) {
      bus.post(UserEmailModificationEvent.builder().user(user).build());
    }
    
    if (!existing.getDiscord().equals(user.getDiscord())) {
      bus.post(UserDiscordModificationEvent.builder().user(user).build());
    }
    
    if (!existing.getPhone().equals(user.getPhone())) {
      log.warn("Phone number modification event is not implemented yet. user={}, old={}, new={}",
          existing.getId(), existing.getPhone(), user.getPhone());
      // FIXME 2025-05-31 klenkes74: Implement phone number modification event
    }
  }
  
  private void manageRoleChanges(final User existing, final User user) {
    existing.getAuthorities().stream()
          .filter(a -> !user.getAuthorities().contains(a))
          .map(this::loadRoleByAuthority).filter(Optional::isPresent).map(Optional::get)
          .forEach(r -> postRoleRemovedEvent(user, r));
    
    user.getAuthorities().stream()
          .filter(a -> !existing.getAuthorities().contains(a))
          .map(this::loadRoleByAuthority).filter(Optional::isPresent).map(Optional::get)
          .forEach(r -> postRoleAddedEvent(user, r));
  }
  
  private Optional<KpRole> loadRoleByAuthority(final GrantedAuthority authority) {
    log.entry(authority);
    
    Optional<KpRole> result = roleReadService.retrieveByName(authority.getAuthority())
        .stream().findFirst().map(KpRole.class::cast);
    
    return log.exit(result);
  }
  
  private void postRoleRemovedEvent(@NotNull final User user, @NotNull final KpRole role) {
    log.entry(user, role);
    
    log.info("Removing role from user. user={}, role={}/{}", user.getId(), role.getNameSpace(), role.getName());
    
    RoleRemovedFromUserEvent event = RoleRemovedFromUserEvent.builder()
        .user(user)
        .role(role)
        .build();
    
    bus.post(event);
    
    log.exit(event);
  }
  
  private void postRoleAddedEvent(@NotNull final User user, @NotNull final KpRole role) {
    log.entry(user, role);
    
    log.info("Adding role to user. user={}, role={}/{}", user.getId(), role.getNameSpace(), role.getName());
    
    RoleAddedToUserEvent event = RoleAddedToUserEvent.builder()
        .user(user)
        .role(role)
        .build();
    
    bus.post(event);
    
    log.exit(event);
  }
  
  
  @DeleteMapping("/{id}")
  @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "PLAYER"})
  public String delete(
      @PathVariable final UUID id,
      Authentication authentication,
      Model model
  ) {
    log.entry(id, model);
    
    Optional<User> user = userReadService.findById(id);
    user.ifPresent(u -> bus.post(UserDeletedEvent.builder().user(u).build()));
    
    return log.exit(uiSetter.addContextPath("user-delete", authentication, model));
  }
  
  @PutMapping("/{id}/detain")
  @RolesAllowed({"ADMIN", "ORGA", "JUDGE"})
  public String detain(
      @PathVariable final UUID id,
      @Min(1) @Max(1095) @RequestParam("ttl") long ttl,
      Authentication authentication,
      Model model
  ) {
    log.entry(id, model, authentication);
    
    Optional<User> user = userReadService.findById(id);
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
  @RolesAllowed({"ADMIN", "ORGA", "JUDGE"})
  public String ban(
      @PathVariable final UUID id,
      Authentication authentication,
      Model model
  ) {
    log.entry(id, model, authentication);
    
    Optional<User> user = userReadService.findById(id);
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
    
    Optional<User> user = userReadService.findById(id);
    user.ifPresentOrElse(
        u -> {
          bus.post(de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserReleasedEvent.builder().user(u).build());
          model.addAttribute("user", u);
        },
        () -> {
          throw new IllegalArgumentException("User could not be released!");
        }
    );
    
    return log.exit(uiSetter.addContextPath("user-release", authentication, model));
  }
}
