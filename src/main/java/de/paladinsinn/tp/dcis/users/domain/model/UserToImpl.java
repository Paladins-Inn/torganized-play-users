package de.paladinsinn.tp.dcis.users.domain.model;

import java.util.function.Function;

import de.paladinsinn.tp.dcis.domain.users.model.User;
import de.paladinsinn.tp.dcis.domain.users.model.UserImpl;
import org.mapstruct.Mapper;

@Mapper
public interface UserToImpl extends Function<User, UserImpl> {
    UserImpl apply(User orig);
}
