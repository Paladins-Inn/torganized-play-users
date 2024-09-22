package de.paladinsinn.tp.dcis.players.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import de.paladinsinn.tp.dcis.players.domain.model.PlayerLogEntry;
import de.paladinsinn.tp.dcis.players.domain.model.PlayerLogEntryToImpl;
import de.paladinsinn.tp.dcis.players.domain.model.PlayerLogEntryToImplImpl;
import de.paladinsinn.tp.dcis.players.domain.services.PlayerLogService;
import de.paladinsinn.tp.dcis.players.persistence.PlayerJPA;
import de.paladinsinn.tp.dcis.players.persistence.PlayerLogEntryJPA;
import de.paladinsinn.tp.dcis.players.persistence.PlayerLogRepository;
import de.paladinsinn.tp.dcis.players.persistence.PlayerRepository;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@Slf4j
public class PlayerLogServiceTest {
    @Configuration
    static class TestConfiguration {
        @MockBean
        @Getter(onMethod = @__(@Bean))
        private PlayerLogRepository logRepository;
    
        @MockBean
        @Getter(onMethod = @__(@Bean))
        private PlayerRepository playerRepository;
        
        @Bean 
        public PlayerLogEntryToImpl toPlayerLogEntry() {
            return new PlayerLogEntryToImplImpl();
        }

        @Bean
        public PlayerLogService sut() {
            return new PlayerLogService(logRepository, playerRepository, toPlayerLogEntry());
        }
    }

    private static final PlayerJPA player = PlayerJPA.builder()
        .id(UUID.randomUUID())
        .nameSpace("namespace")
        .name("name")
        .created(OffsetDateTime.now(ZoneOffset.UTC))
        .revisioned(OffsetDateTime.now(ZoneOffset.UTC))
        .build();

    private static final PlayerLogEntryJPA entry = PlayerLogEntryJPA.builder()
        .id(UUID.randomUUID())
        .player(player)
        .created(OffsetDateTime.now(ZoneOffset.UTC))
        .system("system")
        .text("test.text")
        .build();


    @Inject
    private PlayerRepository playerRepository;

    @Inject
    private PlayerLogRepository logRepository;

    @Inject
    private PlayerLogService sut;

    @Captor
    private ArgumentCaptor<PlayerLogEntryJPA> logEntry;

    @BeforeAll
    void setUp() {
        log.info("Set up service under test. sut={}", sut);
    }


    @Test
    public void shouldLogANewLogentryWhenAnExistingPlayerIsUsed() {
        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
        when(logRepository.save(logEntry.capture())).thenReturn(entry);

        PlayerLogEntry result = sut.log(player.getId(), "test", "playerlog.test");
        log.debug("Logged message. entry={}", result);
        
        assertThat(result.getPlayer()).isEqualTo(player);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenThePlayerDoesNotExist() {
        when(playerRepository.findById(player.getId())).thenThrow(new IllegalArgumentException());

        sut.log(player.getId(), "test", "playerlog.failure");
    }
}
