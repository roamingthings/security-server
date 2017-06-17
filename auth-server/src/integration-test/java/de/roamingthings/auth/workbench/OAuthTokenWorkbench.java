package de.roamingthings.auth.workbench;

import de.roamingthings.SystemPropertyActiveProfileResolver;
import de.roamingthings.auth.AuthServerApplication;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/25
 */
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = BEFORE_CLASS)
@SpringBootTest(classes = AuthServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(resolver = SystemPropertyActiveProfileResolver.class)
public class OAuthTokenWorkbench {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthTokenWorkbench.class);

    private static final String HOST = "http://localhost";

    @Value("${local.server.port}")
    private int port;

    private String baseURI;

    @Before
    public void setup() {
        baseURI = HOST + ":" + port;
    }

    @Test
    public void should_reject_token_request_for_unknown_client_id() throws Exception {
        // @formatter:off
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
        // @formatter:on
    }

    public static RequestSpecification givenWithContent(String authToken) {
        RequestSpecification spec = given().spec(new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .build());
        if (authToken != null) {
            return spec.header("Authorization", "Bearer " + authToken);
        } else {
            return spec;
        }
    }

}
