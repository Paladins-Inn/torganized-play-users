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

package de.paladinsinn.tp.dcis.users.domain.service;

import de.paladinsinn.tp.dcis.users.client.model.user.User;
import de.paladinsinn.tp.dcis.users.client.model.user.UserLogEntry;
import de.paladinsinn.tp.dcis.users.client.model.user.UserLogEntryToImpl;
import de.paladinsinn.tp.dcis.users.domain.model.UserLogEntryJPA;
import de.paladinsinn.tp.dcis.users.domain.model.UserLogRepository;
import de.paladinsinn.tp.dcis.users.store.UserJPA;
import de.paladinsinn.tp.dcis.users.store.UserRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.slf4j.ext.XLogger.Level.WARN;

/**
 * Writes log entries for the user log use-case
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0
 */
@SuppressWarnings("unused")
@Service
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@XSlf4j
public class UserLogService {
    private final UserLogRepository logRepository;
    private final UserRepository userRepository;

    private final UserLogEntryToImpl toUserLogEntry;

    public UserLogEntry log(final User player, final String system, final String text) {
        log.entry(player, system, text);

        UserLogEntry result = logRepository.save(UserLogEntryJPA.builder()
                .user(getUserId(player))
                .system(system)
                .comment(text)
                .build()
        );
        
        return log.exit(result);
    }
    
    public UserLogEntry log(final User player, final String system, final String text, final String comment) {
        log.entry(player, system, text, comment);
        
        UserLogEntry result = logRepository.save(UserLogEntryJPA.builder()
            .user(getUserId(player))
            .system(system)
            .text(text)
            .comment(comment)
            .build()
        );
        
        return log.exit(result);
    }

    public List<UserLogEntry> load(final UUID uid) {
        log.entry(uid);

        List<UserLogEntry> result = logRepository.findByUser(uid).stream().map(p -> (UserLogEntry)toUserLogEntry.apply(p)).toList();

        log.debug("Loaded log file for player. uid={}, log={}", uid, result);

        return log.exit(result);
    }

    public Page<UserLogEntry> load(final UUID uid, Pageable pageable) {
        log.entry(uid);

        Page<UserLogEntryJPA> data = logRepository.findByUser(uid, pageable);
        Page<UserLogEntry> result = new PageImpl<>(data.stream().map(p -> (UserLogEntry)toUserLogEntry.apply(p)).toList(), data.getPageable(), data.getTotalElements());

        log.debug("Loaded log page for player. uid={}, page={}/{}, log={}", uid,
                result.getPageable().getPageNumber(), result.getTotalPages(), result.getContent());

        return log.exit(result);
    }

    private UUID getUserId(final User user) {
        log.entry(user);

        UUID result = user.getId();
        if (result == null) {
            Optional<UserJPA> jpa = userRepository.findByNameSpaceAndName(user.getNameSpace(), user.getName());
            if (jpa.isPresent()) {
                result = jpa.get().getId();
            } else {
                throw log.throwing(WARN, new IllegalArgumentException("User does not exist in database. id='" + user.getId()
                    + "', nameSpace='" + user.getNameSpace() + "', name='" + user.getName() + "'"));
            }
        }

        return result;
    }
}
