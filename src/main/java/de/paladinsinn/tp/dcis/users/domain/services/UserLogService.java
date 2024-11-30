package de.paladinsinn.tp.dcis.users.domain.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.paladinsinn.tp.dcis.users.domain.model.User;
import de.paladinsinn.tp.dcis.users.domain.model.UserLogEntry;
import de.paladinsinn.tp.dcis.users.domain.model.UserLogEntryToImpl;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserJPA;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserLogEntryJPA;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserLogRepository;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static org.slf4j.ext.XLogger.Level.WARN;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@XSlf4j
public class UserLogService {
    private final UserLogRepository logRepository;
    private final UserRepository playerRepository;

    private final UserLogEntryToImpl toUserLogEntry;

    public UserLogEntry log(final User player, final String system, final String text) {
        return log(player.getId(), system, text);
    }

    public UserLogEntry log(final UUID uid, final String system, final String text) {
        log.entry(uid, system, text);

        UserLogEntry result = logRepository.save(UserLogEntryJPA.builder()
                .user(loadUser(uid))
                .system(system)
                .text(text)
                .build());

        log.info("Created log entry for player. entry={}", result);
        log.exit(result);
        return result;
    }

    public List<UserLogEntry> load(final UUID uid) {
        log.entry(uid);

        List<UserLogEntry> result = logRepository.findByUser_Id(uid).stream().map(p -> (UserLogEntry)toUserLogEntry.apply(p)).toList();

        log.debug("Loaded log file for player. uid={}, log={}", uid, result);

        log.exit(result);
        return result;
    }

    public Page<UserLogEntry> load(final UUID uid, Pageable pageable) {
        log.entry(uid);

        Page<UserLogEntryJPA> data = logRepository.findByUser_Id(uid, pageable);
        Page<UserLogEntry> result = new PageImpl<>(data.stream().map(p -> (UserLogEntry)toUserLogEntry.apply(p)).toList(), data.getPageable(), data.getTotalElements());

        log.debug("Loaded log page for player. uid={}, page={}/{}, log={}", uid,
                result.getPageable().getPageNumber(), result.getTotalPages(), result.getContent());

        log.exit(result);
        return result;
    }

    private UserJPA loadUser(final UUID uid) {
        log.entry(uid);

        Optional<UserJPA> result = playerRepository.findById(uid);

        if (result.isEmpty()) {
            log.throwing(WARN, new IllegalArgumentException("User with UID " + uid + " does not exist in database."));
        }

        //noinspection OptionalGetWithoutIsPresent
        log.exit(result.get());
        return result.get();
    }
}
