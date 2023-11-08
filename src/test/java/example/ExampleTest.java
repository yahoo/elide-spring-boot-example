/*
 * Copyright 2019, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package example;

import com.yahoo.elide.core.exceptions.HttpStatus;
import com.yahoo.elide.datastores.jms.websocket.SubscriptionWebSocketTestClient;
import com.yahoo.elide.jsonapi.JsonApi;
import com.yahoo.elide.test.graphql.GraphQLDSL;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static com.yahoo.elide.test.graphql.GraphQLDSL.field;
import static com.yahoo.elide.test.graphql.GraphQLDSL.query;
import static com.yahoo.elide.test.graphql.GraphQLDSL.selection;
import static com.yahoo.elide.test.graphql.GraphQLDSL.selections;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.attr;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.attributes;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.data;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.datum;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.id;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.linkage;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.relation;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.relationships;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.resource;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.type;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import graphql.ExecutionResult;

import java.net.URI;
import java.util.List;

/**
 * Example functional test.
 */
public class ExampleTest extends IntegrationTest {
    /**
     * This test demonstrates an example test using the JSON-API DSL.
     */
    @Test
    @Sql(statements = {
            "DELETE FROM Downloads; DELETE FROM ArtifactVersion; DELETE FROM ArtifactProduct; DELETE FROM ArtifactGroup;",
            "INSERT INTO ArtifactGroup (name, commonName, description) VALUES\n" +
                    "\t\t('com.example.repository','Example Repository','The code for this project');"
    })
    void jsonApiGetTest() {
        when()
            .get("/api/group")
            .then()
            .log().all()
            .body(equalTo(
                data(
                    resource(
                        type( "group"),
                        id("com.example.repository"),
                        attributes(
                            attr("commonName", "Example Repository"),
                            attr("description", "The code for this project")
                        ),
                        relationships(
                            relation("products")
                        )
                    )
                ).toJSON())
            )
            .log().all()
            .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @Sql(statements = {
            "DELETE FROM Downloads; DELETE FROM ArtifactVersion; DELETE FROM ArtifactProduct; DELETE FROM ArtifactGroup;",
            "INSERT INTO ArtifactGroup (name, commonName, description) VALUES\n" +
                    "\t\t('com.example.repository','Example Repository','The code for this project');"
    })
    void jsonApiPatchTest() {
        given()
            .contentType(JsonApi.MEDIA_TYPE)
            .body(
                datum(
                    resource(
                        type("group"),
                        id("com.example.repository"),
                        attributes(
                            attr("commonName", "Changed It.")
                        )
                    )
                )
            )
            .when()
                .patch("/api/group/com.example.repository")
            .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        when()
                .get("/api/group")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "group"),
                                        id("com.example.repository"),
                                        attributes(
                                                attr("commonName", "Changed It."),
                                                attr("description", "The code for this project")
                                        ),
                                        relationships(
                                                relation("products")
                                        )
                                )
                        ).toJSON())
                )
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @Sql(statements = {
            "DELETE FROM Downloads; DELETE FROM ArtifactVersion; DELETE FROM ArtifactProduct; DELETE FROM ArtifactGroup;"
    })
    void jsonApiPostTest() {
        given()
                .contentType(JsonApi.MEDIA_TYPE)
                .body(
                        datum(
                                resource(
                                        type("group"),
                                        id("com.example.repository"),
                                        attributes(
                                                attr("commonName", "New group.")
                                        )
                                )
                        )
                )
                .when()
                .post("/api/group")
                .then()
                .body(equalTo(datum(
                        resource(
                                type("group"),
                                id("com.example.repository"),
                                attributes(
                                        attr("commonName", "New group."),
                                        attr("description", "")
                                ),
                                relationships(
                                        relation("products")
                                )
                        )
                ).toJSON()))
                .statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    @Sql(statements = {
            "DELETE FROM Downloads; DELETE FROM ArtifactVersion; DELETE FROM ArtifactProduct; DELETE FROM ArtifactGroup;",
            "INSERT INTO ArtifactGroup (name, commonName, description) VALUES\n" +
                    "\t\t('com.example.repository','Example Repository','The code for this project');"
    })
    void jsonApiDeleteTest() {
        when()
            .delete("/api/group/com.example.repository")
        .then()
            .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    @Sql(statements = {
            "DELETE FROM Downloads; DELETE FROM ArtifactVersion; DELETE FROM ArtifactProduct; DELETE FROM ArtifactGroup;",
            "INSERT INTO ArtifactGroup (name, commonName, description) VALUES\n" +
                    "\t\t('com.example.repository','Example Repository','The code for this project');",
            "INSERT INTO ArtifactProduct (name, commonName, description, group_name) VALUES\n" +
                    "\t\t('foo','foo Core','The guts of foo','com.example.repository');"
    })
    void jsonApiDeleteRelationshipTest() {
        given()
            .contentType(JsonApi.MEDIA_TYPE)
            .body(datum(
                linkage(type("product"), id("foo"))
            ))
        .when()
                .delete("/api/group/com.example.repository")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    /**
     * This test demonstrates an example test using the GraphQL DSL.
     */
    @Test
    @Sql(statements = {
            "DELETE FROM Downloads; DELETE FROM ArtifactVersion; DELETE FROM ArtifactProduct; DELETE FROM ArtifactGroup;",
            "INSERT INTO ArtifactGroup (name, commonName, description) VALUES\n" +
                    "\t\t('com.example.repository','Example Repository','The code for this project');",
            "INSERT INTO ArtifactGroup (name, commonName, description) VALUES\n" +
                    "\t\t('com.yahoo.elide','Elide','The magical library powering this project');"
    })
    void graphqlTest() {
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .body("{ \"query\" : \"" + GraphQLDSL.document(
                query(
                    selection(
                        field("group",
                            selections(
                                field("name"),
                                field("commonName"),
                                field("description")
                            )
                        )
                    )
                )
            ).toQuery() + "\" }"
        )
        .when()
            .post("/graphql/api")
            .then()
            .body(equalTo(GraphQLDSL.document(
                selection(
                    field(
                        "group",
                        selections(
                            field("name", "com.example.repository"),
                            field( "commonName", "Example Repository"),
                            field("description", "The code for this project")
                        ),
                        selections(
                            field("name", "com.yahoo.elide"),
                            field( "commonName", "Elide"),
                            field("description", "The magical library powering this project")
                        )
                    )
                )
            ).toResponse()))
            .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testSubscription() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        SubscriptionWebSocketTestClient client = new SubscriptionWebSocketTestClient(1,
                List.of("subscription {group(topic: ADDED) {name}}"));

        try (Session session = container.connectToServer(client, new URI("ws://localhost:" + port + "/subscription"))) {

            //Wait for the socket to be full established.
            client.waitOnSubscribe(10);

            given()
                    .contentType(JsonApi.MEDIA_TYPE)
                    .accept(JsonApi.MEDIA_TYPE)
                    .body(
                            data(
                                    resource(
                                            type("group"),
                                            id("foo"),
                                            attributes(attr("description", "bar"))
                                    )
                            )
                    )
                    .post("/api/group")
                    .then().statusCode(org.apache.http.HttpStatus.SC_CREATED).body("data.id", equalTo("foo"));


            List<ExecutionResult> results = client.waitOnClose(10);
            assertEquals(1, results.size());
        }
    }

    @Test
    void openApiDocumentTest() {
        when()
                .get("/doc")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void landingPageTest() {
        when()
                .get("/index.html")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
    @Test
    public void testDownloadAPI() throws Exception {
        given()
                .when()
                .get("/api/downloads?fields[downloads]=downloads,group,product")
                .then()
                .statusCode(200);
    }
}
