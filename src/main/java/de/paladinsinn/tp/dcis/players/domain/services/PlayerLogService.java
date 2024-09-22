package de.paladinsinn.tp.dcis.players.domain.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.paladinsinn.tp.dcis.players.domain.model.Player;
import de.paladinsinn.tp.dcis.players.domain.model.PlayerLogEntry;
import de.paladinsinn.tp.dcis.players.domain.model.PlayerLogEntryToImpl;
import de.paladinsinn.tp.dcis.players.persistence.PlayerJPA;
import de.paladinsinn.tp.dcis.players.persistence.PlayerLogEntryJPA;
import de.paladinsinn.tp.dcis.players.persistence.PlayerLogRepository;
import de.paladinsinn.tp.dcis.players.persistence.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
public class PlayerLogService {
    private final PlayerLogRepository logRepository;
    private final PlayerRepository playerRepository;

    private final PlayerLogEntryToImpl toPlayerLogEntry;

    public PlayerLogEntry log(final Player player, final String system, final String text) {
        return log(loadPlayer(player.getId()), system, text);
    }

    public PlayerLogEntry log(final UUID uid, final String system, final String text) {
        PlayerLogEntry result = logRepository.save(PlayerLogEntryJPA.builder()
                .player(loadPlayer(uid))
                .system(system)
                .text(text)
                .build());

        log.info("Created log entry for player. entry={}", result);
        return result;
    }

    public List<PlayerLogEntry> load(final Player player) {
        return load(player.getId());
    }

    public List<PlayerLogEntry> load(final UUID uid) {
        List<PlayerLogEntry> result = logRepository.findByPlayer_Id(uid).stream().map(p -> (PlayerLogEntry)toPlayerLogEntry.apply(p)).toList();

        log.debug("Loaded log file for player. uid={}, log={}", uid, result);
        return result;
    }

    public Page<PlayerLogEntry> load(final UUID uid, Pageable pageable) {
        Page<PlayerLogEntryJPA> data = logRepository.findByPlayer_Id(uid, pageable);
        Page<PlayerLogEntry> result = new PageImpl<>(data.stream().map(p -> (PlayerLogEntry)toPlayerLogEntry.apply(p)).toList(), data.getPageable(), data.getTotalElements());

        log.debug("Loaded log page for player. uid={}, page={}/{}, log={}", uid,
                result.getPageable().getPageNumber(), result.getTotalPages(), result.getContent());
        return result;
    }

    private PlayerJPA loadPlayer(final UUID uid) {
        Optional<PlayerJPA> result = playerRepository.findById(uid);

        if (! result.isPresent()) {
            throw new IllegalArgumentException("Player with UID " + uid + " does not exist in database.");
        }

        return result.get();
    }
}
