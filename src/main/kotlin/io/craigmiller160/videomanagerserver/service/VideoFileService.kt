package io.craigmiller160.videomanagerserver.service

import io.craigmiller160.videomanagerserver.dto.VideoFile

interface VideoFileService {

    fun addVideoFile(videoFile: VideoFile)

    fun updateVideoFile(fileId: Long, videoFile: VideoFile)

    fun deleteVideoFile(fileId: Long)

}