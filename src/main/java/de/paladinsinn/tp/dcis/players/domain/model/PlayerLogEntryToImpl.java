package de.paladinsinn.tp.dcis.players.domain.model;

import java.util.function.Function;

import org.mapstruct.Mapper;

@Mapper
public interface PlayerLogEntryToImpl extends Function<PlayerLogEntry, PlayerLogEntryImpl> {
        PlayerLogEntryImpl apply(PlayerLogEntry orig);
}
