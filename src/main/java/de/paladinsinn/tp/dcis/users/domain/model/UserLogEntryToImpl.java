package de.paladinsinn.tp.dcis.users.domain.model;

import java.util.function.Function;

import de.paladinsinn.tp.dcis.domain.users.model.UserLogEntry;
import de.paladinsinn.tp.dcis.domain.users.model.UserLogEntryImpl;
import org.mapstruct.Mapper;

@Mapper
public interface UserLogEntryToImpl extends Function<UserLogEntry, UserLogEntryImpl> {
        UserLogEntryImpl apply(UserLogEntry orig);
}
