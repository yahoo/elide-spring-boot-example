/*
 * Copyright 2019, Verizon Media.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package example;

import com.yahoo.elide.core.exceptions.HttpStatus;
import com.yahoo.elide.test.graphql.GraphQLDSL;
import com.yahoo.elide.spring.controllers.JsonApiController;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import javax.ws.rs.core.MediaType;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
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
            .get("/api/v1/group")
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
            .contentType(JsonApiController.JSON_API_CONTENT_TYPE)
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
                .patch("/api/v1/group/com.example.repository")
            .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        when()
                .get("/api/v1/group")
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
                .contentType(JsonApiController.JSON_API_CONTENT_TYPE)
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
                .post("/api/v1/group")
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
            .delete("/api/v1/group/com.example.repository")
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
            .contentType(JsonApiController.JSON_API_CONTENT_TYPE)
            .body(datum(
                linkage(type("product"), id("foo"))
            ))
        .when()
                .delete("/api/v1/group/com.example.repository")
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
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
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
            .post("/graphql/api/v1")
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
    void swaggerDocumentTest() {
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
    public void testAsyncApiEndPoint() throws Exception {
        given()
                .when()
                .get("/api/v1/asyncQuery")
                .then()
                .statusCode(200);
    }

    @Test
    public void testDownloadAPI() throws Exception {
        given()
                .when()
                .get("/api/v1/downloads?fields[downloads]=downloads,group,product")
                .then()
                .statusCode(200);
    }
}
