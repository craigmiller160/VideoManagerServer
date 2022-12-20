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

package io.craigmiller160.videomanagerserver.service.videofile

import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.FileScanStatusResponse
import io.craigmiller160.videomanagerserver.dto.VideoFilePayload
import io.craigmiller160.videomanagerserver.dto.VideoSearchRequest
import io.craigmiller160.videomanagerserver.dto.VideoSearchResponse
import io.craigmiller160.videomanagerserver.dto.createScanAlreadyRunningStatus
import io.craigmiller160.videomanagerserver.dto.createScanErrorStatus
import io.craigmiller160.videomanagerserver.dto.createScanNotRunningStatus
import io.craigmiller160.videomanagerserver.dto.createScanRunningStatus
import io.craigmiller160.videomanagerserver.entity.IsScanning
import io.craigmiller160.videomanagerserver.entity.VideoFile
import io.craigmiller160.videomanagerserver.exception.VideoFileNotFoundException
import io.craigmiller160.videomanagerserver.file.FileScanner
import io.craigmiller160.videomanagerserver.mapper.VMModelMapper
import io.craigmiller160.videomanagerserver.repository.*
import io.craigmiller160.videomanagerserver.repository.query.SearchQueryBuilder
import io.craigmiller160.videomanagerserver.security.VideoTokenAuthentication
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import java.io.File
import java.lang.IllegalStateException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import javax.persistence.EntityManager
import org.springframework.core.io.UrlResource
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class VideoFileService(
  private val videoFileRepo: VideoFileRepository,
  private val videoConfig: VideoConfiguration,
  private val fileScanner: FileScanner,
  private val entityManager: EntityManager,
  private val searchQueryBuilder: SearchQueryBuilder,
  private val fileCategoryRepo: FileCategoryRepository,
  private val fileSeriesRepo: FileSeriesRepository,
  private val fileStarRepo: FileStarRepository,
  private val modelMapper: VMModelMapper,
  private val isScanningRepo: IsScanningRepository,
  private val settingsService: SettingsService
) {

  private fun getIsScanning(): IsScanning =
    isScanningRepo.findById(1L).orElseThrow {
      IllegalStateException("Missing required Is Scanning record")
    }

  private fun getVideoFileSort(sortDirection: Sort.Direction): Sort {
    return Sort.by(
      Sort.Order(sortDirection, "displayName", Sort.NullHandling.NULLS_FIRST),
      Sort.Order(sortDirection, "fileName", Sort.NullHandling.NULLS_FIRST))
  }

  fun getAllVideoFiles(page: Int, sortDirection: String): List<VideoFilePayload> {
    val sort = getVideoFileSort(Sort.Direction.valueOf(value = sortDirection))
    val pageable = PageRequest.of(page, videoConfig.apiPageSize, sort)
    return videoFileRepo
      .findAll(pageable)
      .stream()
      .map { file -> modelMapper.map(file, VideoFilePayload::class.java) }
      .toList()
  }

  fun getVideoFile(fileId: Long): VideoFilePayload? {
    return videoFileRepo
      .findById(fileId)
      .map { file -> modelMapper.map(file, VideoFilePayload::class.java) }
      .orElse(null)
  }

  fun addVideoFile(payload: VideoFilePayload): VideoFilePayload {
    val videoFile = modelMapper.map(payload, VideoFile::class.java)
    videoFile.active = true
    val savedVideoFile = videoFileRepo.save(videoFile)
    return modelMapper.map(savedVideoFile, VideoFilePayload::class.java)
  }

  fun updateVideoFile(fileId: Long, payload: VideoFilePayload): VideoFilePayload? {
    return videoFileRepo
      .findById(fileId)
      .map { existingFile ->
        val videoFile = modelMapper.mapFromExisting(payload, existingFile)
        videoFile.fileId = fileId
        videoFile.viewCount = existingFile.viewCount
        videoFile.lastViewed = existingFile.lastViewed
        val savedVideoFile = videoFileRepo.save(videoFile)
        modelMapper.map(savedVideoFile, VideoFilePayload::class.java)
      }
      .orElse(null)
  }

  fun deleteVideoFile(fileId: Long): VideoFilePayload? {
    val videoFileOptional = videoFileRepo.findById(fileId)
    fileCategoryRepo.deleteAllByFileId(fileId)
    fileStarRepo.deleteAllByFileId(fileId)
    fileSeriesRepo.deleteAllByFileId(fileId)
    videoFileRepo.deleteById(fileId)
    videoFileOptional.ifPresent { deleteFile(it.fileName) }
    return videoFileOptional
      .map { file -> modelMapper.map(file, VideoFilePayload::class.java) }
      .orElse(null)
  }

  private fun deleteFile(filePath: String) {
    val rootDir = settingsService.getOrCreateSettings().rootDir
    val path = Paths.get(rootDir, filePath)
    Files.deleteIfExists(path)
  }

  fun startVideoFileScan(): FileScanStatusResponse {
    val isScanning = getIsScanning()
    if (isScanning.isScanning) {
      return createScanAlreadyRunningStatus()
    }
    isScanning.isScanning = true
    isScanning.lastScanSuccess = true
    try {
      isScanningRepo.save(isScanning)
    } catch (ex: OptimisticLockingFailureException) {
      return createScanAlreadyRunningStatus()
    }

    try {
      fileScanner.scanForFiles { result ->
        val isScanningForResult = getIsScanning()
        isScanningForResult.isScanning = false
        isScanningForResult.lastScanSuccess = result
        isScanningRepo.save(isScanningForResult)
      }
    } catch (ex: Exception) {
      val isScanningForError = getIsScanning()
      isScanningForError.isScanning = false
      isScanningForError.lastScanSuccess = false
      isScanningRepo.save(isScanningForError)
      throw ex
    }

    return createScanRunningStatus()
  }

  fun isVideoFileScanRunning(): FileScanStatusResponse {
    val isScanning = getIsScanning()
    if (isScanning.isScanning) {
      return createScanRunningStatus()
    }

    if (isScanning.lastScanSuccess) {
      return createScanNotRunningStatus()
    }

    return createScanErrorStatus()
  }

  fun playVideo(fileId: Long): UrlResource {
    val auth = SecurityContextHolder.getContext().authentication as VideoTokenAuthentication
    return UrlResource(File(auth.filePath).toURI())
  }

  fun recordNewVideoPlay(fileId: Long) {
    val dbVideoFile =
      videoFileRepo.findById(fileId).orElseThrow {
        VideoFileNotFoundException("Could not find video file in DB by ID: $fileId")
      }
    dbVideoFile.viewCount++
    dbVideoFile.lastViewed = LocalDateTime.now()
    videoFileRepo.save(dbVideoFile)
  }

  fun searchForVideos(search: VideoSearchRequest): VideoSearchResponse {
    val page = search.page
    val pageSize = videoConfig.apiPageSize

    val searchQueryString = searchQueryBuilder.buildEntitySearchQuery(search)
    val countQueryString = searchQueryBuilder.buildCountSearchQuery(search)

    val searchQuery = entityManager.createQuery(searchQueryString)
    val countQuery = entityManager.createQuery(countQueryString)
    searchQueryBuilder.addParamsToQuery(search, searchQuery)
    searchQueryBuilder.addParamsToQuery(search, countQuery)

    val videoList =
      searchQuery.setFirstResult(page * pageSize).setMaxResults(pageSize).resultList
        as List<VideoFile>
    val totalCount = countQuery.singleResult as Long

    return VideoSearchResponse().apply {
      totalFiles = totalCount
      filesPerPage = pageSize
      currentPage = page
      this.videoList = videoList.map { file -> modelMapper.map(file, VideoFilePayload::class.java) }
    }
  }
}
