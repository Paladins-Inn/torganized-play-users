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
import org.keycloak.admin.client.resource.GroupResource;
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
public class KeycloakGroupManagement {
    private Keycloak client;
    private KeycloakConfigurationProperties properties;

    public void join(String userName, String role) throws KeycloakUserAmbiguousException, UserNotFoundException {
        UserRepresentation user = user(userName);
        GroupResource group = client.realm(properties.getRealm()).groups().group(role);

        // TODO 2024-10-10 klenkes74 Implement Group Joining to Keycloak.
    }

    public UserRepresentation user(final String userName) throws KeycloakUserAmbiguousException, UserNotFoundException {
        List<UserRepresentation> user = client.realm(properties.getRealm()).users().search(userName);
        if (user.size() > 1)    throw new KeycloakUserAmbiguousException(userName);
        if (user.isEmpty())     throw new UserNotFoundException(userName);

        return user.get(0);
    }

    /*
     * PUT /{realm}/users/{userId}/groups/{groupId}
     * {
     * "realm":"{realm}",
     * "userId":"{userId}",
     * "groupId":"{groupId}"
     * }
     */
    void joinUserInGroup(String userId, String groupId) {}

    /*
     * DELETE /{realm}/users/{userId}/groups/{groupId}
     */
    void leaveGroup(String userId, String groupId) {}
}
