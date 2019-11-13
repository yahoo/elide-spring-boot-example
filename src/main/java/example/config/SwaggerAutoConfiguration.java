package example.config;

import com.yahoo.elide.contrib.swagger.SwaggerBuilder;
import com.yahoo.elide.core.EntityDictionary;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnMissingBean(Swagger.class)
public class SwaggerAutoConfiguration {

        @Bean
        Swagger buildSwagger(EntityDictionary dictionary, ElideConfigProperties settings) {
            Info info = new Info().title("Test Service").version("1.0");

            SwaggerBuilder builder = new SwaggerBuilder(dictionary, info);

            Swagger swagger = builder.build().basePath(settings.getJsonApi().getPath());

            return swagger;
        }
    }
