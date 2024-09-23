package de.paladinsinn.tp.dcis.players.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder(toBuilder = true, setterPrefix = "")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(includeFieldNames = true)
@EqualsAndHashCode(of = {"id"})
public class PlayerLogEntryImpl implements PlayerLogEntry {
    private UUID id;
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private OffsetDateTime deleted;
    
    private Player player;

    private String system;
    private String text;
}
