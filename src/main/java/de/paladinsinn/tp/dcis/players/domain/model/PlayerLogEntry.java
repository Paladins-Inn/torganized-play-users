package de.paladinsinn.tp.dcis.players.domain.model;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasTimestamps;

@JsonDeserialize(as = PlayerLogEntryImpl.class)
public interface PlayerLogEntry extends HasId<UUID>, HasTimestamps {
    Player getPlayer();

    String getSystem();
    String getText();
}
