package de.paladinsinn.tp.dcis.players;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Roland T. Lichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-06-09
 */
@SpringBootApplication
public class Application extends SpringApplication {
    public static void main(String [] args) {
        SpringApplication.run(Application.class, args);
    }
}
