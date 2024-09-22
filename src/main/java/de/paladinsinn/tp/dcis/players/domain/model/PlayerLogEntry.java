package de.paladinsinn.tp.dcis.players.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.kaiserpfalzedv.commons.api.resources.HasId;

@JsonDeserialize(as = PlayerLogEntryImpl.class)
public interface PlayerLogEntry extends HasId<UUID> {
    Player getPlayer();

    OffsetDateTime getCreated();
    String getSystem();
    String getText();
}
