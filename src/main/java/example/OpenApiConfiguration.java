package example;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfiguration.
 */
@Configuration
public class OpenApiConfiguration {
	@Bean
	public GroupedOpenApi api() {
	    return GroupedOpenApi.builder()
	            .group("default")
	            .pathsToMatch("/**")
	            .pathsToExclude("/api/v1/**", "/api/v2/**")
	            .build();
	}
	
	@Bean
	public GroupedOpenApi apiV1() {
	    return GroupedOpenApi.builder()
	            .group("v1")
	            .pathsToMatch("/api/v1/**")
	            .build();
	}
	
	@Bean
	public GroupedOpenApi apiV2() {
	    return GroupedOpenApi.builder()
	            .group("v2")
	            .pathsToMatch("/api/v2/**")
	            .build();
	}
}
