package de.paladinsinn.tp.dcis.players.domain.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerLogRepository extends JpaRepository<PlayerLogEntryJPA, UUID> {
    List<PlayerLogEntryJPA> findByPlayer(PlayerJPA player);
    Page<PlayerLogEntryJPA> findByPlayer(PlayerJPA player, Pageable pageable);

    List<PlayerLogEntryJPA> findByPlayer_NameSpaceAndName(final String nameSpace, final String name);
    Page<PlayerLogEntryJPA> findByPlayer_NameSpaceAndName(final String nameSpace, final String name, Pageable pageable);
    
    List<PlayerLogEntryJPA> findByPlayer_Id(final UUID uid);
    Page<PlayerLogEntryJPA> findByPlayer_Id(final UUID uid, Pageable pageable);
}