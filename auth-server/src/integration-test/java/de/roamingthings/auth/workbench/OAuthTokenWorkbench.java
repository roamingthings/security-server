package de.roamingthings.auth.workbench;

import de.roamingthings.SystemPropertyActiveProfileResolver;
import de.roamingthings.auth.AuthServerApplication;
import de.roamingthings.net.UrlQueryParser;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.*;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(resolver = SystemPropertyActiveProfileResolver.class)
public class OAuthTokenWorkbench {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthTokenWorkbench.class);

    private static final String HOST = "http://localhost";

    @Value("${local.server.port}")
    private int port;

    private String baseURI;

    private String clientId;
    private String scope ="read";
    private String redirectUri = "http://localhost/redirected";
    private String state = "state123";

    @Before
    public void setup() {
        baseURI = HOST + ":" + port;
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

    @Test
    public void should_obtain_token_with_code_grant() throws Exception {
        final ExtractableResponse<Response> initialAuthorizationRequestResponse = requestAuthorizationWithExpectedRedirectLocation("/login", "code");
        final String jSessionId = getJSessionId(initialAuthorizationRequestResponse);

        assertThat(jSessionId, CoreMatchers.notNullValue());

        final ExtractableResponse<Response> loginFormResponse = submitFormBasedLoginWithHttpSession(jSessionId);

        final String authorizedAuthorizeUri = getLocationFromResponse(loginFormResponse);
        assertThat(authorizedAuthorizeUri, containsString("/oauth/authorize"));

        final String loggedInSessionId = getJSessionId(loginFormResponse);
        assertThat(loggedInSessionId, not(jSessionId));

        final ExtractableResponse<Response> authorized2Response =
                requestAuthorizationWithExpectedRedirectLocation(loggedInSessionId, "http://localhost/redirected", "code");

        final String tokenLocation = getLocationFromResponse(authorized2Response);
        final String code = getAuthorizationCodeFromUri(tokenLocation);

        final String grantRequestBody =
                "grant_type=authorization_code" +
                        "&code=" + code +
                        "&redirect_uri=" + URLEncoder.encode("http://localhost/redirected", "utf-8");
        final String access_token = retrieveTokenFromOAuth2Endpoint(grantRequestBody, "read");

        assertThat(access_token, notNullValue());
    }

    @Test
    public void should_obtain_token_with_implicit_grant() throws Exception {
        final ExtractableResponse<Response> initialAuthorizationRequestResponse = requestAuthorizationWithExpectedRedirectLocation("/login", "token");
        final String jSessionId = getJSessionId(initialAuthorizationRequestResponse);

        assertThat(jSessionId, CoreMatchers.notNullValue());

        final ExtractableResponse<Response> loginFormResponse = submitFormBasedLoginWithHttpSession(jSessionId);

        final String authorizedAuthorizeUri = getLocationFromResponse(loginFormResponse);
        assertThat(authorizedAuthorizeUri, containsString("/oauth/authorize"));

        final String loggedInSessionId = getJSessionId(loginFormResponse);
        assertThat(loggedInSessionId, not(jSessionId));

        final ExtractableResponse<Response> authorized2Response =
                requestAuthorizationWithExpectedRedirectLocation(loggedInSessionId, "http://localhost/redirected", "token");

        final String accessTokenRedirectLocation = getLocationFromResponse(authorized2Response);

        final String access_token = getAccessTokenFromResponse(accessTokenRedirectLocation);

        assertThat(access_token, notNullValue());
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

    private String getAuthorizationCodeFromUri(String uri) throws MalformedURLException {
        final URL tokenUrl = new URL(uri);
        final Map<String, List<String>> parameters = UrlQueryParser.extractParametersFromUrl(tokenUrl);
        assertThat(parameters.containsKey("code"), is(true));
        return parameters.get("code").get(0);
    }

    private String getAccessTokenFromResponse(String uri) throws MalformedURLException, URISyntaxException {
        final URI tokenUri = new URI(uri);
        final String tokenFragment = tokenUri.getFragment();
        final Map<String, List<String>> parameters = UrlQueryParser.extractParametersFromFormEncodedParameterList(tokenFragment);
        assertThat(parameters.containsKey("access_token"), is(true));
        return parameters.get("access_token").get(0);
    }

    private static String getLocationFromResponse(ExtractableResponse<Response> response) {
        return response.header("Location");
    }

    private static String getJSessionId(ExtractableResponse<Response> response) {
        return response.cookie("JSESSIONID");
    }


    private ExtractableResponse<Response> submitFormBasedLoginWithHttpSession(String jSessionId) {
        // @formatter:off
        return given().log().all()
            .when()
                .redirects().follow(false)
                .formParam("username", "user")
                .formParam("password", "simple")
                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                .cookie("JSESSIONID", jSessionId)
                .post(baseURI + "/login").prettyPeek()
            .then()
                .statusCode(302)
            .and()
                .extract();
        // @formatter:on
    }

    private ExtractableResponse<Response> requestAuthorizationWithExpectedRedirectLocation(String expectedRedirectUri, String responseType) {
        return requestAuthorizationWithExpectedRedirectLocation(null, expectedRedirectUri, responseType);
    }

    private ExtractableResponse<Response> requestAuthorizationWithExpectedRedirectLocation(String jSessionId, String expectedRedirectUri, String responseType) {

        // @formatter:off
        clientId = "fullClient";final ExtractableResponse<Response> redirectedResponse =
            given().log().all()
            .auth()
                .basic("fullClient", "secret")
            .and()
                .contentType("application/x-www-form-urlencoded; charset=utf-8")
            .when()
                .redirects().follow(false)
                .cookie("JSESSIONID", jSessionId)
                .queryParam("client_id", clientId)
                .queryParam("scope", scope)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .queryParam("response_type", responseType)
                .get(baseURI + "/oauth/authorize").prettyPeek()
            .then()
                .statusCode(302)
            .and()
                .extract();
        // @formatter:on

        final String redirectUri = getLocationFromResponse(redirectedResponse);
        assertThat(redirectUri, containsString(expectedRedirectUri));

        return redirectedResponse;
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
