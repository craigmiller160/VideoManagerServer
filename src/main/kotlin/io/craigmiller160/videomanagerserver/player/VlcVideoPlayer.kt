package io.craigmiller160.videomanagerserver.player

import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.VideoFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class VlcVideoPlayer @Autowired constructor(
        private val videoConfig: VideoConfiguration
): VideoPlayer {

    override fun playVideo(videoFile: VideoFile) {
        val fullPath = "${videoConfig.filePathRoot}/${videoFile.fileName}"
        val procBuilder = ProcessBuilder("vlc", fullPath)
        procBuilder.start()
    }
}