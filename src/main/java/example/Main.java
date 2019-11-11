/*
 * Copyright 2019, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package example;

import com.yahoo.elide.Elide;
import com.yahoo.elide.ElideSettings;
import com.yahoo.elide.ElideSettingsBuilder;
import com.yahoo.elide.core.DataStore;
import com.yahoo.elide.core.EntityDictionary;
import com.yahoo.elide.core.filter.dialect.RSQLFilterDialect;
import com.yahoo.elide.datastores.jpa.JpaDataStore;
import com.yahoo.elide.datastores.jpa.transaction.NonJtaTransaction;

import com.yahoo.elide.standalone.Util;
import example.config.ElideConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * Example app using Elide library.
 */
@Slf4j
@SpringBootApplication
public class Main {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    Elide initializeElide(AutowireCapableBeanFactory beanFactory, ElideConfig config) throws Exception {
        //If JDBC_DATABASE_URL is not set, we'll run with H2 in memory.
        boolean inMemory = (System.getenv("JDBC_DATABASE_URL") == null);

        Settings old_settings = new Settings(inMemory) {};

        old_settings.runLiquibaseMigrations();

        EntityManagerFactory entityManagerFactory = Util.getEntityManagerFactory(config.getModelPackage(),
                old_settings.getDatabaseProperties());

        DataStore dataStore = new JpaDataStore(
                () -> { return entityManagerFactory.createEntityManager(); },
                (em -> { return new NonJtaTransaction(em); }));

        EntityDictionary dictionary = new EntityDictionary(old_settings.getCheckMappings(), beanFactory::autowireBean);

        ElideSettingsBuilder builder = new ElideSettingsBuilder(dataStore)
                .withUseFilterExpressions(true)
                .withEntityDictionary(dictionary)
                .withJoinFilterDialect(new RSQLFilterDialect(dictionary))
                .withSubqueryFilterDialect(new RSQLFilterDialect(dictionary));

        builder = builder.withISO8601Dates("yyyy-MM-dd'T'HH:mm'Z'", TimeZone.getTimeZone("UTC"));

        return new Elide(builder.build());
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }
}
