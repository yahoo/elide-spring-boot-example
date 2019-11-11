package example.config;

import lombok.Data;

@Data
public class ControllerConfig {
    boolean enabled;
    String path;
}
