package de.paladinsinn.tp.dcis.users.domain.model;

import java.util.function.Function;

import org.mapstruct.Mapper;

@Mapper
public interface UserLogEntryToImpl extends Function<UserLogEntry, UserLogEntryImpl> {
        UserLogEntryImpl apply(UserLogEntry orig);
}
