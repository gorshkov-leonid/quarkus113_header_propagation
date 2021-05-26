package org.maybe.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.hamcrest.*;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class MainResourcesTest
{
    @Test
    void test()
    {
        Response resp = given()
                .when()
                .header("Authorization", "auth-token")
                .get("/main-service")
                .then().extract().response();

        MatcherAssert.assertThat(resp.statusCode() + " " + resp.asString(), Matchers.is("200 final result is 3"));
    }
}