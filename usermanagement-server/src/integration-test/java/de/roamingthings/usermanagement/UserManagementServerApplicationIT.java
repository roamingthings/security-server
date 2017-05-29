package de.roamingthings.usermanagement;

import de.roamingthings.test.spring.SystemPropertyActiveProfileResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(resolver = SystemPropertyActiveProfileResolver.class)
public class UserManagementServerApplicationIT {

    @Test
    public void should_load_application_context() {

    }
}
