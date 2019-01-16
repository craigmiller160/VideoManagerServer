package io.craigmiller160.videomanagerserver.player

import io.craigmiller160.videomanagerserver.dto.VideoFile

interface VideoPlayer {

    fun playVideo(videoFile: VideoFile)

}