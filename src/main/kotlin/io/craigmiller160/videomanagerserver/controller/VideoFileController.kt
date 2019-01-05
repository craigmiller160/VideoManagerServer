package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.service.VideoFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.lang.IllegalArgumentException
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/video-files")
class VideoFileController @Autowired constructor(
        private val videoFileService: VideoFileService
) {

    private fun validateSortDirection(sortDirection: String) {
        try {
            Sort.Direction.valueOf(sortDirection)
        }
        catch (ex: Exception) {
            throw IllegalArgumentException("Invalid sortDirection parameter: $sortDirection")
        }
    }

    @ExceptionHandler
    fun handleIllegalArgumentException(ex: IllegalArgumentException, response: HttpServletResponse) {
        response.sendError(HttpStatus.BAD_REQUEST.value())
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

}