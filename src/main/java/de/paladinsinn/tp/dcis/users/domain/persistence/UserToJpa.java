package de.paladinsinn.tp.dcis.users.domain.persistence;

import java.util.function.Function;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paladinsinn.tp.dcis.users.domain.model.User;

@Mapper
public interface UserToJpa extends Function<User, UserJPA> {
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "revId", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "revisioned", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    UserJPA apply(User orig);
}
