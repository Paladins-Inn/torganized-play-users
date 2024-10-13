/*
 * Copyright (c) 2024 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.paladinsinn.tp.dcis.users.keycloak;


import de.kaiserpfalzedv.commons.api.resources.HasName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-10-10
 */
@Getter
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class KeycloakGroupException extends KeycloakException implements HasName {
    private String name;

    public KeycloakGroupException(final String groupName) {
        super("Programmer is lazy and does not give any information about the problem with user '" + groupName + "'.");

        this.name = groupName;
    }

    public KeycloakGroupException(final String groupName, final String message) {
        super(message);

        this.name = groupName;
    }

    public KeycloakGroupException(final String groupName, final String message, final Throwable cause) {
        super(message, cause);

        this.name = groupName;
    }
}
