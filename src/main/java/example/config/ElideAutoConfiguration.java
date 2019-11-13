package example.config;

import com.yahoo.elide.Elide;
import com.yahoo.elide.ElideSettingsBuilder;
import com.yahoo.elide.contrib.swagger.SwaggerBuilder;
import com.yahoo.elide.core.DataStore;
import com.yahoo.elide.core.EntityDictionary;
import com.yahoo.elide.core.filter.dialect.RSQLFilterDialect;
import com.yahoo.elide.datastores.jpa.JpaDataStore;
import example.datastore.SpringDataTransaction;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
public class ElideAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Elide.class)
    public Elide initializeElide(EntityDictionary dictionary,
                          DataStore dataStore) throws Exception {

        ElideSettingsBuilder builder = new ElideSettingsBuilder(dataStore)
                .withUseFilterExpressions(true)
                .withEntityDictionary(dictionary)
                .withJoinFilterDialect(new RSQLFilterDialect(dictionary))
                .withSubqueryFilterDialect(new RSQLFilterDialect(dictionary))
                .withISO8601Dates("yyyy-MM-dd'T'HH:mm'Z'", TimeZone.getTimeZone("UTC"));

        return new Elide(builder.build());
    }

    @Bean
    @ConditionalOnMissingBean(EntityDictionary.class)
    public EntityDictionary buildDictionary(AutowireCapableBeanFactory beanFactory) {
        return new EntityDictionary(new HashMap<>(), beanFactory::autowireBean);
    }

    @Bean
    @ConditionalOnMissingBean(DataStore.class)
    public DataStore buildDataStore(EntityManagerFactory entityManagerFactory) {
        return new JpaDataStore(
                () -> { return entityManagerFactory.createEntityManager(); },
                (em -> { return new SpringDataTransaction(em); }));
    }

    @Bean
    @ConditionalOnMissingBean(Swagger.class)
    public Swagger buildSwagger(EntityDictionary dictionary, ElideConfigProperties settings) {
        Info info = new Info()
                .title(settings.getSwagger().getName())
                .version(settings.getSwagger().getVersion());

        SwaggerBuilder builder = new SwaggerBuilder(dictionary, info);

        Swagger swagger = builder.build().basePath(settings.getJsonApi().getPath());

        return swagger;
    }
}
