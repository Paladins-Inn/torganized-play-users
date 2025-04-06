/*
 * Copyright (c) 2024. Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.paladinsinn.tp.dcis.users.domain.services;

import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

import lombok.*;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import de.paladinsinn.tp.dcis.users.domain.model.User;
import de.paladinsinn.tp.dcis.users.domain.model.UserImpl;
import de.paladinsinn.tp.dcis.users.domain.model.UserToImpl;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserJPA;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserRepository;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserToJpa;

@Service
@RequiredArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@XSlf4j
public class UserService {
    private final UserRepository userRepository;

    private final UserLogService logService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final UserToImpl toUser;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final UserToJpa toUserJPA;

    private final String applicationName;
    
    @Getter
    @Setter
    private Authentication authentication;

    public User createUser(final User player) {
        log.entry(player, authentication);

        User result = userRepository.save(toUserJPA.apply(player));

        logService.log(result, applicationName, "log.user.created");

        return log.exit(result);
    }

    public User createUser(final UUID uid, final String nameSpace, final String name) {
        log.entry(uid, nameSpace, name, authentication);
        
        User result = createUser(UserImpl.builder()
                .id(uid)
                .nameSpace(nameSpace)
                .name(name)
                .build()
        );
        
        logService.log(result, applicationName, "log.user.created");
        
        return log.exit(result);
    }

    public Optional<User> retrieveUser(final UUID uid) {
        log.entry(uid, authentication);

        Optional<UserJPA> result = userRepository.findById(uid);

        log.debug("Loaded player from database. uid={}, player={}", uid, result.isPresent() ? result.get() : "***none***");

        return Optional.of(log.exit(result.orElse(null)));
    }
    
    public Optional<User> retrieveUser(final String nameSpace, final String name) {
        log.entry(nameSpace, name, authentication);
        
        Optional<UserJPA> result = userRepository.findByNameSpaceAndName(nameSpace, name);
        
        log.debug("Loaded player from database. nameSpace={}, name={}, player={}", nameSpace, name, result.isPresent() ? result.get() : "***none***");
        
        return Optional.of(log.exit(result.orElse(null)));
    }

    public Page<User> retrieveUsers(final String nameSpace, final Pageable pageable) {
        log.entry(nameSpace, pageable, authentication);

        Page<UserJPA> data = userRepository.findByNameSpace(nameSpace, pageable);
        Page<User> result = new PageImpl<>(new LinkedList<>(data.stream().map(toUser).toList()), pageable, data.getTotalElements());

        log.debug("Loaded users for namespace. nameSpace='{}', page={}/{}, size={}", nameSpace, 
                result.getPageable().getPageNumber(), result.getTotalPages(), result.getTotalElements());

        return log.exit(result);
    }
    
    public User updateUser(final UUID uid, final User user) {
        log.entry(uid, user, authentication);
        
        log.info("Updating user not implemented yet. uid={}, user={}, authentication={}", uid, user, authentication);
        
        Optional<User> data = retrieveUser(uid);
        
        data.ifPresent(value -> logService.log(value, applicationName, "log.user.updated"));
        
        return log.exit(data.orElse(user));
    }
    
    public void deleteUser(final UUID uid) {
        log.entry(uid, authentication);
        
        log.info("Deleting user not implemented yet. uid={}, authentication={}", uid, authentication);
        
        Optional<User> data = retrieveUser(uid);
        
        data.ifPresent(value -> logService.log(value, applicationName, "log.user.deleted"));
        
        log.exit();
    }
    
    public Optional<User> detainUser(final UUID uid, final long ttl) {
        log.entry(uid, authentication);
        
        Optional<UserJPA> data = userRepository.findById(uid);
        
        if (data.isEmpty()) {
            log.warn("User can't be detained. User not found. uid={}", uid);
            
            return log.exit(Optional.empty());
        }

        data.get().detain(ttl);
        
        UserJPA result = userRepository.save(data.get());
        logService.log(result, applicationName, "log.user.detained");

        return log.exit(Optional.of(result));
    }
    
    public Optional<User> releaseUser(final UUID uid) {
        log.entry(uid, authentication);
        
        Optional<UserJPA> data = userRepository.findById(uid);
        if (data.isEmpty()) {
            log.warn("User can't be released. User not found. uid={}", uid);
            return log.exit(Optional.empty());
        }
        
        data.get().release();
        
        UserJPA result = userRepository.save(data.get());
        logService.log(result, applicationName, "log.user.released");
        
        return log.exit(Optional.of(result));
    }
}
