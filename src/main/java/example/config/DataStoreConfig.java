package example.config;

import com.yahoo.elide.core.DataStore;
import com.yahoo.elide.datastores.jpa.JpaDataStore;
import example.datastore.SpringDataTransaction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
public class DataStoreConfig {

    @Bean
    @ConditionalOnMissingBean(DataStore.class)
    DataStore buildDataStore(EntityManagerFactory entityManagerFactory) {
        return new JpaDataStore(
                () -> { return entityManagerFactory.createEntityManager(); },
                (em -> { return new SpringDataTransaction(em); }));
    }
}
