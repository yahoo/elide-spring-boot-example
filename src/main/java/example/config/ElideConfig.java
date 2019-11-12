package example.config;

import com.yahoo.elide.Elide;
import com.yahoo.elide.ElideSettingsBuilder;
import com.yahoo.elide.core.DataStore;
import com.yahoo.elide.core.EntityDictionary;
import com.yahoo.elide.core.filter.dialect.RSQLFilterDialect;
import com.yahoo.elide.datastores.jpa.JpaDataStore;
import com.yahoo.elide.datastores.jpa.transaction.NonJtaTransaction;
import example.Settings;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.persistence.EntityManagerFactory;
import java.util.TimeZone;

@Configuration
public class ElideConfig {

    @Bean
    @DependsOn("liquibaseMigration")
    @ConditionalOnMissingBean(Elide.class)
    Elide initializeElide(AutowireCapableBeanFactory beanFactory,
                          ElideConfigProperties config,
                          EntityManagerFactory entityManagerFactory) throws Exception {
        //If JDBC_DATABASE_URL is not set, we'll run with H2 in memory.
        boolean inMemory = (System.getenv("JDBC_DATABASE_URL") == null);

        Settings old_settings = new Settings(inMemory) {};

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
}
