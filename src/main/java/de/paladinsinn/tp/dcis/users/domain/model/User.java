package de.paladinsinn.tp.dcis.users.domain.model;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasName;
import de.kaiserpfalzedv.commons.api.resources.HasNameSpace;
import de.kaiserpfalzedv.commons.api.resources.HasTimestamps;

@JsonDeserialize(as = UserImpl.class)
public interface User extends HasId<UUID>, HasNameSpace, HasName, HasTimestamps {
}