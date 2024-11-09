package de.paladinsinn.tp.dcis.users;

import de.paladinsinn.tp.dcis.commons.configuration.EnableDefaultConfiguration;
import de.paladinsinn.tp.dcis.commons.configuration.EnableMessagingConfiguration;
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
@EnableDefaultConfiguration
@EnableMessagingConfiguration
public class Application extends SpringApplication {
    @Value("${spring.application.name:PLAYERS}")
    @Getter(onMethod = @__(@Bean))
    private String applicationName;

    public static void main(String [] args) {
        SpringApplication.run(Application.class, args);
    }

}
