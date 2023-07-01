/*
 * Copyright 2021, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */

package example;

import static io.restassured.RestAssured.given;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;


/**
 * Example functional test for optional features.
 */
@TestPropertySource(
        properties = {
                "elide.async.enabled=true"
        }
)
public class ExampleOptionalFeaturesTest extends IntegrationTest {

        @Test
        public void testAsyncApiEndPoint() throws Exception {
                given()
                        .when()
                        .get("/api/asyncQuery")
                        .then()
                        .statusCode(200);
        }
}
