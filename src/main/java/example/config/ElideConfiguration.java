package example.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

import com.yahoo.elide.core.security.obfuscation.IdObfuscator;
import com.yahoo.elide.spring.datastore.config.DataStoreBuilderCustomizer;
import com.yahoo.elide.spring.security.obfuscation.BytesEncryptorIdObfuscator;

import example.datastore.OperationDataStore;
import example.datastore.SpringDataDataStore;
import example.exception.TransactionExceptionMapper;
import example.model.ArtifactGroupPage;
import example.model.ArtifactGroupStream;
import example.model.Mail;
import example.repository.ArtifactGroupRepository;
import example.service.JacksonSpringDataCursorEncoder;
import example.service.QueryService;
import example.service.SpringDataCursorEncoder;
import jakarta.validation.Validator;

/**
 * Configuration for Elide.
 */
@Configuration
@EnableConfigurationProperties(AppSecurityProperties.class)
public class ElideConfiguration {
    /**
     * Creates the {@link OperationDataStore}.
     *
     * @return the builder
     */
    @Bean
    DataStoreBuilderCustomizer operationDataStoreBuilderCustomizer(Validator validator) {
        return builder -> {
            builder.dataStore(new OperationDataStore(validator, Mail.class));
        };
    }

    /**
     * Creates the {@link SpringDataDataStore}.
     *
     * @return the builder
     */
    @Bean
    DataStoreBuilderCustomizer springDataDataStoreBuilderCustomizer(QueryService queryService,
            SpringDataCursorEncoder cursorEncoder) {
        return builder -> {
            builder.dataStore(new SpringDataDataStore(queryService, cursorEncoder));
        };
    }

    /**
     * Creates the {@link QueryService}.
     *
     * @return the service
     */
    @Bean
    QueryService queryService(ArtifactGroupRepository artifactGroupRepository) {
        Map<Class<?>, JpaSpecificationExecutor<?>> repositories = new HashMap<>();
        repositories.put(ArtifactGroupStream.class, artifactGroupRepository);
        repositories.put(ArtifactGroupPage.class, artifactGroupRepository);
        return new QueryService(repositories);
    }

    /**
     * Creates the {@link SpringDataCursorEncoder}.
     * 
     * @return the cursor encoder
     */
    @Bean
    SpringDataCursorEncoder springDataCursorEncoder() {
        return new JacksonSpringDataCursorEncoder();
    }

    /**
     * Configures a id obfuscator.
     *
     * For demonstration purposes.
     * 
     * @param securityConfigProperties the configuration
     * @return the id obfuscator
     */
    @Bean
    @ConditionalOnProperty(prefix = "app.security.id-obfuscation", name = "enabled", havingValue = "true")
    IdObfuscator idObfuscator(AppSecurityProperties securityConfigProperties) {
        String password = securityConfigProperties.getIdObfuscation().getPassword();
        String salt = securityConfigProperties.getIdObfuscation().getSalt();
        AesBytesEncryptor bytesEncryptor = new AesBytesEncryptor(password, salt);
        return new BytesEncryptorIdObfuscator(bytesEncryptor);
    }

    /**
     * Configures an exception mapper.
     *
     * @return the exception mapper
     */
    @Bean
    TransactionExceptionMapper transactionExceptionMapper() {
        return new TransactionExceptionMapper();
    }
}
