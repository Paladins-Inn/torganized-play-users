package de.paladinsinn.tp.dcis.players.domain.model;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * The player
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0
 */
@Entity
@Table(
    name = "PLAYERLOG",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ID"}),
        @UniqueConstraint(columnNames = {"UID"}),
    }
)
@Jacksonized
@Builder(toBuilder = true, setterPrefix = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(of = {"uid"})
public class PlayerLogEntry implements HasId {
    /** The Database ID of the players account. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", columnDefinition = "BIGINT", unique = true, nullable = false, insertable = true, updatable = false)
    @ToString.Include
    private Long id;

    /** The UID of the player. */
    @Default
    @NotNull
    @Column(name = "UID", columnDefinition = "UUID", unique = true, nullable = false, insertable = true, updatable = false)
    @ToString.Include
    private UUID uid = UUID.randomUUID();

    /** Data set creation timestamp. */
    @Default
    @NotNull
    @Column(name = "CREATED", columnDefinition = "TIMESTAMP WITH TIME ZONE", unique = false, nullable = false, insertable = true, updatable = false)
    @ToString.Include
    private OffsetDateTime created = OffsetDateTime.now(ZoneId.of("UTC"));

    @NotNull
    @ManyToOne
    @Column(name = "PLAYER", columnDefinition = "BIGINT", unique = false, nullable = false, insertable = true, updatable = false)
    private Player player;

    @NotNull
    @Column(name = "SYSTEM", columnDefinition = "VARCHAR(100)", unique = false, nullable = false, insertable = true, updatable = true)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") 
    @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    private String system;

    @NotNull
    @Column(name = "ENTRY", columnDefinition = "VARCHAR(1000)", unique = false, nullable = false, insertable = true, updatable = true)
    @Size(min = 3, max = 1000, message = "The length of the string must be between 3 and 100 characters long.") 
    private String text;
}
