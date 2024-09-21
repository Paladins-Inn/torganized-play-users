package de.paladinsinn.tp.dcis.players.domain.model;

import java.util.function.Function;

import org.mapstruct.Mapper;

@Mapper
public interface PlayerToImpl extends Function<Player, PlayerImpl> {
    PlayerImpl apply(Player orig);
}
