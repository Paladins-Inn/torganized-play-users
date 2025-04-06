package de.paladinsinn.tp.dcis.users.domain.persistence;

import java.util.function.Function;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paladinsinn.tp.dcis.domain.users.model.User;
import de.paladinsinn.tp.dcis.domain.users.model.UserLogEntry;

@Mapper
public interface UserLogEntryToJPA extends Function<UserLogEntry, UserLogEntryJPA> {
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    UserLogEntryJPA apply(UserLogEntry orig);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "revisioned", ignore = true)
    @Mapping(target = "revId", ignore = true)
    UserJPA map(User orig);
}
