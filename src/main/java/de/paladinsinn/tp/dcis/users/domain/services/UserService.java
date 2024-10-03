package de.paladinsinn.tp.dcis.users.domain.services;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
public class UserService {
    private final UserRepository playerRepository;

    private final UserLogService logService;

    private final UserToImpl toUser;
    private final UserToJpa toUserJPA;

    private final String applicationName;

    public User createUser(User player) {
        player = playerRepository.save(toUserJPA.apply(player));

        logService.log(player.getId(), applicationName, "player.created");

        log.info("Created player in database. player={}", player);
        return player;
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
        Optional<UserJPA> result = playerRepository.findById(uid);

        log.debug("Loaded player from database. uid={}, player={}", uid, result.isPresent() ? result.get() : "***none***");
        return Optional.ofNullable(result.isPresent() ? toUser.apply(result.get()) : null);
    }

    public List<User> retrieveUsers(final String nameSpace) {
        List<User> result = new LinkedList<>(playerRepository.findByNameSpace(nameSpace).stream().map(toUser::apply).toList());

        log.debug("Loaded users for namespace. nameSpace='{}', users={}", nameSpace, result);
        return result;
    }

    public Page<User> retrieveUsers(final String nameSpace, final Pageable pageable) {
        Page<UserJPA> data = playerRepository.findByNameSpace(nameSpace, pageable);
        Page<User> result = new PageImpl<>(new LinkedList<>(data.stream().map(toUser::apply).toList()), pageable, data.getTotalElements());

        log.debug("Loaded users for namespace. nameSpace='{}', page={}/{}, size={}", nameSpace, 
                result.getPageable().getPageNumber(), result.getTotalPages(), result.getTotalElements());
        return result;
    }

}
