package example.config;

import lombok.Data;

@Data
public class SwaggerControllerProperties extends ControllerProperties {
    private String name = "Elide Service";
    private String version = "";
}
