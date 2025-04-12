/*
 * Copyright (c) 2024-2025. Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.paladinsinn.dcis.users.domain.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import de.paladinsinn.tp.dcis.commons.events.EnableEventBus;
import de.paladinsinn.tp.dcis.commons.formatter.EnableKaiserpfalzCommonsSpringFormatters;
import de.paladinsinn.tp.dcis.domain.users.events.UserBaseEvent;
import de.paladinsinn.tp.dcis.domain.users.events.activity.UserLoginEvent;
import de.paladinsinn.tp.dcis.domain.users.events.activity.UserLogoutEvent;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserBannedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserDetainedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.arbitation.UserReleasedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserActivatedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserCreatedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserDeletedEvent;
import de.paladinsinn.tp.dcis.domain.users.events.state.UserRemovedEvent;
import de.paladinsinn.tp.dcis.domain.users.model.UserImpl;
import de.paladinsinn.tp.dcis.domain.users.model.UserToImplImpl;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserToJpaImpl;
import de.paladinsinn.tp.dcis.domain.users.services.EnableUserManagement;
import de.paladinsinn.tp.dcis.users.domain.service.UserMessagingSender;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAspectsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Tests the UserLogEntrySender.
 *
 * <p>The test ist done as integration test. It will send an {@link EventBus#post(Object)} for the user events and checks if it is handled by the spring cloud streaming service.</p>
 *
 * @author klenkes74
 * @since 2025-03-23
 */
@SpringBootTest
@ActiveProfiles({"test"})
@EnableAutoConfiguration(exclude = {
    MetricsAspectsAutoConfiguration.class,
    MetricsAutoConfiguration.class,
    WebMvcAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class,
    OAuth2ClientAutoConfiguration.class
})
@EnableTestBinder
@EnableJpaRepositories(basePackages = {"de.paladinsinn.tp.dcis"})
@EntityScan(basePackages = {"de.paladinsinn.tp.dcis"})
@EnableEventBus
@EnableUserManagement
@EnableKaiserpfalzCommonsSpringFormatters
@Import({
    UserMessagingSender.class,
    UserToImplImpl.class,
    UserToJpaImpl.class
})
@XSlf4j
public class UserMessagingSenderIT {
  
  @Autowired
  private OutputDestination outputDestination;
  
  @Autowired
  private UserMessagingSender sut;
  
  @Value("${spring.application.name:Mary}")
  private String application;
  
  @Autowired
  private Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;
  
  private static final String sinkName = "user-events";
  
  @Test
  public void shouldSendTheLoginEvent() throws IOException {
    UserLoginEvent loginEvent = UserLoginEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();
    log.entry(sinkName, loginEvent);

    sut.event(loginEvent);

    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserLoginEvent received = getUserEvent(result.getPayload(), UserLoginEvent.class);
    
    verifyReceivedEvent(loginEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheLogoutEvent() throws IOException {
    UserLogoutEvent logoutEvent = UserLogoutEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);

    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserLogoutEvent.class);
    verifyReceivedEvent(logoutEvent, received);

    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheCreateEvent() throws IOException {
    UserCreatedEvent logoutEvent = UserCreatedEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserCreatedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheActivationEvent() throws IOException {
    UserActivatedEvent logoutEvent = UserActivatedEvent.builder()
        .user(createDefaultUser())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserActivatedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheDeletionEvent() throws IOException {
    UserDeletedEvent logoutEvent = UserDeletedEvent.builder()
        .user(createDefaultUser().delete())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserDeletedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheRemovingEvent() throws IOException {
    UserRemovedEvent logoutEvent = UserRemovedEvent.builder()
        .user(createDefaultUser().delete())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserRemovedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheDetainedEvent() throws IOException {
    UserDetainedEvent logoutEvent = UserDetainedEvent.builder()
        .user(createDefaultUser().detain(100))
        .days(100)
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserDetainedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheBanningEvent() throws IOException {
    UserBannedEvent logoutEvent = UserBannedEvent.builder()
        .user(createDefaultUser().ban())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserBannedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  @Test
  public void shouldSendTheReleasingEvent() throws IOException {
    UserReleasedEvent logoutEvent = UserReleasedEvent.builder()
        .user(createDefaultUser().release())
        .system(application)
        .build();
    log.entry(sinkName, logoutEvent);
    
    sut.event(logoutEvent);
    
    Message<byte[]> result = outputDestination.receive(1000L, sinkName);
    UserBaseEvent received = getUserEvent(result.getPayload(), UserReleasedEvent.class);
    verifyReceivedEvent(logoutEvent, received);
    
    log.exit("success");
  }
  
  
  private static UserImpl createDefaultUser() {
    return UserImpl.builder()
        .name("Peter")
        .nameSpace("Paul")
        .build();
  }
  
  private <T extends UserBaseEvent> T getUserEvent(byte[] payload, Class<T> clasz) throws IOException {
    ObjectMapper mapper = jackson2ObjectMapperBuilder.build();
    
    log.trace("Reading payload to event. payload={}", new String(payload));
    
    return log.exit(mapper.readValue(payload, clasz));
  }
  
  private static void verifyReceivedEvent(final UserBaseEvent expected, final UserBaseEvent actual) {
    log.trace("Received via stream: event={}", actual);
    
    assertEquals(expected, actual);
    assertEquals(expected.getSystem(), actual.getSystem());
    assertEquals(expected.getUser().getId(), actual.getUser().getId());
    assertEquals(expected.getUser().getName(), actual.getUser().getName());
    assertEquals(expected.getUser().getNameSpace(), actual.getUser().getNameSpace());
  }
}
