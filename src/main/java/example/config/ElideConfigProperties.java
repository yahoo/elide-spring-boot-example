package example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;


@Data
@ConfigurationProperties(prefix = "elide")
@ComponentScan
public class ElideConfigProperties {
    private ControllerProperties jsonApi;
    private ControllerProperties graphql;
    private SwaggerControllerProperties swagger;
    private String modelPackage;
}
