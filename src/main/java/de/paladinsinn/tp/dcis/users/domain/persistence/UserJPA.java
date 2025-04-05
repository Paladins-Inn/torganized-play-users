package de.paladinsinn.tp.dcis.users.domain.persistence;

import java.time.OffsetDateTime;
import java.time.Period;
import java.util.UUID;

import de.kaiserpfalzedv.commons.jpa.AbstractRevisionedJPAEntity;
import de.paladinsinn.tp.dcis.users.domain.model.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * The player
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(
    name = "USERS",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ID"}),
        @UniqueConstraint(columnNames = {"NAMESPACE", "NAME"})
    }
)
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
public class UserJPA extends AbstractRevisionedJPAEntity<UUID> implements User {
    @Nullable
    @Column(name = "DETAINED_PERIOD")
    private Period detainedPeriod;
    
    @Nullable
    @Column(name = "DETAINED_TILL")
    private OffsetDateTime detainedTill;
    
    @NotNull
    @Column(name = "BANNED", nullable = false)
    @Builder.Default
    private boolean banned = false;
    
    /** The namespace this player is registered for. */
    @NotNull
    @Column(name = "NAMESPACE", columnDefinition = "VARCHAR(100)", nullable = false, updatable = false)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String nameSpace;

    /** The name of the player. Needs to be unique within the namespace. */
    @NotNull
    @Column(name = "NAME", columnDefinition = "VARCHAR(100)", nullable = false)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String name;
}
