package io.craigmiller160.videomanagerserver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "video")
data class VideoConfiguration (
        var vlcCommand: String = "",
        var filePathRoot: String = "",
        var apiPageSize: Int = 0,
        var fileExts: String = ""
) {

    fun splitFileExts(): List<String> {
        return fileExts.split(",")
                .map { ext -> ext.trim() }
    }

}