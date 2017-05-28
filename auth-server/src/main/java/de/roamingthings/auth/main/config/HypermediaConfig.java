package de.roamingthings.auth.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/04/17
 */
@Configuration
@EnableHypermediaSupport(type= {HypermediaType.HAL})
public class HypermediaConfig {
}