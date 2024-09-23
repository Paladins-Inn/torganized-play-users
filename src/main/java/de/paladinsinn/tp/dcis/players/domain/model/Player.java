package de.paladinsinn.tp.dcis.players.domain.model;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasName;
import de.kaiserpfalzedv.commons.api.resources.HasNameSpace;
import de.kaiserpfalzedv.commons.api.resources.HasTimestamps;

@JsonDeserialize(as = PlayerImpl.class)
public interface Player extends HasId<UUID>, HasNameSpace, HasName, HasTimestamps {
}