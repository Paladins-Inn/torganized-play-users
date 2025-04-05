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

/**
 * Writes log entries for the user log use-case
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0
 */
@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@XSlf4j
public class UserLogService {
    private final UserLogRepository logRepository;
    private final UserRepository userRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final UserLogEntryToImpl toUserLogEntry;

    public UserLogEntry log(final User player, final String system, final String text) {
        log.entry(player, system, text);

        UserLogEntry result = logRepository.save(UserLogEntryJPA.builder()
                .user(loadUser(player))
                .system(system)
                .comment(text)
                .build()
        );
        
        log.info("Created log entry for user. user={}, system={}, comment={}", player, system, text);
        return log.exit(result);
    }
    
    public UserLogEntry log(final User player, final String system, final String text, final String comment) {
        log.entry(player, system, text, comment);
        
        UserLogEntry result = logRepository.save(UserLogEntryJPA.builder()
            .user(loadUser(player))
            .system(system)
            .text(text)
            .comment(comment)
            .build()
        );
        
        log.info("Created log entry for user. user={}, system={}, text={}, comment={}", player, system, text, comment);
        return log.exit(result);
    }

    public List<UserLogEntry> load(final UUID uid) {
        log.entry(uid);

        List<UserLogEntry> result = logRepository.findByUser_Id(uid).stream().map(p -> (UserLogEntry)toUserLogEntry.apply(p)).toList();

        log.debug("Loaded log file for player. uid={}, log={}", uid, result);

        return log.exit(result);
    }

    public Page<UserLogEntry> load(final UUID uid, Pageable pageable) {
        log.entry(uid);

        Page<UserLogEntryJPA> data = logRepository.findByUser_Id(uid, pageable);
        Page<UserLogEntry> result = new PageImpl<>(data.stream().map(p -> (UserLogEntry)toUserLogEntry.apply(p)).toList(), data.getPageable(), data.getTotalElements());

        log.debug("Loaded log page for player. uid={}, page={}/{}, log={}", uid,
                result.getPageable().getPageNumber(), result.getTotalPages(), result.getContent());

        return log.exit(result);
    }

    private UserJPA loadUser(final User user) {
        log.entry(user);

        Optional<UserJPA> result;
        if (user.getId() != null) {
            result = userRepository.findById(user.getId());
        } else {
            result = userRepository.findByNameSpaceAndName(user.getNameSpace(), user.getName());
        }

        if (result.isEmpty()) {
            log.throwing(WARN, new IllegalArgumentException("User does not exist in database. id='" + user.getId() + "', nameSpace='" + user.getNameSpace() + "', name='" + user.getName() + "'"));
        }

        //noinspection OptionalGetWithoutIsPresent
        return log.exit(result.get());
    }
}
