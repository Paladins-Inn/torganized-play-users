package de.paladinsinn.tp.dcis.players.persistence;

import java.util.List;
import java.util.function.Function;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paladinsinn.tp.dcis.players.domain.model.Player;
import de.paladinsinn.tp.dcis.players.domain.model.PlayerLogEntry;

@Mapper
public interface PlayerLogEntryToJPA extends Function<PlayerLogEntry, PlayerLogEntryJPA> {
    @Mapping(target = "id", ignore = true)
    PlayerLogEntryJPA apply(PlayerLogEntry orig);
    List<PlayerLogEntryJPA> apply(List<? extends PlayerLogEntry> orig);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    PlayerJPA apply(Player orig);
}
