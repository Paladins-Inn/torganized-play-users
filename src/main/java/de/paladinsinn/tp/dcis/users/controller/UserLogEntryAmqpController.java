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
package de.paladinsinn.tp.dcis.users.controller;



import de.paladinsinn.tp.dcis.users.domain.services.UserLogService;
import lombok.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import de.paladinsinn.tp.dcis.users.domain.model.User;
import de.paladinsinn.tp.dcis.users.domain.model.UserLogEntry;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-11-01
 */
@Service
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Getter
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Slf4j
public class UserLogEntryAmqpController {
  private final UserLogService service;


  @RabbitListener(queues = {"dcis.users.log"}, autoStartup = "true")
  public void receiveLoggingEvent(@Payload UserLogEntry entry, @Header("amqp_consumerQueue") String queue) {
    log.debug("Received a message. queue='{}', message={}", queue, entry);

    service.log(entry.getUser(), entry.getSystem(), entry.getText());
  }
}
