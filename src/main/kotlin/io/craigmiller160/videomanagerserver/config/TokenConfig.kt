package io.craigmiller160.videomanagerserver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Suppress("ConfigurationProperties")
@Configuration
@Validated
@ConfigurationProperties(prefix = "video.security.token")
data class TokenConfig (
        var expSecs: Int = 0
)