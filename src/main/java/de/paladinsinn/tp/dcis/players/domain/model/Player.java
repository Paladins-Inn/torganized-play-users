package de.paladinsinn.tp.dcis.players.domain.model;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasName;
import de.kaiserpfalzedv.commons.api.resources.HasNameSpace;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * The player
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0
 */
@Entity
@Table(
    name = "PLAYERS",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ID"}),
        @UniqueConstraint(columnNames = {"UID"}),
        @UniqueConstraint(columnNames = {"NAMESPACE", "NAME"})
    }
)
@Jacksonized
@Builder(toBuilder = true, setterPrefix = "")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(of = {"uid"})
public class Player implements HasId, HasNameSpace, HasName {
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

    /** Last modification to this data set. */
    @Default
    @NotNull
    @Column(name = "MODIFIED", columnDefinition = "TIMESTAMP WITH TIME ZONE", unique = false, nullable = false, insertable = true, updatable = true)
    @ToString.Include
    private OffsetDateTime modified = OffsetDateTime.now(ZoneId.of("UTC"));

    /** Deletion date of this data set. */
    @Nullable
    @Column(name = "DELETED", columnDefinition = "TIMESTAMP WITH TIME ZONE", unique = false, nullable = true, insertable = false, updatable = true)
    @ToString.Include
    private OffsetDateTime deleted;

    /** The namespace this player is registered for. */
    @NotNull
    @Column(name = "NAMESPACE", columnDefinition = "VARCHAR(100)", unique = false, nullable = false, insertable = true, updatable = false)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String nameSpace;

    /** The name of the player. Needs to be unique within the namespace. */
    @NotNull
    @Column(name = "NAME", columnDefinition = "VARCHAR(100)", unique = false, nullable = false, insertable = true, updatable = true)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String name;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Default
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PUBLIC)
    private List<PlayerLogEntry> logs = new LinkedList<>();

    public boolean addLogEntry(@NotNull final String system, @NotNull final String entry) {
        return logs.add(PlayerLogEntry.builder().player(this).system(system).text(entry).build());
    }
}
