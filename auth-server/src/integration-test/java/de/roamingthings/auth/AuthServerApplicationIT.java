package de.roamingthings.auth;

import de.roamingthings.SystemPropertyActiveProfileResolver;
import de.roamingthings.auth.main.AuthServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthServerApplication.class)
@ActiveProfiles(resolver = SystemPropertyActiveProfileResolver.class)
public class AuthServerApplicationIT {

	@Test
	public void should_load_application_context() {
	}

}
