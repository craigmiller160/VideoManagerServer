package io.craigmiller160.videomanagerserver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Suppress("ConfigurationProperties")
@Configuration
@Validated
@ConfigurationProperties(prefix = "spring.datasource")
data class DbConfig (
    var url: String = "",
    var username: String = "",
    var password: String = "",
    var driverClassName: String = "",
    var db: String = "",
    var docker: Boolean = false
)