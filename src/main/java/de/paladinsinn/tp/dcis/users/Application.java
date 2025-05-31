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

import de.kaiserpfalzedv.commons.users.messaging.EnableUsersMessaging;
import de.kaiserpfalzedv.commons.users.store.EnableUsersStore;
import de.paladinsinn.tp.dcis.users.controller.UserController;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Roland T. Lichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-06-09
 */
@SpringBootApplication(scanBasePackageClasses = {
    UserController.class,
})
@EnableJpaAuditing
@EnableTransactionManagement
@EnableUsersMessaging
@EnableUsersStore
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class Application extends SpringApplication {
    @Value("${spring.application.name:PLAYERS}")
    @Getter(onMethod = @__(@Bean))
    private String applicationName;

    public static void main(String [] args) {
        log.entry(Arrays.stream(args).collect(Collectors.toList()));
        
        log.exit(SpringApplication.run(Application.class, args));
    }
}
