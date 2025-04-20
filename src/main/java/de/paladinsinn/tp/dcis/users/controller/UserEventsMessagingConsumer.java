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

package de.paladinsinn.tp.dcis.users.controller;


import de.paladinsinn.tp.dcis.lib.messaging.events.LoggingEventBus;
import de.paladinsinn.tp.dcis.users.client.events.UserBaseEvent;
import de.paladinsinn.tp.dcis.users.client.events.activity.UserLoginEvent;
import de.paladinsinn.tp.dcis.users.client.events.activity.UserLogoutEvent;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserBannedEvent;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserDetainedEvent;
import de.paladinsinn.tp.dcis.users.client.events.arbitation.UserReleasedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserActivatedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserCreatedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserDeletedEvent;
import de.paladinsinn.tp.dcis.users.client.events.state.UserRemovedEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * This is the messaging receiver which will create the internal {@link LoggingEventBus} events.
 * The events will be sent to the local event bus for the real handlers to take care of the events.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@XSlf4j
public class UserEventsMessagingConsumer {
    private final LoggingEventBus bus;
    
    @PostConstruct
    public void init() {
        log.entry(bus);
        
        bus.register(this);
        
        log.exit();
    }
    
    @PreDestroy
    public void destroy() {
        log.entry(bus);
        
        bus.unregister(this);
        
        log.exit();
    }

    
    @Bean
    public Consumer<Message<UserLoginEvent>> userLogin() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());

            postUserEvent(event.getPayload());
            
            log.exit();
        });
    }
    
    @Bean
    public Consumer<Message<UserLogoutEvent>> userLogout() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());
            
            postUserEvent(event.getPayload());
            
            log.exit();
        });
    }
    
    @Bean
    public Consumer<Message<UserDetainedEvent>> userDetained() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());
            
            postUserEvent(event.getPayload());
            
            log.exit();
        });
    }
    
    @Bean
    public Consumer<Message<UserBannedEvent>> userBanned() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());
            
            postUserEvent(event.getPayload());
            
            log.exit();
        });
    }
    
    @Bean
    public Consumer<Message<UserReleasedEvent>> userReleased() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());
            
            postUserEvent(event.getPayload());
            
            log.exit();
        });
    }
    
    
    @Bean
    public Consumer<Message<UserCreatedEvent>> userCreated() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());
            
            postUserEvent(event.getPayload());
            
            log.exit();
        });
    }
    
    @Bean
    public Consumer<Message<UserActivatedEvent>> userActivated() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());
            
            postUserEvent(event.getPayload());
            
            log.exit();
        });
    }
    
    @Bean
    public Consumer<Message<UserDeletedEvent>> userDeleted() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());
            
            postUserEvent(event.getPayload());
            
            log.exit();
        });
    }
    
    @Bean
    public Consumer<Message<UserRemovedEvent>> userRemoved() {
        return log.exit(event -> {
            log.entry(event.getPayload(), event.getHeaders());
            
            postUserEvent(event.getPayload());
            
            log.exit();
        });
    }
    
    
    /**
     * Posts the event so the real handler can take care of.
     * @param event The event to be posted.
     */
    private void postUserEvent(final UserBaseEvent event) {
        log.entry(event);
        
        bus.post(event);
        
        log.exit();
    }
}
