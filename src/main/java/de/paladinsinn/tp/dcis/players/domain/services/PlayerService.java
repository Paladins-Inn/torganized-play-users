package de.paladinsinn.tp.dcis.players.domain.services;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.paladinsinn.tp.dcis.players.domain.model.Player;
import de.paladinsinn.tp.dcis.players.domain.model.PlayerImpl;
import de.paladinsinn.tp.dcis.players.domain.model.PlayerToImpl;
import de.paladinsinn.tp.dcis.players.persistence.PlayerJPA;
import de.paladinsinn.tp.dcis.players.persistence.PlayerRepository;
import de.paladinsinn.tp.dcis.players.persistence.PlayerToJpa;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
public class PlayerService {
    private final PlayerRepository playerRepository;

    private final PlayerLogService logService;

    private final PlayerToImpl toPlayer;
    private final PlayerToJpa toPlayerJPA;

    private final String applicationName;

    public Player createPlayer(Player player) {
        player = playerRepository.save(toPlayerJPA.apply(player));

        logService.log(player.getUid(), applicationName, "player.created");

        log.info("Created player in database. player={}", player);
        return player;
    }

    public Player createPlayer(final UUID uid, final String nameSpace, final String name) {
        return createPlayer(PlayerImpl.builder()
                .uid(uid)
                .nameSpace(nameSpace)
                .name(name)
                .build()
        );
    }

    public Optional<Player> retrievePlayer(final UUID uid) {
        Player result = playerRepository.findByUid(uid);

        log.debug("Loaded player from database. uid={}, player={}", uid, result);
        return Optional.ofNullable(result);
    }

    public List<Player> retrievePlayers(final String nameSpace) {
        List<Player> result = new LinkedList<>(playerRepository.findByNameSpace(nameSpace).stream().map(toPlayer::apply).toList());

        log.debug("Loaded players for namespace. nameSpace='{}', players={}", nameSpace, result);
        return result;
    }

    public Page<Player> retrievePlayers(final String nameSpace, final Pageable pageable) {
        Page<PlayerJPA> data = playerRepository.findByNameSpace(nameSpace, pageable);
        Page<Player> result = new PageImpl<>(new LinkedList<>(data.stream().map(toPlayer::apply).toList()), pageable, data.getTotalElements());

        log.debug("Loaded players for namespace. nameSpace='{}', page={}/{}, size={}", nameSpace, 
                result.getPageable().getPageNumber(), result.getTotalPages(), result.getTotalElements());
        return result;
    }

}
