package de.roamingthings.auth.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/04/10
 */
@AuthServerModules
@SpringBootApplication
//@EnableAutoConfiguration
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

}
