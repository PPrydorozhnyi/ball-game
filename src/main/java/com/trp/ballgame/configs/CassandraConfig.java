package com.trp.ballgame.configs;

import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class CassandraConfig {

  @Bean
  public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
    final var bundle = astraProperties.getSecureConnectBundle().toPath();
    return builder -> builder.withCloudSecureConnectBundle(bundle);
  }
}
