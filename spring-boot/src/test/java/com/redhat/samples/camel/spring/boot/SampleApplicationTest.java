package com.redhat.samples.camel.spring.boot;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Ignore
public class SampleApplicationTest {

    @Test
    public void hello() {
        // @formatter:off
        given()
            .baseUri("http://localhost:8080")
            .param("name","Camel")
        .when()
            .get("/hello")
        .then()
            .body( is("Hello, Camel!"));
        // @formatter:on
    }

}
