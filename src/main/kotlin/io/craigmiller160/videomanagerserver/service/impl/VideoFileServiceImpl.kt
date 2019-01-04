package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.service.VideoFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class VideoFileServiceImpl @Autowired constructor(
        private val videoFileRepo: VideoFileRepository
): VideoFileService {

    override fun getAllVideoFiles(): Set<VideoFile> {
        return videoFileRepo.findAll().toSet()
    }

    override fun getVideoFile(fileId: Long): Optional<VideoFile> {
        return videoFileRepo.findById(fileId)
    }

    override fun addVideoFile(videoFile: VideoFile): VideoFile {
        return videoFileRepo.save(videoFile)
    }

    override fun updateVideoFile(fileId: Long, videoFile: VideoFile): Optional<VideoFile> {
        videoFile.fileId = fileId
        return videoFileRepo.findById(fileId)
                .map { videoFileRepo.save(videoFile) }
    }

    override fun deleteVideoFile(fileId: Long): Optional<VideoFile> {
        val videoFileOptional = videoFileRepo.findById(fileId)
        videoFileRepo.deleteById(fileId)
        return videoFileOptional
    }
}