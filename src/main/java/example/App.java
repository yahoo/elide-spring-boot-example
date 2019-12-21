/*
 * Copyright 2019, Verizon Media.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package example;

import com.yahoo.elide.core.DataStore;
import com.yahoo.elide.datastores.jpa.JpaDataStore;
import com.yahoo.elide.datastores.jpa.transaction.NonJtaTransaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Example app using elide-spring.
 */
@SpringBootApplication
public class App {
    EntityCompiler compiler = new EntityCompiler();
    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       DataSource source) {


        try {
            compiler.compile();
        } catch (Exception e) {}

        Collection<ClassLoader> classLoaders = new ArrayList<>();
        classLoaders.add(compiler.getClassLoader());
        Map<String, Object> properties = new HashMap<>();
        properties.put(AvailableSettings.CLASSLOADERS, classLoaders);

        Properties props = new Properties();
        props.putAll(properties);

        PersistenceUnitInfoImpl pui = new PersistenceUnitInfoImpl("test",
                Arrays.asList(EntityCompiler.classNames), props, compiler.getClassLoader());
        pui.setNonJtaDataSource(source);
        pui.setJtaDataSource(source);

        new HibernateJpaVendorAdapter();

        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        bean.setJpaPropertyMap(properties);
        bean.setPersistenceUnitManager(new PersistenceUnitManager() {
            @Override
            public PersistenceUnitInfo obtainDefaultPersistenceUnitInfo() throws IllegalStateException {
                return pui;
            }

            @Override
            public PersistenceUnitInfo obtainPersistenceUnitInfo(String persistenceUnitName) throws IllegalArgumentException, IllegalStateException {
                return pui;
            }
        });

        return bean;
    }
}



