package de.paladinsinn.dcis.users.domain.service;

import de.paladinsinn.tp.dcis.domain.users.model.UserLogEntry;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserJPA;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserRepository;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserLogEntryJPA;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserLogRepository;
import de.paladinsinn.tp.dcis.users.domain.service.UserLogService;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@XSlf4j
@ExtendWith(MockitoExtension.class)
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

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserLogRepository logRepository;

    @InjectMocks
    private UserLogService sut;
    
    @Captor
    private ArgumentCaptor<UserLogEntryJPA> logEntry;


    @Test
    public void shouldLogANewLogEntryWhenAnExistingUserIsUsed() {
        log.exit();
        
        when(userRepository.findById(PLAYER.getId())).thenReturn(Optional.of(PLAYER));
        when(logRepository.save(logEntry.capture())).thenReturn(LOG_ENTRY);

        UserLogEntry result = sut.log(PLAYER, "test", "playerLog.test");
        
        log.debug("log repository saved entry. entry={}", logEntry.getValue());
        log.debug("Logged message. entry={}", result);
        
        assertEquals(PLAYER, result.getUser());
        
        log.exit();
    }

    @Test
    public void shouldFailWhenTheUserDoesNotExist() {
        log.entry();
        
        when(userRepository.findById(PLAYER.getId())).thenThrow(new IllegalArgumentException());

        try {
            sut.log(PLAYER, "test", "playerLog.failure");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // everything is fine.
        }
        
        log.exit();
    }
}
