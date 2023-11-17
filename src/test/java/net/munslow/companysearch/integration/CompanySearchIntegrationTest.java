package net.munslow.companysearch.integration;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;

class CompanySearchIntegrationTest extends AbstractIntegrationTest {

    @Test
    void givenACompanyNumberWhenSearchIsPerformedThenSearchReturnsCompanyDetails() {
        stubFor(
                get(urlPathEqualTo("/Search"))
                        .withHeader("x-api-key", equalTo("test-api-key"))
                        .withQueryParam("Query", equalTo("123456"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("single-company-response.json")));

        stubFor(
                get(urlPathEqualTo("/Officers"))
                        .withHeader("x-api-key", equalTo("test-api-key"))
                        .withQueryParam("CompanyNumber", equalTo("111111"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("officers-response-111111.json")));

        String expectedResponse = readFile("company-number-search-response.json");

        var jsonResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(
                                """
                                        {
                                            "companyNumber" : "123456"
                                        }
                                        """)
                        .when()
                        .post("/")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .extract()
                        .response()
                        .asString();

        assertThatJson(jsonResponse).when(IGNORING_ARRAY_ORDER).isEqualTo(expectedResponse);
    }

    @Test
    void givenACompanyNameWhenSearchIsPerformedThenSearchReturnsSearchResults() {
        stubFor(
                get(urlPathEqualTo("/Search"))
                        .withHeader("x-api-key", equalTo("test-api-key"))
                        .withQueryParam("Query", equalTo("ACME LIMITED"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("multiple-company-response.json")));

        stubFor(
                get(urlPathEqualTo("/Officers"))
                        .withHeader("x-api-key", equalTo("test-api-key"))
                        .withQueryParam("CompanyNumber", equalTo("111111"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("officers-response-111111.json")));

        stubFor(
                get(urlPathEqualTo("/Officers"))
                        .withHeader("x-api-key", equalTo("test-api-key"))
                        .withQueryParam("CompanyNumber", equalTo("222222"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("officers-response-222222.json")));

        String expectedResponse = readFile("company-name-search-response.json");

        var jsonResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(
                                """
                                        {
                                            "companyName" : "ACME LIMITED"
                                        }
                                        """)
                        .when()
                        .post("/")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .extract()
                        .response()
                        .asString();

        assertThatJson(jsonResponse).when(IGNORING_ARRAY_ORDER).isEqualTo(expectedResponse);
    }

    @Test
    void givenACompanyNumberWhenSearchIsPerformedAndAnOfficerResignedTheResponseDoesNotContainTheResignedOfficer() {
        stubFor(
                get(urlPathEqualTo("/Search"))
                        .withHeader("x-api-key", equalTo("test-api-key"))
                        .withQueryParam("Query", equalTo("123456"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("single-company-response.json")));

        stubFor(
                get(urlPathEqualTo("/Officers"))
                        .withHeader("x-api-key", equalTo("test-api-key"))
                        .withQueryParam("CompanyNumber", equalTo("111111"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("officers-response-with-resignation-111111.json")));

        String expectedResponse = readFile("company-number-search-response.json");

        var jsonResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(
                                """
                                        {
                                            "companyNumber" : "123456"
                                        }
                                        """)
                        .when()
                        .post("/")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .extract()
                        .response()
                        .asString();

        assertThatJson(jsonResponse).when(IGNORING_ARRAY_ORDER).isEqualTo(expectedResponse);
    }

    @Test
    void givenACompanyNumberWhenSearchIsPerformedMultipleTimesDatabaseIsUsedAfterFirstCall() {
        stubFor(
                get(urlPathEqualTo("/Search"))
                        .withHeader("x-api-key", equalTo("test-api-key"))
                        .withQueryParam("Query", equalTo("123456"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("single-company-response.json")));

        stubFor(
                get(urlPathEqualTo("/Officers"))
                        .withHeader("x-api-key", equalTo("test-api-key"))
                        .withQueryParam("CompanyNumber", equalTo("111111"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("officers-response-111111.json")));

        String expectedResponse = readFile("company-number-search-response.json");

        var jsonResponse1 = performCompanyNumberSearch("123456");

        assertThatJson(jsonResponse1).when(IGNORING_ARRAY_ORDER).isEqualTo(expectedResponse);

        verify(1, getRequestedFor(urlPathEqualTo("/Search")));

        var jsonResponse2 = performCompanyNumberSearch("123456");

        assertThatJson(jsonResponse2).when(IGNORING_ARRAY_ORDER).isEqualTo(expectedResponse);

        verify(1, getRequestedFor(urlPathEqualTo("/Search")));
    }

    String performCompanyNumberSearch(String companyNumber) {
        var request = String.format(companyNumberRequest, companyNumber);
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response()
                .asString();
    }
}
