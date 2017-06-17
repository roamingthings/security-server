package de.roamingthings.auth.workbench;

import de.roamingthings.SystemPropertyActiveProfileResolver;
import de.roamingthings.auth.AuthServerApplication;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/25
 */
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = BEFORE_CLASS)
@SpringBootTest(classes = AuthServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(resolver = SystemPropertyActiveProfileResolver.class)
public class OAuth2PasswordGrantIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2PasswordGrantIT.class);

    private static final String HOST = "http://localhost";

    @Value("${local.server.port}")
    private int port;

    private String baseURI;

    @Before
    public void setup() {
        baseURI = HOST + ":" + port;

        RestAssured.reset();
    }

    @Test
    public void should_obtain_token_with_password_grant() throws Exception {
        final String grantRequestBody =
                "grant_type=password" +
                        "&username=user" +
                        "&password=simple";

        final String accessToken = retrieveTokenFromOAuth2Endpoint(grantRequestBody, "read write integration");

        assertThat(accessToken, notNullValue());
    }

    private String retrieveTokenFromOAuth2Endpoint(String grantRequestBody, String expectedScope) {
        // @formatter:off
        final ExtractableResponse<Response> tokenResponse =
            given().log().all()
                    .auth().basic("fullClient", "secret")
                    .contentType("application/x-www-form-urlencoded; charset=utf-8")
                .when()
                    .body(grantRequestBody)
                    .post(baseURI + "/oauth/token").prettyPeek()
                .then()
                    .statusCode(200)
                    .body("access_token", notNullValue())
                    .body("refresh_token", notNullValue())
                    .body("user_uuid", is("7aa9080f-eb9a-4499-ae60-1876d97f8dc0"))
                    .body("scope", is(expectedScope))
                .extract();
        // @formatter:on

        return tokenResponse.body().path("access_token");
    }

}
