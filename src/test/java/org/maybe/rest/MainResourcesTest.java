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
    void test1()
    {
        testOperation("/main-service/v1", 3);
    }

    @Test
    void test2()
    {
        testOperation("/main-service/v2", 3);
    }

    @Test
    void test3()
    {
        testOperation("/main-service/v3", 3);
    }

    private void testOperation(String uri, Integer expected)
    {
        Response resp = given()
                .when()
                .header("Authorization", "auth-token")
                .get(uri)
                .then().extract().response();

        MatcherAssert.assertThat(resp.statusCode() + " " + resp.asString(), Matchers.is("200 final result is " + expected));
    }
}