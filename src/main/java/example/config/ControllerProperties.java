package example.config;

import lombok.Data;

/**
 * Settings for a Spring REST controller.
 */
@Data
public class ControllerProperties {

    /**
     * Whether or not the controller is enabled.
     */
    boolean enabled = false;

    /**
     * The URL path prefix for the controller.
     */
    String path = "/";
}
