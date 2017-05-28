package de.roamingthings.auth.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/04/23
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("de.roamingthings.auth")
@EnableJpaAuditing
public class PersistenceConfig {
}
