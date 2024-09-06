package com.github.bestheroz.standard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;

@Configuration
public class AwsConfig {

  @Bean
  public AwsRegionProvider awsRegionProvider() {
    return new DefaultAwsRegionProviderChain();
  }
}
