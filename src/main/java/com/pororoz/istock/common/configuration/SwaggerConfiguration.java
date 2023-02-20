package com.pororoz.istock.common.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"local", "build", "dev"})
@Configuration
public class SwaggerConfiguration {

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("v1-definition")
        .pathsToMatch("/v1/**")
        .build();
  }

  @Bean
  public OpenAPI openAPI() {
    Info info = new Info()
        .title("iStock")
        .version("v0.0.1")
        .license(new License().name("Apache 2.0").url("http://springdoc.org"));

    ExternalDocumentation externalDocumentation = new ExternalDocumentation()
        .description("iStock Documentation")
        .url("https://vigorous-mailbox-050.notion.site/iStock-0e74fa8144414d078810cb11bc08a7c3");

    return new OpenAPI().info(info).externalDocs(externalDocumentation);
  }
}
