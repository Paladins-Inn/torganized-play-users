package de.paladinsinn.tp.dcis.users.domain.service;

import de.paladinsinn.tp.dcis.users.client.model.user.User;
import de.paladinsinn.tp.dcis.users.client.model.user.UserLogEntry;
import de.paladinsinn.tp.dcis.users.domain.model.UserLogEntryJPA;
import de.paladinsinn.tp.dcis.users.domain.model.UserLogRepository;
import de.paladinsinn.tp.dcis.users.store.UserJPA;
import de.paladinsinn.tp.dcis.users.store.UserRepository;
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
        .user(PLAYER.getId())
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
        
        when(logRepository.save(logEntry.capture())).thenReturn(LOG_ENTRY);

        UserLogEntry result = sut.log(PLAYER, "test", "playerLog.test");
        
        log.debug("log repository saved entry. entry={}", logEntry.getValue());
        log.debug("Logged message. entry={}", result);
        
        assertEquals(PLAYER.getId(), result.getUser());
        
        log.exit();
    }

    @Test
    public void shouldFailWhenTheUserHasNoIDAndTheNameAndNamespaceIsNotFound() {
        log.entry();
        
        User object = PLAYER.toBuilder().id(null).build();
        
        when(userRepository.findByNameSpaceAndName(object.getNameSpace(), object.getName()))
            .thenReturn(Optional.empty());

        try {
            sut.log(object, "test", "playerLog.failure");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // everything is fine.
        }
        
        log.exit();
    }
    
    @Test
    public void shouldFindTheUserWhenTheUserHasNoIDButTheNameAndNamespaceIsFound() {
        log.entry();
        
        User object = PLAYER.toBuilder().id(null).build();
        
        when(userRepository.findByNameSpaceAndName(object.getNameSpace(), object.getName()))
            .thenReturn(Optional.of(PLAYER));
        when(logRepository.save(logEntry.capture())).thenReturn(LOG_ENTRY);
        
        UserLogEntry result = sut.log(object, "test", "playerLog.test");
        
        log.debug("log repository saved entry. entry={}", logEntry.getValue());
        log.debug("Logged message. entry={}", result);
        
        assertEquals(PLAYER.getId(), result.getUser());
        
        log.exit();
    }
}
