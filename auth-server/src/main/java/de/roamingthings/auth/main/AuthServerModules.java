package de.roamingthings.auth.main;

import de.roamingthings.auth.health.HealthModule;
import de.roamingthings.auth.useraccount.UserAccountModule;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/25
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan(basePackageClasses = {AuthServerModules.class}, excludeFilters = @ComponentScan.Filter(AuthServerModules.class))
@Configuration

@Import({
        UserAccountModule.class,
        HealthModule.class,
})
public @interface AuthServerModules {
}
