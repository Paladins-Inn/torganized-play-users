/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.paladinsinn.tp.dcis.users.domain.services;


import de.paladinsinn.tp.dcis.users.domain.events.UserBlockedEvent;
import de.paladinsinn.tp.dcis.users.domain.events.UserLoginEvent;
import de.paladinsinn.tp.dcis.users.domain.events.UserLogoutEvent;
import de.paladinsinn.tp.dcis.users.domain.events.UserRemovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0
 */
@Component
@RequiredArgsConstructor
@XSlf4j
public class UserLogWriter  {
    private final UserLogService userLogService;
    
    @Bean
    public Consumer<Message<UserLoginEvent>> userLogin() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());
            UserLoginEvent payload = event.getPayload();
            
            userLogService.log(payload.getUser(), payload.getSystem(), "log.user.log-in", "");
            log.exit();
        });
    }
    
    @Bean
    public Consumer<Message<UserLogoutEvent>> userLogout() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());
            UserLogoutEvent payload = event.getPayload();
            
            userLogService.log(payload.getUser(), payload.getSystem(), "log.user.log-out", "");
            log.exit();
        });
    }
    
    @Bean
    public Consumer<Message<UserBlockedEvent>> userBlocked() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());
            UserBlockedEvent payload = event.getPayload();
            
            userLogService.log(payload.getUser(), payload.getSystem(), "log.user.is-detained", "");
            log.exit();
        });
    }
    
    @Bean
    public Consumer<Message<UserRemovedEvent>> userRemoved() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());
            UserRemovedEvent payload = event.getPayload();
            
            userLogService.log(payload.getUser(), payload.getSystem(), "log.user.is-deleted", "");
            log.exit();
            
        });
    }
}
