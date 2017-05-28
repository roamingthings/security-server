package de.roamingthings.auth.workbench;

import de.roamingthings.SystemPropertyActiveProfileResolver;
import de.roamingthings.auth.main.AuthServerApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(resolver = SystemPropertyActiveProfileResolver.class)
public class OAuthTokenWorkbench {
    private static final String HOST = "http://localhost";

    @Value("${local.server.port}")
    private int port;

    private String baseURI;

    @Before
    public void setup() {
        baseURI = HOST + ":" + port;
    }

    @Test
    public void should_obtain_token() throws Exception {
        given().log().all()
                .auth()
                .basic("fooClientIdPassword", "secret")
                .contentType("application/x-www-form-urlencoded; charset=utf-8")
            .when()
                .body("grant_type=password&username=user&password=simple")
                .post(baseURI + "/oauth/token").prettyPeek()
            .then()
                .statusCode(200)
            .and()
                .body("access_token", notNullValue())
                .body("refresh_token", notNullValue())
                .body("user_uuid", is("7aa9080f-eb9a-4499-ae60-1876d97f8dc0"))
                .body("scope", is("foo read write"))
        ;
    }

    @Test
    public void should_reject_token_request_for_unknown_client_id() throws Exception {
        given().log().all()
                .auth()
                .basic("nonExistingClient", "secret")
                .contentType("application/x-www-form-urlencoded; charset=utf-8")
            .when()
                .body("grant_type=password&username=user&password=simple")
                .post(baseURI + "/oauth/token").prettyPeek()
            .then()
                .statusCode(401)
            .and()
                .body("status", is(401))
                .body("error", is("Unauthorized"))
                .body("path", is("/oauth/token"))
                .body("message", is("Bad credentials"))
        ;
    }
}
