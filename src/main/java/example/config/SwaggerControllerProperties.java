package example.config;

import lombok.Data;

/**
 * Extra controller properties for the Swagger document endpoint.
 */
@Data
public class SwaggerControllerProperties extends ControllerProperties {

    /**
     * Swagger needs a name for the service.
     */
    private String name = "Elide Service";

    /**
     * Swagger needs a version for the service.
     */
    private String version = "1.0";
}
