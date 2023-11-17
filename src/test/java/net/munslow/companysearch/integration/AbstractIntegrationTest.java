package net.munslow.companysearch.integration;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import wiremock.org.apache.commons.io.IOUtils;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {
    String companyNumberRequest = """
             {
                 "companyNumber" : "%s"
             }
            """;

    @LocalServerPort
    private int port;

    String baseUri = "http://localhost";

    @BeforeAll
    static void initGlobal() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @BeforeEach
    void init() {
        RestAssured.baseURI = baseUri;
        RestAssured.port = port;
    }

    @SneakyThrows
    String readFile(String path) {
        return IOUtils.toString(this.getClass().getResourceAsStream("/fixtures/" + path), "UTF-8");
    }
}
