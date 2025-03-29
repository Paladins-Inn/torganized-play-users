package de.paladinsinn.tp.dcis.users;

import de.paladinsinn.tp.dcis.commons.events.EnableEventBus;
import de.paladinsinn.tp.dcis.commons.rest.EnableRestConfiguration;
import de.paladinsinn.tp.dcis.users.domain.services.EnableUserLogEntryClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Roland T. Lichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-06-09
 */
@SpringBootApplication
@EnableEventBus
@EnableUserLogEntryClient
@EnableRestConfiguration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Application extends SpringApplication {
    @Value("${spring.application.name:PLAYERS}")
    @Getter(onMethod = @__(@Bean))
    private final String applicationName;

    public static void main(String [] args) {
        SpringApplication.run(Application.class, args);
    }
}
