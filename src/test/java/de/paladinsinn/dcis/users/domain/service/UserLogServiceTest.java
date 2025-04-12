package de.paladinsinn.dcis.users.domain.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.*;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import de.paladinsinn.tp.dcis.domain.users.model.UserLogEntry;
import de.paladinsinn.tp.dcis.domain.users.model.UserLogEntryToImpl;
import de.paladinsinn.tp.dcis.users.domain.model.UserLogEntryToImplImpl;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserJPA;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserLogEntryJPA;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserLogRepository;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserRepository;
import de.paladinsinn.tp.dcis.users.domain.service.UserLogService;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("CdiInjectionPointsInspection")
@RunWith(SpringRunner.class)
@Slf4j
public class UserLogServiceTest {
    private static final UserJPA PLAYER = UserJPA.builder()
        .id(UUID.randomUUID())
        .nameSpace("namespace")
        .name("name")
        .detainmentDuration(Duration.ofDays(30L))
        .detainedTill(LocalDate.now().atStartOfDay(ZoneId.of("UTC")).minusDays(16L).toOffsetDateTime())
        .created(OffsetDateTime.now(ZoneOffset.UTC))
        .revisioned(OffsetDateTime.now(ZoneOffset.UTC))
        .build();

    private static final UserLogEntryJPA LOG_ENTRY = UserLogEntryJPA.builder()
        .id(UUID.randomUUID())
        .user(PLAYER)
        .created(OffsetDateTime.now(ZoneOffset.UTC))
        .system("system")
        .text("test.text")
        .build();


    @Inject
    private UserRepository playerRepository;

    @Inject
    private UserLogRepository logRepository;

    @Inject
    private UserLogService sut;

    @Captor
    private ArgumentCaptor<UserLogEntryJPA> logEntry;
    
    @Test
    public void shouldLogANewLogEntryWhenAnExistingUserIsUsed() {
        when(playerRepository.findById(PLAYER.getId())).thenReturn(Optional.of(PLAYER));
        when(logRepository.save(logEntry.capture())).thenReturn(LOG_ENTRY);

        UserLogEntry result = sut.log(PLAYER, "test", "playerLog.test");
        log.debug("Logged message. entry={}", result);
        
        assertEquals(PLAYER, result.getUser());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenTheUserDoesNotExist() {
        when(playerRepository.findById(PLAYER.getId())).thenThrow(new IllegalArgumentException());

        sut.log(PLAYER, "test", "playerLog.failure");
    }

    @Configuration
    static class TestConfiguration {
        @MockBean
        @Getter(onMethod = @__(@Bean))
        private UserLogRepository logRepository;
    
        @MockBean
        @Getter(onMethod = @__(@Bean))
        private UserRepository playerRepository;
        
        @Bean 
        public UserLogEntryToImpl toUserLogEntry() {
            return new UserLogEntryToImplImpl();
        }

        @Bean
        public UserLogService sut() {
            return new UserLogService(logRepository, playerRepository, toUserLogEntry());
        }
    }
}
