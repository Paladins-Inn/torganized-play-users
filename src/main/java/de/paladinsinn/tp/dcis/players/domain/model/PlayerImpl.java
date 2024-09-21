package de.paladinsinn.tp.dcis.players.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder(toBuilder = true, setterPrefix = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(includeFieldNames = true)
@EqualsAndHashCode(of = {"uid"})
public class PlayerImpl implements Player {
    private UUID uid;
    private OffsetDateTime created;
    private OffsetDateTime modified;

    private String nameSpace;
    private String name;
}
