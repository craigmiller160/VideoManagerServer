/*
 *     video-manager-server
 *     Copyright (C) 2020 Craig Miller
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.FileScanStatusResponse
import io.craigmiller160.videomanagerserver.dto.VideoFilePayload
import io.craigmiller160.videomanagerserver.dto.VideoSearchRequest
import io.craigmiller160.videomanagerserver.dto.VideoSearchResponse
import io.craigmiller160.videomanagerserver.security.ROLE_EDIT
import io.craigmiller160.videomanagerserver.security.ROLE_SCAN
import io.craigmiller160.videomanagerserver.service.videofile.VideoFileService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.support.ResourceRegion
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/video-files")
class VideoFileController @Autowired constructor(private val videoFileService: VideoFileService) {

  private val logger = LoggerFactory.getLogger(VideoFileController::class.java)

  private fun validateSortDirection(sortDirection: String) {
    try {
      Sort.Direction.valueOf(sortDirection)
    } catch (ex: Exception) {
      throw IllegalArgumentException("Invalid sortDirection parameter: $sortDirection")
    }
  }

  private fun validateVideoFileName(videoFileName: String) {
    if (videoFileName.isEmpty()) {
      throw java.lang.IllegalArgumentException("Video file name must be provided")
    }
  }

  private fun cleanUpSearch(search: VideoSearchRequest) {
    if (search.searchText?.isEmpty() == true) search.searchText = null
    if (search.categoryId == 0L) search.categoryId = null
    if (search.seriesId == 0L) search.seriesId = null
    if (search.starId == 0L) search.starId = null
  }

  @GetMapping
  fun getAllVideoFiles(
    @RequestParam(required = false, defaultValue = "0") page: Int,
    @RequestParam(required = false, defaultValue = "ASC") sortDirection: String
  ): ResponseEntity<List<VideoFilePayload>> {
    validateSortDirection(sortDirection)
    val videoFiles = videoFileService.getAllVideoFiles(page, sortDirection)
    if (videoFiles.isEmpty()) {
      return ResponseEntity.noContent().build()
    }
    return ResponseEntity.ok(videoFiles)
  }

  @GetMapping("/{videoFileId}")
  fun getVideoFile(@PathVariable videoFileId: Long): ResponseEntity<VideoFilePayload> {
    return okOrNoContent(videoFileService.getVideoFile(videoFileId))
  }

  @Secured(ROLE_EDIT)
  @PostMapping
  fun addVideoFile(@RequestBody videoFile: VideoFilePayload): ResponseEntity<VideoFilePayload> {
    return ResponseEntity.ok(videoFileService.addVideoFile(videoFile))
  }

  @Secured(ROLE_EDIT)
  @PutMapping("/{videoFileId}")
  fun updateVideoFile(
    @PathVariable videoFileId: Long,
    @RequestBody videoFile: VideoFilePayload
  ): ResponseEntity<VideoFilePayload> {
    return okOrNoContent(videoFileService.updateVideoFile(videoFileId, videoFile))
  }

  @Secured(ROLE_EDIT)
  @DeleteMapping("/{videoFileId}")
  fun deleteVideoFile(@PathVariable videoFileId: Long): ResponseEntity<VideoFilePayload> {
    return okOrNoContent(videoFileService.deleteVideoFile(videoFileId))
  }

  @Secured(ROLE_SCAN)
  @PostMapping("/scanner")
  fun startVideoFileScan(): ResponseEntity<FileScanStatusResponse> {
    val status = videoFileService.startVideoFileScan()
    if (status.alreadyRunning) {
      logger.warn("Video scanner already running, cannot start it again")
      return ResponseEntity.badRequest().body(status)
    }
    return ResponseEntity.ok(status)
  }

  @GetMapping("/scanner")
  fun isVideoFileScanRunning(): ResponseEntity<FileScanStatusResponse> {
    return ResponseEntity.ok(videoFileService.isVideoFileScanRunning())
  }

  @GetMapping("/play/{fileId}")
  @PreAuthorize("hasAuthority('file_' + #fileId)")
  fun playVideo(
    @PathVariable fileId: Long,
    @RequestHeader headers: HttpHeaders
  ): ResponseEntity<ResourceRegion> {
    val video = videoFileService.playVideo(fileId)
    val region = resourceRegion(video, headers)
    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
      .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
      .body(region)
  }

  @GetMapping("/record-play/{fileId}")
  fun recordNewVideoPlay(@PathVariable fileId: Long): ResponseEntity<Void> {
    videoFileService.recordNewVideoPlay(fileId)
    return ResponseEntity.ok().build()
  }

  @PostMapping("/search")
  fun searchForVideos(
    @RequestBody search: VideoSearchRequest
  ): ResponseEntity<VideoSearchResponse> {
    cleanUpSearch(search)
    val videoFiles = videoFileService.searchForVideos(search)
    if (videoFiles.videoList.isEmpty()) {
      return ResponseEntity.noContent().build()
    }
    return ResponseEntity.ok(videoFiles)
  }
}
