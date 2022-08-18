/*
 * Copyright 2019, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import com.yahoo.elide.core.filter.Operator;
import com.yahoo.elide.core.type.ClassType;
import com.yahoo.elide.datastores.jpql.filter.FilterTranslator;

import example.models.ArtifactGroup;

/**
 * Example app using elide-spring.
 */
@SpringBootApplication
@EntityScan
public class App {
    public static void main(String[] args) throws Exception {
    	TestJPQLPredicateGenerator generator = new TestJPQLPredicateGenerator();
        FilterTranslator.registerJPQLGenerator(Operator.IN, ClassType.of(ArtifactGroup.class), "commonName", generator);
        SpringApplication.run(App.class, args);
    }
}
