package de.paladinsinn.tp.dcis.players.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import de.paladinsinn.tp.dcis.players.domain.model.PlayerLogEntry;
import de.paladinsinn.tp.dcis.players.domain.model.PlayerLogEntryToImpl;
import de.paladinsinn.tp.dcis.players.domain.model.PlayerLogEntryToImplImpl;
import de.paladinsinn.tp.dcis.players.domain.services.PlayerLogService;
import de.paladinsinn.tp.dcis.players.persistence.PlayerJPA;
import de.paladinsinn.tp.dcis.players.persistence.PlayerLogEntryJPA;
import de.paladinsinn.tp.dcis.players.persistence.PlayerLogRepository;
import de.paladinsinn.tp.dcis.players.persistence.PlayerRepository;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@Slf4j
public class PlayerLogServiceTest {
    private static final PlayerJPA player = PlayerJPA.builder()
        .id(1000L)
        .uid(UUID.randomUUID())
        .nameSpace("namespace")
        .name("name")
        .build();

    private static final PlayerLogEntryJPA entry = PlayerLogEntryJPA.builder()
        .id(1L)
        .uid(UUID.randomUUID())
        .player(player)
        .created(OffsetDateTime.now(Clock.systemUTC()))
        .system("system")
        .text("test.text")
        .build();

    @MockBean
    private PlayerLogRepository logRepository;

    @Captor
    private ArgumentCaptor<PlayerLogEntryJPA> logEntry;

    @MockBean
    private PlayerRepository playerRepository;

    private PlayerLogEntryToImpl toPlayerLogEntry = new PlayerLogEntryToImplImpl();

        
    private PlayerLogService sut;


    @BeforeAll
    void setUp() {
        sut = new PlayerLogService(logRepository, playerRepository, toPlayerLogEntry);
        log.info("Set up service under test. sut={}", sut);
    }


    @Test
    public void shouldLogANewLogentryWhenAnExistingPlayerIsUsed() {
        setUp();

        when(playerRepository.findByUid(player.getUid())).thenReturn(player);
        when(logRepository.save(logEntry.capture())).thenReturn(entry);

        PlayerLogEntry result = sut.log(player.getUid(), "test", "playerlog.test");
        log.debug("Logged message. entry={}", result);
        
        assertThat(result.getPlayer()).isEqualTo(player);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenThePlayerDoesNotExist() {
        setUp();

        when(playerRepository.findByUid(player.getUid())).thenThrow(new IllegalArgumentException());

        sut.log(player.getUid(), "test", "playerlog.failure");
    }
}
