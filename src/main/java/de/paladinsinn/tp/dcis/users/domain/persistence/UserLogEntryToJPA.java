/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.users.domain.persistence;

import java.util.function.Function;

import de.paladinsinn.tp.dcis.domain.users.persistence.UserJPA;
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
