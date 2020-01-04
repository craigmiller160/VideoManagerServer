package io.craigmiller160.videomanagerserver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Suppress("ConfigurationProperties")
@Configuration
@Validated
@ConfigurationProperties(prefix = "video")
data class VideoConfiguration (
        var vlcCommand: String = "",
        var apiPageSize: Int = 0,
        var fileExts: String = ""
) {

    fun splitFileExts(): List<String> {
        return fileExts.split(",")
                .map { ext -> ext.trim() }
    }

}