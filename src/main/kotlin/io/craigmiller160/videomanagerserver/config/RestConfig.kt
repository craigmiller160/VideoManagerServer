package io.craigmiller160.videomanagerserver.config

import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RestConfig(private val keycloakClientRequestFactory: KeycloakClientRequestFactory) {
  @Bean
  fun restTemplate(): KeycloakRestTemplate = KeycloakRestTemplate(keycloakClientRequestFactory)
}
