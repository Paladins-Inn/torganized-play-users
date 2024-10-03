package de.paladinsinn.tp.dcis.users.domain.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository extends JpaRepository<UserLogEntryJPA, UUID> {
    List<UserLogEntryJPA> findByUser(UserJPA player);
    Page<UserLogEntryJPA> findByUser(UserJPA player, Pageable pageable);

    List<UserLogEntryJPA> findByUser_NameSpaceAndName(final String nameSpace, final String name);
    Page<UserLogEntryJPA> findByUser_NameSpaceAndName(final String nameSpace, final String name, Pageable pageable);
    
    List<UserLogEntryJPA> findByUser_Id(final UUID uid);
    Page<UserLogEntryJPA> findByUser_Id(final UUID uid, Pageable pageable);
}
