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

import java.util.UUID;

import de.kaiserpfalzedv.commons.jpa.AbstractJPAEntity;
import de.paladinsinn.tp.dcis.domain.users.model.UserLogEntry;
import de.paladinsinn.tp.dcis.domain.users.persistence.UserJPA;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * The player action log.
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0
 */
//@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(
    name = "PLAYERLOG",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ID"}),
    }
)
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
public class UserLogEntryJPA extends AbstractJPAEntity<UUID> implements UserLogEntry {
    @NotNull
    @ManyToOne
    @JoinColumn(name = "PLAYER", columnDefinition = "UUID", nullable = false, updatable = false)
    @ToString.Include
    private UserJPA user;

    @NotNull
    @Column(name = "SYSTEM", columnDefinition = "VARCHAR(100)", nullable = false, updatable = false)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") 
    @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    private String system;

    @NotNull
    @Column(name = "ENTRY", columnDefinition = "VARCHAR(1000)", nullable = false, updatable = false)
    @Size(min = 3, max = 1000, message = "The length of the string must be between 3 and 100 characters long.") 
    private String text;
    
    @Nullable
    @Column(name = "COMMENTS", columnDefinition = "VARCHAR(1000)", updatable = false)
    private String comment;

    /**
     * Will prevent the updating of the data set.
     */
    @PreUpdate
    public void thisDataIsNotUpdatable() {
        throw new UnsupportedOperationException("This data set is immutable.");
    }
}
