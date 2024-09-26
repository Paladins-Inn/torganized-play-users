package de.paladinsinn.tp.dcis.players.domain.persistence;

import java.util.function.Function;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paladinsinn.tp.dcis.players.domain.model.Player;

@Mapper
public interface PlayerToJpa extends Function<Player, PlayerJPA> {
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "revId", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "revisioned", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    PlayerJPA apply(Player orig);
}
