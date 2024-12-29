package de.paladinsinn.tp.dcis.users;

import de.paladinsinn.tp.dcis.commons.events.EnableEventBus;
import de.paladinsinn.tp.dcis.commons.messaging.EnableMessagingConfiguration;
import de.paladinsinn.tp.dcis.commons.rest.EnableRestConfiguration;
import de.paladinsinn.tp.dcis.users.controller.UserLogEntryAmqpController;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.Getter;

/**
 * @author Roland T. Lichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-06-09
 */
@SpringBootApplication
@EnableEventBus
@EnableRestConfiguration
@EnableMessagingConfiguration
public class Application extends SpringApplication {
    @Value("${spring.application.name:PLAYERS}")
    @Getter(onMethod = @__(@Bean))
    private String applicationName;

    @SuppressWarnings("unused") // only to start the AMQP listener up
    @Autowired
    private UserLogEntryAmqpController userLogEntryAmqpController;

    public static void main(String [] args) {
        SpringApplication.run(Application.class, args);
    }
}
