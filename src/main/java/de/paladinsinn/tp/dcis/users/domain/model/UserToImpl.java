package de.paladinsinn.tp.dcis.users.domain.model;

import java.util.function.Function;

import org.mapstruct.Mapper;

@Mapper
public interface UserToImpl extends Function<User, UserImpl> {
    UserImpl apply(User orig);
}
