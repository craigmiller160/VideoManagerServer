package io.craigmiller160.videomanagerserver.service

import io.craigmiller160.videomanagerserver.dto.VideoFile
import java.util.Optional

interface VideoFileService {

    fun getAllVideoFiles(page: Int, sortDirection: String): List<VideoFile>

    fun getVideoFile(fileId: Long): Optional<VideoFile>

    fun addVideoFile(videoFile: VideoFile): VideoFile

    fun updateVideoFile(fileId: Long, videoFile: VideoFile): Optional<VideoFile>

    fun deleteVideoFile(fileId: Long): Optional<VideoFile>

}