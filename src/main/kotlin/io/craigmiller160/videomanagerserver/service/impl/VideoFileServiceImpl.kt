package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.*
import io.craigmiller160.videomanagerserver.file.FileScanner
import io.craigmiller160.videomanagerserver.player.VideoPlayer
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.service.VideoFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

@Service
class VideoFileServiceImpl @Autowired constructor(
        private val videoFileRepo: VideoFileRepository,
        private val videoConfig: VideoConfiguration,
        private val fileScanner: FileScanner,
        private val videoPlayer: VideoPlayer
): VideoFileService {

    private val fileScanRunning = AtomicBoolean(false)

    private fun getVideoFileSort(sortDirection: Sort.Direction): Sort {
        return Sort.by(
                Sort.Order(sortDirection, "displayName", Sort.NullHandling.NULLS_LAST),
                Sort.Order(sortDirection, "fileName", Sort.NullHandling.NULLS_LAST)
        )
    }

    override fun getAllVideoFiles(page: Int, sortDirection: String): List<VideoFile> {
        val sort = getVideoFileSort(Sort.Direction.valueOf(sortDirection))
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

    override fun playVideo(videoFile: VideoFile) {
        val dbVideoFileOpt = videoFileRepo.findById(videoFile.fileId)
        val dbVideoFile = dbVideoFileOpt.orElseThrow { Exception("Could not find video file in DB: ${videoFile.fileName}") }
        dbVideoFile.viewCount++
        videoFileRepo.save(dbVideoFile)
        videoPlayer.playVideo(dbVideoFile)
    }

    override fun searchForVideos(search: VideoSearch, page: Int, sortDirection: String): VideoSearchResults {
        val pageSize = videoConfig.apiPageSize
        val sort = getVideoFileSort(Sort.Direction.valueOf(sortDirection))
        val pageable = PageRequest.of(page, pageSize, sort)
        val videoList = videoFileRepo.searchByValues("%${search.searchText}%", search.seriesId, search.starId, search.categoryId, pageable)
        return VideoSearchResults().apply {
            // TODO need totalFiles here
            filesPerPage = pageSize
            currentPage = page
            this.videoList = videoList
        }
    }
}