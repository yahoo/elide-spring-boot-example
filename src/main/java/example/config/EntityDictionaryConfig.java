package example.config;

import com.yahoo.elide.core.EntityDictionary;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class EntityDictionaryConfig {

    @Bean
    @ConditionalOnMissingBean(EntityDictionary.class)
    EntityDictionary buildDictionary(AutowireCapableBeanFactory beanFactory) {
        return new EntityDictionary(new HashMap<>(), beanFactory::autowireBean);
    }
}
