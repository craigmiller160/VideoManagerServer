package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.FileScanStatus
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.dto.createScanAlreadyRunningStatus
import io.craigmiller160.videomanagerserver.dto.createScanNotRunningStatus
import io.craigmiller160.videomanagerserver.dto.createScanRunningStatus
import io.craigmiller160.videomanagerserver.file.FileScanner
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.service.VideoFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.concurrent.atomic.AtomicBoolean

@Service
class VideoFileServiceImpl @Autowired constructor(
        private val videoFileRepo: VideoFileRepository,
        private val videoConfig: VideoConfiguration,
        private val fileScanner: FileScanner
): VideoFileService {

    private val fileScanRunning = AtomicBoolean(false)

    override fun getAllVideoFiles(page: Int, sortDirection: String): List<VideoFile> {
        val sort = Sort.by(
                Sort.Order(Sort.Direction.valueOf(sortDirection), "displayName", Sort.NullHandling.NULLS_LAST),
                Sort.Order(Sort.Direction.valueOf(sortDirection), "fileName", Sort.NullHandling.NULLS_LAST)
        )

        val pageable = PageRequest.of(page, videoConfig.apiPageSize, sort)
        return videoFileRepo.findAll(pageable).toList()
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

    override fun startVideoFileScan(): FileScanStatus {
        if (fileScanRunning.get()) {
            return createScanAlreadyRunningStatus()
        }
        fileScanRunning.set(true)
        fileScanner.scanForFiles {
            fileScanRunning.set(false)
        }
        return createScanRunningStatus()
    }

    override fun isVideoFileScanRunning(): FileScanStatus {
        val scanRunning = fileScanRunning.get()
        if (scanRunning) {
            return createScanRunningStatus()
        }
        return createScanNotRunningStatus()
    }
}