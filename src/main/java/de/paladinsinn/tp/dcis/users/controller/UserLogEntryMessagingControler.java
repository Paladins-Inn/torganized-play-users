/*
 * Copyright (c) 2024 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.paladinsinn.tp.dcis.users.controller;


import de.paladinsinn.tp.dcis.domain.users.events.UserLoginEvent;
import de.paladinsinn.tp.dcis.domain.users.events.UserLogoutEvent;
import de.paladinsinn.tp.dcis.users.domain.services.UserLogService;
import jakarta.inject.Inject;
import lombok.*;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-11-01
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class UserLogEntryMessagingControler {
  private final UserLogService service;
  
  @Bean
  public Consumer<Message<UserLoginEvent>> userLoginConsumer() {
    return log.exit(event -> {
      log.entry(event.getHeaders(), event.getPayload());
      
      service.log(event.getPayload().getUser(), event.getPayload().getSystem(), "User logged in");
      
      log.exit(event.getPayload().getUser());
    });
  }
  
  @Bean
  public Consumer<Message<UserLogoutEvent>> userLogoutConsumer() {
    return log.exit(event -> {
      log.entry(event.getHeaders(), event.getPayload());
      
      service.log(event.getPayload().getUser(), event.getPayload().getSystem(), "User logged out");
      
      log.exit(event.getPayload().getUser());
    });
  }
}
