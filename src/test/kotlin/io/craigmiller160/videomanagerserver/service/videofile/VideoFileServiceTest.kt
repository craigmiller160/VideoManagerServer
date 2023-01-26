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

import com.nhaarman.mockito_kotlin.*
import io.craigmiller160.videomanagerserver.config.MapperConfig
import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.*
import io.craigmiller160.videomanagerserver.entity.IsScanning
import io.craigmiller160.videomanagerserver.entity.VideoFile
import io.craigmiller160.videomanagerserver.exception.InvalidSettingException
import io.craigmiller160.videomanagerserver.file.FileScanner
import io.craigmiller160.videomanagerserver.repository.*
import io.craigmiller160.videomanagerserver.repository.query.SearchQueryBuilder
import io.craigmiller160.videomanagerserver.security.VideoTokenAuthentication
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import io.craigmiller160.videomanagerserver.test_util.isA
import io.craigmiller160.videomanagerserver.util.DEFAULT_TIMESTAMP
import java.nio.file.Files
import java.util.Optional
import javax.persistence.EntityManager
import javax.persistence.Query
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.core.userdetails.User

@ExtendWith(MockitoExtension::class)
class VideoFileServiceTest {

  companion object {

    private const val FIRST_NAME = "FirstName"
    private const val SECOND_NAME = "SecondName"
    private const val THIRD_NAME = "ThirdName"
    private const val ROOT_DIR = "rootDir"

    private val expectedFiles =
      listOf(
        VideoFile(fileId = 1, fileName = FIRST_NAME), VideoFile(fileId = 2, fileName = SECOND_NAME))

    private val expectedFilePayloads =
      listOf(
        VideoFilePayload(fileId = 1, fileName = FIRST_NAME),
        VideoFilePayload(fileId = 2, fileName = SECOND_NAME))

    private val expectedFilesPage = PageImpl(expectedFiles)
  }

  @Mock private lateinit var searchQueryBuilder: SearchQueryBuilder
  @Mock private lateinit var videoFileRepo: VideoFileRepository
  @Mock private lateinit var isScanningRepo: IsScanningRepository
  @Spy private lateinit var videoConfig: VideoConfiguration
  @Mock private lateinit var fileScanner: FileScanner
  @Mock private lateinit var entityManager: EntityManager
  @Mock private lateinit var settingsService: SettingsService
  @Mock private lateinit var fileCategoryRepo: FileCategoryRepository
  @Mock private lateinit var fileStarRepo: FileStarRepository
  @Mock private lateinit var fileSeriesRepo: FileSeriesRepository
  @InjectMocks private lateinit var videoFileService: VideoFileService
  @Spy private var modelMapper = MapperConfig().modelMapper()

  @BeforeEach
  fun setup() {
    SecurityContextHolder.clearContext()

    videoConfig.apiPageSize = 10
  }

  @AfterEach
  fun cleanup() {
    SecurityContextHolder.clearContext()
  }

  @Test
  fun test_getAllVideoFiles() {
    Mockito.`when`(videoFileRepo.findAll(isA(Pageable::class.java))).thenReturn(expectedFilesPage)

    Mockito.`when`(videoConfig.apiPageSize).thenReturn(20)

    val actualFiles = videoFileService.getAllVideoFiles(1, Sort.Direction.DESC.toString())
    assertNotNull(actualFiles)
    assertEquals(expectedFilePayloads.size, actualFiles.size)
    assertEquals(expectedFilePayloads, actualFiles)
  }

  @Test
  fun test_getVideoFile() {
    Mockito.`when`(videoFileRepo.findById(1)).thenReturn(Optional.of(expectedFiles[0]))
    Mockito.`when`(videoFileRepo.findById(2)).thenReturn(Optional.of(expectedFiles[1]))

    var actualFile = videoFileService.getVideoFile(1)
    assertNotNull(actualFile)
    assertEquals(expectedFilePayloads[0], actualFile)

    actualFile = videoFileService.getVideoFile(2)
    assertNotNull(actualFile)
    assertEquals(expectedFilePayloads[1], actualFile)

    actualFile = videoFileService.getVideoFile(3)
    assertNull(actualFile)
  }

  @Test
  fun testAddVideoFile() {
    val newFile = VideoFile(fileName = THIRD_NAME, active = true)
    val newFileWithId = VideoFile(fileId = 3, fileName = THIRD_NAME, active = true)
    val newFilePayload = VideoFilePayload(fileName = THIRD_NAME)
    val newFilePayloadWithId = VideoFilePayload(fileId = 3, fileName = THIRD_NAME)

    Mockito.`when`(videoFileRepo.save(newFile)).thenReturn(newFileWithId)

    val actualFile = videoFileService.addVideoFile(newFilePayload)
    assertEquals(newFilePayloadWithId, actualFile)
  }

  @Test
  fun testUpdateVideoFile() {
    val newFileWithId = VideoFile(fileId = 1, fileName = "foo", active = true)
    val newFileToSave = VideoFile(fileId = 1, fileName = THIRD_NAME, active = true)
    val newFilePayload = VideoFilePayload(fileName = THIRD_NAME)
    val newFilePayloadWithId = VideoFilePayload(fileId = 1, fileName = THIRD_NAME)

    Mockito.`when`(videoFileRepo.save(newFileToSave)).thenReturn(newFileToSave)
    Mockito.`when`(videoFileRepo.findById(1)).thenReturn(Optional.of(newFileWithId))

    var actualFile = videoFileService.updateVideoFile(1, newFilePayload)
    assertNotNull(actualFile)
    assertEquals(newFilePayloadWithId, actualFile)

    actualFile = videoFileService.updateVideoFile(3, newFilePayload)
    assertNull(actualFile)
  }

  @Test
  fun test_deleteVideoFile() {
    val tempDir = Files.createTempDirectory("a")
    val filePath = tempDir.resolve("file.txt")
    Files.write(filePath, "Hello World".toByteArray())
    val videoFile = expectedFiles[0].copy(fileName = "file.txt")

    assertTrue { Files.exists(filePath) }

    whenever(videoFileRepo.findById(1))
      .thenReturn(Optional.of(videoFile))
      .thenReturn(Optional.empty())

    whenever(settingsService.getOrCreateSettings())
      .thenReturn(SettingsPayload(rootDir = tempDir.toString()))

    var actualFile = videoFileService.deleteVideoFile(1)
    assertNotNull(actualFile)
    assertEquals(modelMapper.map(videoFile, VideoFilePayload::class.java), actualFile)

    assertFalse { Files.exists(filePath) }

    actualFile = videoFileService.deleteVideoFile(1)
    assertNull(actualFile)

    verify(videoFileRepo, Mockito.times(2)).deleteById(1)
    verify(fileCategoryRepo, Mockito.times(2)).deleteAllByFileId(1)
    verify(fileStarRepo, Mockito.times(2)).deleteAllByFileId(1)
    verify(fileSeriesRepo, Mockito.times(2)).deleteAllByFileId(1)
  }

  @Test
  fun testStartVideoFileScan() {
    whenever(isScanningRepo.findById(1L)).thenReturn(Optional.of(IsScanning(id = 1L)))

    val status = videoFileService.startVideoFileScan()
    assertThat(
      status,
      Matchers.allOf(
        Matchers.hasProperty("inProgress", Matchers.equalTo(true)),
        Matchers.hasProperty("alreadyRunning", Matchers.equalTo(false)),
        Matchers.hasProperty("message", Matchers.equalTo(SCAN_STATUS_RUNNING)),
        Matchers.hasProperty("scanError", Matchers.equalTo(false))))

    verify(fileScanner, Mockito.times(1)).scanForFiles(any())
  }

  @Test
  fun test_startVideoFileScan_scanError() {
    whenever(isScanningRepo.findById(1L)).thenReturn(Optional.of(IsScanning(id = 1L)))
    whenever(fileScanner.scanForFiles(any())).thenThrow(InvalidSettingException())

    var exception: Exception? = null

    try {
      videoFileService.startVideoFileScan()
    } catch (ex: Exception) {
      exception = ex
    }

    val captor = argumentCaptor<IsScanning>()

    assertNotNull(exception)
    verify(isScanningRepo, times(2)).save(captor.capture())

    assertEquals(2, captor.allValues.size)
    val errorIsScanning = captor.allValues[1]
    assertThat(errorIsScanning)
      .hasFieldOrPropertyWithValue("isScanning", false)
      .hasFieldOrPropertyWithValue("lastScanSuccess", false)
  }

  @Test
  fun test_startVideoFileScan_scanRunning() {
    whenever(isScanningRepo.findById(1L))
      .thenReturn(Optional.of(IsScanning(id = 1L, isScanning = true)))
    val expectedStatus = createScanAlreadyRunningStatus()
    val result = videoFileService.startVideoFileScan()
    assertEquals(expectedStatus, result)
  }

  @Test
  fun test_startVideoFileScan_optimisticLockingException() {
    whenever(isScanningRepo.findById(1L)).thenReturn(Optional.of(IsScanning(id = 1L)))
    whenever(isScanningRepo.save(any<IsScanning>()))
      .thenThrow(OptimisticLockingFailureException("Dying"))

    val expectedStatus = createScanAlreadyRunningStatus()
    val result = videoFileService.startVideoFileScan()
    assertEquals(expectedStatus, result)
  }

  @Test
  fun testIsVideoFileScanRunning() {
    whenever(isScanningRepo.findById(1L)).thenReturn(Optional.of(IsScanning(id = 1L)))
    var status = videoFileService.isVideoFileScanRunning()
    assertThat(
      status,
      Matchers.allOf(
        Matchers.hasProperty("inProgress", Matchers.equalTo(false)),
        Matchers.hasProperty("alreadyRunning", Matchers.equalTo(false)),
        Matchers.hasProperty("message", Matchers.equalTo(SCAN_STATUS_NOT_RUNNING)),
        Matchers.hasProperty("scanError", Matchers.equalTo(false))))

    whenever(isScanningRepo.findById(1L))
      .thenReturn(Optional.of(IsScanning(id = 1L, isScanning = true)))

    status = videoFileService.isVideoFileScanRunning()
    assertThat(
      status,
      Matchers.allOf(
        Matchers.hasProperty("inProgress", Matchers.equalTo(true)),
        Matchers.hasProperty("alreadyRunning", Matchers.equalTo(false)),
        Matchers.hasProperty("message", Matchers.equalTo(SCAN_STATUS_RUNNING)),
        Matchers.hasProperty("scanError", Matchers.equalTo(false))))

    whenever(isScanningRepo.findById(1L))
      .thenReturn(Optional.of(IsScanning(id = 1L, lastScanSuccess = false)))

    status = videoFileService.isVideoFileScanRunning()
    assertThat(
      status,
      Matchers.allOf(
        Matchers.hasProperty("inProgress", Matchers.equalTo(false)),
        Matchers.hasProperty("alreadyRunning", Matchers.equalTo(false)),
        Matchers.hasProperty("message", Matchers.equalTo(SCAN_STATUS_ERROR)),
        Matchers.hasProperty("scanError", Matchers.equalTo(true))))
  }

  @Test
  fun test_playVideo() {
    val path = "$ROOT_DIR/${expectedFiles[0].fileName}"
    val claims = mapOf(TokenConstants.CLAIM_FILE_PATH to path)
    val details = User.withUsername("Hello").password("World").authorities(emptyList()).build()
    val auth = VideoTokenAuthentication(details, claims)
    val context = SecurityContextImpl(auth)
    SecurityContextHolder.setContext(context)

    val video = videoFileService.playVideo(expectedFiles[0].fileId)

    assertThat(video.file.absolutePath, Matchers.containsString(path))
  }

  @Test
  fun test_recordNewVideoPlay() {
    Mockito.`when`(videoFileRepo.findById(1L)).thenReturn(Optional.of(expectedFiles[0].copy()))

    videoFileService.recordNewVideoPlay(1L)

    val argumentCaptor =
      argumentCaptor<VideoFile>().apply { verify(videoFileRepo, Mockito.times(1)).save(capture()) }

    assertEquals(1, argumentCaptor.allValues.size)
    assertThat(
      argumentCaptor.firstValue,
      Matchers.allOf(
        Matchers.hasProperty("viewCount", Matchers.equalTo(1)),
        Matchers.hasProperty("lastViewed", Matchers.greaterThan(DEFAULT_TIMESTAMP))))
  }

  @Test
  fun test_searchForVideos() {
    val search = VideoSearchRequest(page = 1)
    val searchQueryString = "searchQueryString"
    val countQueryString = "countQueryString"
    Mockito.`when`(searchQueryBuilder.buildEntitySearchQuery(search)).thenReturn(searchQueryString)
    Mockito.`when`(searchQueryBuilder.buildCountSearchQuery(search)).thenReturn(countQueryString)

    val searchQuery = Mockito.mock(Query::class.java)
    val countQuery = Mockito.mock(Query::class.java)

    Mockito.`when`(entityManager.createQuery(searchQueryString)).thenReturn(searchQuery)
    Mockito.`when`(entityManager.createQuery(countQueryString)).thenReturn(countQuery)

    Mockito.`when`(searchQuery.setFirstResult(10)).thenReturn(searchQuery)
    Mockito.`when`(searchQuery.setMaxResults(10)).thenReturn(searchQuery)

    val dbResultList = listOf(VideoFile())
    val resultList = listOf(VideoFilePayload())
    Mockito.`when`(searchQuery.resultList).thenReturn(dbResultList)

    Mockito.`when`(countQuery.singleResult).thenReturn(10L)

    val results = videoFileService.searchForVideos(search)
    assertThat(
      results,
      Matchers.allOf(
        Matchers.hasProperty("totalFiles", Matchers.equalTo(10L)),
        Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
        Matchers.hasProperty("currentPage", Matchers.equalTo(1)),
        Matchers.hasProperty("videoList", Matchers.equalTo(resultList))))
    verify(searchQueryBuilder, Mockito.times(1)).buildEntitySearchQuery(search)
    verify(searchQueryBuilder, Mockito.times(1)).buildCountSearchQuery(search)
    verify(searchQueryBuilder, Mockito.times(1)).addParamsToQuery(search, searchQuery)
    verify(searchQueryBuilder, Mockito.times(1)).addParamsToQuery(search, countQuery)
  }
}
