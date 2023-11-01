/*    
 * Copyright 2020, Yahoo Inc.    
 * Licensed under the Apache License, Version 2.0    
 * See LICENSE file in project root for terms.    
 */    

package example.config;    

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;    

@Data    
@ConfigurationProperties(prefix = "app.security")
public class SecurityConfigProperties {
    private String origin = "*";
}
