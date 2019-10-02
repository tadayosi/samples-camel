package com.redhat.samples.camel.spring.boot;

import io.restassured.RestAssured;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.*;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "camel.springboot.java-routes-include-pattern=**/Rest*"
    })
public class RestRouteTest {

    @Test
    public void outMessageNullBody() {
        // @formatter:off
        RestAssured
            .given()
                .baseUri("http://localhost:8080/camel")
            .when()
                .get("/outMessageNullBody")
            .then()
                .body(isEmptyOrNullString());
        // @formatter:on
    }

}
