package de.paladinsinn.tp.dcis.players.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = PlayerLogEntryImpl.class)
public interface PlayerLogEntry {
    UUID getUid();
    Player getPlayer();

    OffsetDateTime getCreated();
    String getSystem();
    String getText();
}
