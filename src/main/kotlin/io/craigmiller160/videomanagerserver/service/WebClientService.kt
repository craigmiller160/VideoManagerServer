package io.craigmiller160.videomanagerserver.service

import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.FileConversionRequest
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class WebClientService(
  private val webClient: WebClient,
  private val videoConfig: VideoConfiguration
) {
  private val log = LoggerFactory.getLogger(javaClass)
  suspend fun sendConvertFileRequest(sourceFile: String) =
    webClient
      .post()
      .uri(videoConfig.converterUrl)
      .body(BodyInserters.fromValue(FileConversionRequest(sourceFile)))
      .awaitExchange { res ->
        if (res.statusCode().is4xxClientError || res.statusCode().is5xxServerError) {
          val body = res.bodyToMono<String>().awaitSingle()
          log.error(
            "Error sending convert request for $sourceFile. Status: ${res.statusCode().value()} Message: $body")
        }
      }
}
