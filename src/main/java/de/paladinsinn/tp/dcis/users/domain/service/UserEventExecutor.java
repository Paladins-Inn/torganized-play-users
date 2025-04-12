/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.users.domain.service;


import com.google.common.eventbus.Subscribe;
import de.paladinsinn.tp.dcis.commons.events.LoggingEventBus;
import de.paladinsinn.tp.dcis.domain.users.events.UserEventsHandler;
import de.paladinsinn.tp.dcis.domain.users.events.activity.UserLoginEvent;
import de.paladinsinn.tp.dcis.domain.users.events.activity.UserLogoutEvent;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserBannedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserDetainedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserPetitionedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserReleasedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserActivatedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserCreatedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserDeletedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserRemovedEvent;
import de.paladinsinn.tp.dcis.domain.users.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Service;


/**
 * Handles all {@link de.paladinsinn.tp.dcis.domain.users.events.UserBaseEvent}.
 *
 * @author klenkes74
 * @since 12.04.25
 */
@Service
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class UserEventExecutor implements UserEventsHandler {
  private final UserService userService;
  private final UserLogService logService;
  
  private final LoggingEventBus bus;
  
  @PostConstruct
  public void init() {
    log.entry(bus, userService);
    
    bus.register(this);
    
    log.exit();
  }
  
  @PreDestroy
  public void destroy() {
    log.entry(bus, userService);
    
    bus.unregister(this);
    
    log.exit();
  }
 
  @Subscribe
  @Override
  public void event(final UserActivatedEvent event) {
    log.entry(event);
    
    userService.activateUser(event.getUser());
  
    log.exit();
  }
  
  @Subscribe
  @Override
  public void event(final UserCreatedEvent event) {
    log.entry(event);
    
    userService.createUser(event.getUser());
    
    log.exit();
  }
  
  @Subscribe
  @Override
  public void event(final UserDeletedEvent event) {
    log.entry(event);
    
    userService.deleteUser(event.getUser());
    
    log.exit();
  }
  
  @Subscribe
  @Override
  public void event(final UserRemovedEvent event) {
    log.entry(event);
    
    userService.removeUser(event.getUser());
    
    log.exit();
  }
  
  @Subscribe
  @Override
  public void event(final UserBannedEvent event) {
    log.entry(event);
    
    userService.banUser(event.getUser());
    
    log.exit();
  }
  
  @Subscribe
  @Override
  public void event(final UserDetainedEvent event) {
    log.entry(event);
    
    userService.detainUser(event.getUser(), event.getDays());
    
    log.exit();
  }
  
  @Subscribe
  @Override
  public void event(final UserPetitionedEvent event) {
    log.entry(event);
    
    log.info("There is a petition for/against an user. user={}, pettion={}", event.getUser(), event.getPetition());
    
    log.exit();
  }
  
  @Subscribe
  @Override
  public void event(final UserReleasedEvent event) {
    log.entry(event);
    
    userService.releaseUser(event.getUser());
    
    log.exit();
  }
  
  @Subscribe
  @Override
  public void event(final UserLoginEvent event) {
    log.entry(event);
    
    logService.log(event.getUser(), event.getSystem(), event.getI18nKey());
    
    log.exit();
  }
  
  @Subscribe
  @Override
  public void event(final UserLogoutEvent event) {
    log.entry(event);
    
    logService.log(event.getUser(), event.getSystem(), event.getI18nKey());
    
    log.exit();
  }
}
