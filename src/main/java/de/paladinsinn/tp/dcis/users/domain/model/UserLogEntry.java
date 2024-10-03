package de.paladinsinn.tp.dcis.users.domain.model;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasTimestamps;

@JsonDeserialize(as = UserLogEntryImpl.class)
public interface UserLogEntry extends HasId<UUID>, HasTimestamps {
    User getUser();

    String getSystem();
    String getText();
}
