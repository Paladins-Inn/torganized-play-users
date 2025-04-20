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

package de.paladinsinn.tp.dcis.users;

import de.paladinsinn.tp.dcis.lib.messaging.events.EnableEventBus;
import de.paladinsinn.tp.dcis.lib.rest.EnableRestConfiguration;
import de.paladinsinn.tp.dcis.lib.ui.EnableWebUiConfiguration;
import de.paladinsinn.tp.dcis.users.store.EnableUserStore;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Roland T. Lichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-06-09
 */
@SpringBootApplication(scanBasePackages = {
    "de.paladinsinn.tp.dcis.users.controller",
    "de.paladinsinn.tp.dcis.users.domain.service"
})
@EnableEventBus
@EnableUserStore
@EnableRestConfiguration
@EnableWebUiConfiguration
@EnableJpaRepositories(basePackages = {"de.paladinsinn.tp.dcis.users.domain.model"})
@EntityScan(basePackages = {"de.paladinsinn.tp.dcis.users.domain.model"})
@EnableJpaAuditing
@EnableEnversRepositories
@EnableTransactionManagement
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class Application extends SpringApplication {
    @Value("${spring.application.name:PLAYERS}")
    @Getter(onMethod = @__(@Bean))
    private String applicationName;

    public static void main(String [] args) {
        SpringApplication.run(Application.class, args);
    }
}
