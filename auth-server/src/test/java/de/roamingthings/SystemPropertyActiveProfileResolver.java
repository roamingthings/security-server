package de.roamingthings;

import org.springframework.test.context.ActiveProfilesResolver;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/04/25
 */
public class SystemPropertyActiveProfileResolver implements ActiveProfilesResolver {

    @Override
    public String[] resolve(final Class<?> aClass) {
        final String activeProfile = System.getProperty("spring.profiles.active");
        return new String[] { activeProfile == null ? "it" : activeProfile };
    }
}
