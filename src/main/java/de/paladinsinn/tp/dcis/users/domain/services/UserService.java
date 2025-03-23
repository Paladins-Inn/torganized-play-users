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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.paladinsinn.tp.dcis.users.domain.model.User;
import de.paladinsinn.tp.dcis.users.domain.model.UserImpl;
import de.paladinsinn.tp.dcis.users.domain.model.UserToImpl;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserJPA;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserRepository;
import de.paladinsinn.tp.dcis.users.domain.persistence.UserToJpa;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Service
@RequiredArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@XSlf4j
public class UserService {
    private final UserRepository playerRepository;

    private final UserLogService logService;

    private final UserToImpl toUser;
    private final UserToJpa toUserJPA;

    private final String applicationName;

    public User createUser(User player) {
        log.entry(player);

        player = playerRepository.save(toUserJPA.apply(player));

        logService.log(player, applicationName, "player.created");

        return log.exit(player);
    }

    public User createUser(final UUID uid, final String nameSpace, final String name) {
        return createUser(UserImpl.builder()
                .id(uid)
                .nameSpace(nameSpace)
                .name(name)
                .build()
        );
    }

    public Optional<User> retrieveUser(final UUID uid) {
        log.entry(uid);

        Optional<UserJPA> result = playerRepository.findById(uid);

        log.debug("Loaded player from database. uid={}, player={}", uid, result.isPresent() ? result.get() : "***none***");

        return Optional.of(log.exit(result.orElse(null)));
    }
    
    public Optional<User> retrieveUser(final String nameSpace, final String name) {
        log.entry(nameSpace, name);
        
        Optional<UserJPA> result = playerRepository.findByNameSpaceAndName(nameSpace, name);
        
        log.debug("Loaded player from database. nameSpace={}, name={}, player={}", nameSpace, name, result.isPresent() ? result.get() : "***none***");
        
        return Optional.of(log.exit(result.orElse(null)));
    }

    public List<User> retrieveUsers(final String nameSpace) {
        log.entry(nameSpace);

        List<User> result = new LinkedList<>(playerRepository.findByNameSpace(nameSpace).stream().map(toUser).toList());

        return log.exit(result);
    }

    public Page<User> retrieveUsers(final String nameSpace, final Pageable pageable) {
        log.entry(nameSpace, pageable);

        Page<UserJPA> data = playerRepository.findByNameSpace(nameSpace, pageable);
        Page<User> result = new PageImpl<>(new LinkedList<>(data.stream().map(toUser).toList()), pageable, data.getTotalElements());

        log.debug("Loaded users for namespace. nameSpace='{}', page={}/{}, size={}", nameSpace, 
                result.getPageable().getPageNumber(), result.getTotalPages(), result.getTotalElements());

        return log.exit(result);
    }
}
