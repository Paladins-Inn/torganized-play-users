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

import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-10-10
 */
@Service
@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Slf4j
public class KeycloakUserGroupManagementService {
    private Keycloak client;
    private KeycloakConfigurationProperties properties;

    /**
     * @param userName the user who needs to join a group.
     * @param groupName the group to be joined.
     * @return true = the join worked; false = the join didn't work.
     * @throws KeycloakUserAmbiguousException If there are more than one user with that userName.
     * @throws KeycloakUserNotFoundException If there is not a user with that userName.
     * @throws KeycloakGroupNotFoundException 
     * @throws KeycloakGroupAmbiguousException 
     */
    public boolean join(final String userName, final String groupName) 
            throws  KeycloakUserAmbiguousException, KeycloakUserNotFoundException, 
                    KeycloakGroupAmbiguousException, KeycloakGroupNotFoundException {
        UserResource user = user(userName);
        GroupRepresentation group = group(groupName);

        return user.groups().add(group);
    }

    private UserResource user(final String userName) throws KeycloakUserAmbiguousException, KeycloakUserNotFoundException {
        UserRepresentation user = userRepresentation(userName);
        
        return client.realm(properties.getRealm()).users().get(user.getId());
    }

    private UserRepresentation userRepresentation(final String userName) throws KeycloakUserAmbiguousException, KeycloakUserNotFoundException {
        List<UserRepresentation> user = client.realm(properties.getRealm()).users().search(userName);
        if (user.size() > 1)    throw new KeycloakUserAmbiguousException(userName);
        if (user.isEmpty())     throw new KeycloakUserNotFoundException(userName);

        return user.get(0);
    }

    private GroupRepresentation group(final String groupName) throws KeycloakGroupAmbiguousException, KeycloakGroupNotFoundException {
        List<GroupRepresentation> group = client.realm(properties.getRealm()).groups().groups();
        if (group.size() > 1)   throw new KeycloakGroupAmbiguousException(groupName);
        if (group.isEmpty())    throw new KeycloakGroupNotFoundException(groupName);

        return group.get(0);
    }


    public boolean leave(final String userName, final String groupName) 
            throws  KeycloakUserAmbiguousException, KeycloakUserNotFoundException, 
                    KeycloakGroupAmbiguousException, KeycloakGroupNotFoundException {
        return user(userName).groups().remove(group(groupName));
    }
}
