package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.FileScanStatus
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.dto.VideoSearch
import io.craigmiller160.videomanagerserver.dto.VideoSearchResults
import io.craigmiller160.videomanagerserver.service.VideoFileService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.support.ResourceRegion
import org.springframework.data.domain.Sort
import org.springframework.http.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/video-files")
class VideoFileController @Autowired constructor(
        private val videoFileService: VideoFileService
) {

    private val logger = LoggerFactory.getLogger(VideoFileController::class.java)

    private fun validateSortDirection(sortDirection: String) {
        try {
            Sort.Direction.valueOf(sortDirection)
        }
        catch (ex: Exception) {
            throw IllegalArgumentException("Invalid sortDirection parameter: $sortDirection")
        }
    }

    private fun validateVideoFileName(videoFileName: String) {
        if (videoFileName.isEmpty()) {
            throw java.lang.IllegalArgumentException("Video file name must be provided")
        }
    }

    private fun cleanUpSearch(search: VideoSearch) {
        if (search.searchText?.isEmpty() == true) search.searchText = null
        if (search.categoryId == 0L) search.categoryId = null
        if (search.seriesId == 0L) search.seriesId = null
        if (search.starId == 0L) search.starId = null
    }

    @GetMapping
    fun getAllVideoFiles(@RequestParam(required = false, defaultValue = "0") page: Int,
                         @RequestParam(required = false, defaultValue = "ASC") sortDirection: String): ResponseEntity<List<VideoFile>> {
        validateSortDirection(sortDirection)
        val videoFiles = videoFileService.getAllVideoFiles(page, sortDirection)
        if (videoFiles.isEmpty()) {
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(videoFiles)
    }

    @GetMapping("/{videoFileId}")
    fun getVideoFile(@PathVariable videoFileId: Long): ResponseEntity<VideoFile> {
        return okOrNoContent(videoFileService.getVideoFile(videoFileId))
    }

    @PostMapping
    fun addVideoFile(@RequestBody videoFile: VideoFile): ResponseEntity<VideoFile> {
        return ResponseEntity.ok(videoFileService.addVideoFile(videoFile))
    }

    @PutMapping("/{videoFileId}")
    fun updateVideoFile(@PathVariable videoFileId: Long, @RequestBody videoFile: VideoFile): ResponseEntity<VideoFile> {
        return okOrNoContent(videoFileService.updateVideoFile(videoFileId, videoFile))
    }

    @DeleteMapping("/{videoFileId}")
    fun deleteVideoFile(@PathVariable videoFileId: Long): ResponseEntity<VideoFile> {
        return okOrNoContent(videoFileService.deleteVideoFile(videoFileId))
    }

    @PostMapping("/scanner")
    fun startVideoFileScan(): ResponseEntity<FileScanStatus> {
        val status = videoFileService.startVideoFileScan()
        if (status.alreadyRunning) {
            logger.warn("Video scanner already running, cannot start it again")
            return ResponseEntity.badRequest().body(status)
        }
        return ResponseEntity.ok(status)
    }

    @GetMapping("/scanner")
    fun isVideoFileScanRunning(): ResponseEntity<FileScanStatus> {
        return ResponseEntity.ok(videoFileService.isVideoFileScanRunning())
    }

    @GetMapping("/play/{fileId}")
    fun playVideo(@PathVariable fileId: Long, @RequestHeader headers: HttpHeaders): ResponseEntity<ResourceRegion> {
        val video = videoFileService.playVideo(fileId)
        val region = resourceRegion(video, headers)
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory
                        .getMediaType(video)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region)
    }

    @GetMapping("/record_play/{fileId}")
    fun recordNewVideoPlay(@PathVariable fileId: Long): ResponseEntity<Void> {
        videoFileService.recordNewVideoPlay(fileId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/search")
    fun searchForVideos(@RequestBody search: VideoSearch,
                        @RequestParam(required = false, defaultValue = "0") page: Int,
                        @RequestParam(required = false, defaultValue = "ASC") sortDirection: String): ResponseEntity<VideoSearchResults> {
        validateSortDirection(sortDirection)
        cleanUpSearch(search)
        val videoFiles = videoFileService.searchForVideos(search, page, sortDirection)
        if (videoFiles.videoList.isEmpty()) {
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(videoFiles)
    }

}