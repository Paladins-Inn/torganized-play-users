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

import java.util.List;
import java.util.UUID;

import de.paladinsinn.tp.dcis.domain.users.persistence.UserJPA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository extends JpaRepository<UserLogEntryJPA, UUID> {
    List<UserLogEntryJPA> findByUser(UserJPA player);
    Page<UserLogEntryJPA> findByUser(UserJPA player, Pageable pageable);
    
    List<UserLogEntryJPA> findByUser_Id(UUID player);
    Page<UserLogEntryJPA> findByUser_Id(UUID player, Pageable pageable);
}
