package io.craigmiller160.videomanagerserver.config

import java.time.Duration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class RestConfig {
  @Bean
  fun oauth2ClientManager(
    clientRegistrationRepository: ClientRegistrationRepository,
    authorizedClientService: OAuth2AuthorizedClientService
  ): OAuth2AuthorizedClientManager {
    val authorizedClientProvider =
      OAuth2AuthorizedClientProviderBuilder.builder().refreshToken().clientCredentials().build()

    val authorizedClientManager =
      AuthorizedClientServiceOAuth2AuthorizedClientManager(
        clientRegistrationRepository, authorizedClientService)
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)

    return authorizedClientManager
  }

  @Bean
  fun webClient(authClientManager: OAuth2AuthorizedClientManager): WebClient {
    val oauth2Client = ServletOAuth2AuthorizedClientExchangeFilterFunction(authClientManager)
    oauth2Client.setDefaultClientRegistrationId("custom")

    val client = HttpClient.create().responseTimeout(Duration.ofSeconds(10))

    return WebClient.builder()
      .clientConnector(ReactorClientHttpConnector(client))
      .apply(oauth2Client.oauth2Configuration())
      .build()
  }
}
