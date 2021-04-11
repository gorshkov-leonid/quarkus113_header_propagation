package org.maybe.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@QuarkusTest
public class MaybeResourcesTest
{
    private static Stream<Arguments> successfulCases()
    {
        return newArrayList(
                arguments("v1-ok", true, "yes"),
                arguments("v1-ok", false, "no"),
                arguments("v1-fail", true, "yes")/* fail */,
                arguments("v1-wa", true, "yes")/* fix in 1.13 */,
                arguments("v1-wa", false, "no")/* fix in 1.13 */,

                arguments("v2-ok", true, "yes"),
                arguments("v2-ok", false, "no"),
                arguments("v2-fail", true, "yes")/* fail in 1.13 */,
                arguments("v2-fail", false, "no")/* fail in 1.13 */,
                arguments("v2-wa1", true, "yes")/* fix */,
                arguments("v2-wa1", false, "no")/* fix */,
                arguments("v2-wa2", true, "yes")/* fix */,
                arguments("v2-wa2", false, "no")/* fix */,

                arguments("v3-fail", true, "yes")/* fail in 1.13 */,
                arguments("v3-fail", false, "no")/* fail in 1.13 */,
                arguments("v3-wa", true, "yes")/* fix */,
                arguments("v3-wa", false, "no")/* fix  */
        ).stream();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("successfulCases")
    public void testSuccess(String testCase, boolean sendHeader, String expectedBody)
    {
        RequestSpecification reqSpec = given().when();
        if (sendHeader)
        {
            reqSpec = reqSpec.header("Authorization", "auth-token-42");
        }
        reqSpec
                .get("/" + testCase + "/maybe")
                .then()
                .statusCode(200)
                .body(is(expectedBody));
    }
}