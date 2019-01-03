package io.craigmiller160.videomanagerserver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "video")
data class VideoConfiguration (
        var vlcCommand: String = "",
        var filePathRoot: String = ""
)