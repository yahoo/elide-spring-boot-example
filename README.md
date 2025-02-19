# Elide Spring Boot Example

An archetype Elide project using Spring Boot.

[![Build Status](https://cd.screwdriver.cd/pipelines/7924/badge)](https://cd.screwdriver.cd/pipelines/7924)

## Background

This project is the sample code for [Elide's Getting Started documentation](https://elide.io/pages/guide/v7/01-start.html).

## Quick Start

### Build and run

#### Java

```shell
mvn clean install
java -jar target/elide-spring-boot-1.0.0.jar
```

#### Native

[Getting Started with GraalVM](https://www.graalvm.org/latest/docs/getting-started/)

```shell
mvn clean -Pnative native:compile
```

### Explore

| Description         | URL
|---------------------|---------------------------------------------
| API Default Version | http://localhost:8080/
| API Version 1       | http://localhost:8080/?path=/v1
| API Version 2       | http://localhost:8080/?path=/v2
| Springdoc           | http://localhost:8080/swagger-ui/index.html

## Customizing

### DataStore

#### Performing operations / actions

This project demonstrates a simple custom `DataStore`, the `OperationDataStore`, that allows exposing operations / actions that do not persist any data, for instance for sending an e-mail. This serves the same purpose as Elide's `NoopDataStore` but uses a Jakarta Bean Validator to validate the entity. This is registered in `ElideConfiguration` using a `DataStoreBuilderCustomizer`.

The operation is implemented by creating a model, `Mail`, with a `LifeCycleHook` that performs the sending of the mail.

This allows exposing the operations through both JSON API and GraphQL.

##### JSON API

`POST /mails`
```json
{
  "data": {
    "type": "mails",
    "attributes": {
      "from": "thomas",
      "to": "henry",
      "content": "Hello world"
    }
  }
}
```

##### GraphQL

```graphql
mutation {
  mails(op: UPSERT, data: {from: "thomas", to: "henry", content: "Hello world"}) {
    edges {
      node {
        id
      }
    }
  }
}
```

#### Integrating with other libraries

The project demonstrates a custom `DataStore`, the `SpringDataDataStore`, that demonstrates how to integrate Elide with other libraries such as Spring Data. This is registered in `ElideConfiguration` using a `DataStoreBuilderCustomizer`.

This defines a `QueryRepository` interface that uses the `JpaSpecificationExecutor`. Implementations of `QueryRepository` such as `ArtifactGroupRepository` will then be registered to the `QueryService` which is used by the `SpringDataDataStore` the retrieve data.

Two resources, the `ArtifactGroupPage` which exposes offset pagination, and the `ArtifactGroupStream` which exposes cursor pagination, are using the `SpringDataDataStore`.

##### JSON API

Offset Pagination

```shell
curl -X 'GET' \
  'http://localhost:8080/api/groupPages?page%5Bsize%5D=2&page%5Btotals%5D=true' \
  -H 'accept: application/vnd.api+json'
```


Cursor Pagination

```shell
curl -X 'GET' \
  'http://localhost:8080/api/groupStreams?page%5Bfirst%5D=2&page%5Btotals%5D=true' \
  -H 'accept: application/vnd.api+json'
```


##### GraphQL

Offset Pagination

As the pagination arguments in GraphQL for offset pagination and cursor pagination are the same, `after: 0` is used to hint that offset pagination is requested.

```graphql
query {
  groupPages (first: 2 after: 0) {
    edges {
      node {
        name
        commonName
        description
      }
    }
    pageInfo {
      hasNextPage
      startCursor
      endCursor
      totalRecords
    }
  }
}
```

Cursor Pagination

```graphql
query {
  groupStreams (first: 2) {
    edges {
      node {
        name
        commonName
        description
      }
    }
    pageInfo {
      hasNextPage
      startCursor
      endCursor
      totalRecords
    }
  }
}
```
### OpenAPI

Elide will only generate the OpenAPI document for the models that are defined in its `EntityDictionary`.

If a consolidated document is required, for instance for documenting a `@RestController` like the `HelloController` example then Elide offers integration with Springdoc which can be accessed at http://localhost:8080/swagger-ui/index.html.

The sample configuration is in `OpenApiConfiguration` which takes into account Elide's API versioning functionality.

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>${springdoc.version}</version>
</dependency>
```

### Error Responses

Error responses in Elide can be overridden by exposing a `ExceptionMapper` bean.

This project comes with a `TransactionExceptionMapper` that will handle `org.hibernate.exception.ConstraintViolationException` that are raised when a database constraint has been violated.

### Security

Elide integrates with Spring Security by using the `com.yahoo.elide.spring.security.HttpServletRequestUser` implementation of `com.yahoo.elide.core.security.User`. Using the `HttpServletRequest` also allows integration with the security provided by the container or custom schemes where a filter is used to wrap the `HttpServletRequest`.

When Spring Security is used it will by default wrap the `HttpServletRequest` using the `org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper` which will return Spring's `org.springframework.security.core.Authentication` as the principal via `getUserPrincipal()` and delegate accordingly when `isUserInRole()` is called. contain. This is accessed from `RequestScope.getUser()` or in security checks like `UserCheck`.

By default this project does not enable security to make it easier to explore the API.

This project does come with sample configuration in `SecurityConfiguration` for enabling form login security for Spring Security. This is not intended as a reference but for demonstration purposes only. This can be enabled by setting `app.security.enabled=true` in `application.yaml`.

```yaml
app:
  security:
    enabled: true
```

This enables form login with the following test users.

|User      |Password       | Roles
|----------|---------------|---------
|`admin`   |`adminpass`    | `ROLE_ADMIN`, `ROLE_USER`
|`user`    |`userpass`     | `ROLE_USER`

As the session credentials are maintained in the `JSESSIONID` cookie. The sample configuration also enables Cross-Site Request Forgery (CSRF) protection using the [Cookie-to-header token](https://en.wikipedia.org/wiki/Cross-site_request_forgery#Cookie-to-header_token) mechanism that relies on proper configuration of Cross-Origin Resource Sharing (CORS) to prevent sending of custom headers. This means that Spring Security expects a `X-XSRF-TOKEN` header with the cookie value of `XSRF-TOKEN` for all methods except `GET`, `HEAD`, `TRACE` and `OPTIONS`. Failure to send the `X-XSRF-TOKEN` for `POST` or `PATCH` requests will result in a `403 Forbidden`.

The sample Swagger UI and GraphiQL implementations in this project have been modified to support CSRF protection. This is enabled for Springdoc by setting `springdoc.swagger-ui.csrf.enabled=true`.

A sample `UserCheck` is implemented in `AdminCheck` that just checks that the user has `ROLE_ADMIN`. This is applied to the `Mail` model when security is enabled.

### ID Obfuscation

It is common to use a numeric sequence as the primary key, however this may undesirably leak information.

* The value of the IDs generated can be predicted
* Potentially leaks the counts of items
* Potentially leaks the relative times of creation between different records

 However at the same time using a random value as the primary key may affect performance.

Elide supports 2 different ways of not exposing the primary key of the entity.

* Allows registering a `IdObfuscator` that can obfuscate the id. Typically an encryption algorithm is used.
* Allows designating another field or property as the `@EntityId` that will be used to look up the record instead of the `@Id`.

#### ID Obfuscator

This project has a `Note` resource which uses a sequence as the primary key to demonstrate using a ID Obfuscator. This uses the `org.springframework.security.crypto.encrypt.AesBytesEncryptor`.

```yaml
app:
  security:
    id-obfuscation:
      enabled: true
      password: yourPassword
      salt: 5c0744940b5c369b
```

#### Entity ID

This project has a `Post` resource which uses a sequence as the primary key to demonstrate using a Entity ID which stores a UUID.

### Native

Elide supports being built into a GraalVM native image by supplying a feature, `yahoo.elide.core.graal.ElideFeature`, and its own native hints in it's libraries, ie. `native-image.properties`, `reflect-config.json` and `resource-config.json`.

Further configuration is typically required, for instance to add project specific resources like `analytics/models/tables/artifactDownloads.hjson`, or for instance if the [GraalVM Reachability Metadata Repository](https://github.com/oracle/graalvm-reachability-metadata) does not contain updated metadata for newly released libraries.

This project has configured additional hints using Spring Boot's `RuntimeHintsRegistrar` in `AppRuntimeHints`. This is configured on `App` using `@ImportRuntimeHints`.

## Deploying

### Docker and Containerize

To containerize and run elide project locally

1. Build Docker Image from Root of the project 
   ```
   docker build -t elide/elide-spring-boot-example .
   ```
2. Run docker container locally
   ```
   docker run -p 8081:8080 -d elide/elide-spring-boot-example
   ```
3. Application should be running in 
   ```
   http://localhost:8081/
   ```
   
3. Check Docker containers running
   ```
   docker ps
   CONTAINER ID   IMAGE                             COMMAND                  CREATED         STATUS         PORTS                     NAMES
   99998a35d377   elide/elide-spring-boot-example   "java -cp app:app/li…"   6 seconds ago   Up 5 seconds   0.0.0.0:8089->8080/tcp    upbeat_lehmann
   ```
4. Check logs of the container
   ```
   docker logs -f 99998a35d377 
   ```

5. If you want to ssh to your docker container for more troubleshooting
   ```
    docker exec -it 99998a35d377 /bin/sh
   ```
### Run from cloud (AWS)

1. ECR 
   1. Create a repository in Elastic Container Registry with name elide-spring-boot-example
   2. aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin xxx.dkr.ecr.us-east-1.amazonaws.com 
   3. docker tag elide-spring-boot-example:latest xxx.dkr.ecr.us-east-1.amazonaws.com/elide-spring-boot-example:latest
   4. docker push xxx.dkr.ecr.us-east-1.amazonaws.com/elide-spring-boot-example
2. Fargate
   1. With the image on AWS ECR, you can now define your deployment on AWS ECS/Fargate.
   2. Make sure to mention ECR details
   3. Port mapping is needed in the task definition. If you open the json definition of the task, you can edit to add this.
   "portMappings": [
         {
         "hostPort": 8080,
         "protocol": "tcp",
         "containerPort": 8080
         }
      ],
    4. The security group created or used for the task , needs to have inbound rule for TCP custom port 8080 , CIDR block 0.0.0.0/0(open to everyone, or you can add your company's CIDR block if its an internal service)
    
## Usage

See [Elide's Getting Started documentation](https://elide.io/pages/guide/v7/01-start.html).

## Queries

The following are sample queries.

### JSON API

#### Mutation

`POST /groups/{groupId}/products/{productId}/versions`

|Variable      |Value
|--------------|--------------------
|`groupId`     |`com.yahoo.elide`
|`productId`   |`elide-core`

```json
{
  "data": {
  "type": "versions",
  "id": "7.1.0",
  "attributes": {
    "createdOn": "2007-12-03T10:15Z"
    }
  }
}
```

#### Atomic Operations

`POST /operations`

```json
{
  "atomic:operations": [
    {
      "op": "add",
      "href": "/groups/com.yahoo.elide/products/elide-core/versions",
      "data": {
        "type": "versions",
        "id": "7.1.0",
        "attributes": {
          "createdOn": "2007-12-03T10:15Z"
        }
      }
    }
  ]
}
```

#### Async Query

`POST /asyncQuery`

```json
{
  "data": {
    "type": "asyncQuery",
    "id": "ba31ca4e-ed8f-4be0-a0f3-12088fa9263d",
    "attributes": {
      "query": "/groups?sort=commonName&fields%5Bgroups%5D=commonName,description",
      "queryType": "JSONAPI_V1_0",
      "status": "QUEUED"
    }
  }
}
```

#### Async Table Export

`POST /tableExport`

```json
{
  "data": {
    "type": "tableExport",
    "id": "ba31ca4e-ed8f-4be0-a0f3-12088fa9263f",
    "attributes": {
      "query": "/groups?sort=commonName&fields%5Bgroups%5D=commonName,description",
      "queryType": "JSONAPI_V1_0",
      "status": "QUEUED",
      "resultType": "CSV"
    }
  }
}
```


### GraphQL

#### Query

```graphql
query QueryGroup {
  groups {
    edges {
      node {
        name
        description
        products {
          edges {
            node {
              name
              versions {
                edges {
                  node {
                    name
                    createdOn
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
```

#### Mutation

```graphql
mutation UpsertGroup {
  groups(
    op: UPSERT
    data: {name: "org.hibernate.orm", description: "Hibernate"}
  ) {
    edges {
      node {
        name
        description
      }
    }
  }
}
```
#### Subscription

```graphql
subscription OnAddGroup {
  groups (topic: ADDED) {
    name
    description
  }
}
```

#### Async Query

```graphql
mutation {
  asyncQuery(
    op: UPSERT
    data: {id: "bb31ca4e-ed8f-4be0-a0f3-12088fb9263e", query: "{\"query\":\"{ groups { edges { node { name } } } }\",\"variables\":null}", queryType: GRAPHQL_V1_0, status: QUEUED}
  ) {
    edges {
      node {
        id
        query
        queryType
        status
        result {
          completedOn
          responseBody
          contentLength
          httpStatus
          recordCount
        }
      }
    }
  }
}
```

#### Async Table Export

```graphql
mutation {
  tableExport(
    op: UPSERT
    data: {id: "bb31ca4e-ed8f-4be0-a0f3-12088fb9263d", query: "{\"query\":\"{ groups { edges { node { name } } } }\",\"variables\":null}", queryType: GRAPHQL_V1_0, resultType: "CSV", status: QUEUED}
  ) {
    edges {
      node {
        id
        query
        queryType
        resultType
        status
        result {
          completedOn
          url
          message
          httpStatus
          recordCount
        }
      }
    }
  }
}
```

## Dependencies

This example uses the `elide-spring-boot-starter` which includes most of Elide's modules which may be excluded if not required.

### Async

This enables the async functionality to make `asyncQuery` and `tableExport` for both JSON-API and GraphQL.

```xml
<dependency>
    <groupId>com.yahoo.elide</groupId>
    <artifactId>elide-spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.yahoo.elide</groupId>
            <artifactId>elide-async</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### Aggregation Datastore

This enables the functionality for defining analytic models. The example is at `resources/analytics/models/tables/artifactDownloads.hjson.`

```xml
<dependency>
    <groupId>com.yahoo.elide</groupId>
    <artifactId>elide-spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.yahoo.elide</groupId>
            <artifactId>elide-datastore-aggregation</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### GraphQL

This enables the functionality for making GraphQL queries, mutations and subscriptions.

```xml
<dependency>
    <groupId>com.yahoo.elide</groupId>
    <artifactId>elide-spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.yahoo.elide</groupId>
            <artifactId>elide-graphql</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### Swagger

This enables the functionality for exposing the JSON-API documentation using OpenAPI 3.

```xml
<dependency>
    <groupId>com.yahoo.elide</groupId>
    <artifactId>elide-spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.yahoo.elide</groupId>
            <artifactId>elide-swagger</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
## Contribute
Please refer to the [CONTRIBUTING.md](CONTRIBUTING.md) file for information about how to get involved. We welcome issues, questions, and pull requests.

## License
This project is licensed under the terms of the [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) open source license.
Please refer to [LICENSE](LICENSE.txt) for the full terms.
