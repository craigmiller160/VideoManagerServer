package io.craigmiller160.videomanagerserver.service

import io.craigmiller160.videomanagerserver.dto.FileScanStatus
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.dto.VideoSearch
import io.craigmiller160.videomanagerserver.dto.VideoSearchResults
import org.springframework.core.io.UrlResource
import java.util.*

interface VideoFileService {

    fun getAllVideoFiles(page: Int, sortDirection: String): List<VideoFile>

    fun getVideoFile(fileId: Long): Optional<VideoFile>

    fun addVideoFile(videoFile: VideoFile): VideoFile

    fun updateVideoFile(fileId: Long, videoFile: VideoFile): Optional<VideoFile>

    fun deleteVideoFile(fileId: Long): Optional<VideoFile>

    fun startVideoFileScan(): FileScanStatus

    fun isVideoFileScanRunning(): FileScanStatus

    fun playVideo(fileId: Long): UrlResource

    fun recordNewVideoPlay(fileId: Long)

    fun searchForVideos(search: VideoSearch): VideoSearchResults

}