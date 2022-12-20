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

import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.*
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import io.craigmiller160.videomanagerserver.service.videofile.VideoFileService
import io.craigmiller160.videomanagerserver.test_util.JwtUtils
import io.craigmiller160.videomanagerserver.test_util.isA
import java.io.File
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.UrlResource
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class VideoFileControllerTest : AbstractControllerTest() {

  @MockBean private lateinit var videoFileService: VideoFileService

  @Autowired private lateinit var videoFileController: VideoFileController

  private lateinit var jacksonVideoFileList: JacksonTester<List<VideoFilePayload>>
  private lateinit var jacksonVideoFile: JacksonTester<VideoFilePayload>
  private lateinit var jacksonStatus: JacksonTester<FileScanStatusResponse>
  private lateinit var jacksonSearch: JacksonTester<VideoSearchRequest>
  private lateinit var jacksonVideoSearchResults: JacksonTester<VideoSearchResponse>

  private lateinit var videoFileNoId: VideoFilePayload
  private lateinit var videoFile1: VideoFilePayload
  private lateinit var videoFile2: VideoFilePayload
  private lateinit var videoFile3: VideoFilePayload
  private lateinit var videoFileList: List<VideoFilePayload>
  private lateinit var videoSearchResults: VideoSearchResponse
  private lateinit var scanRunning: FileScanStatusResponse
  private lateinit var scanNotRunning: FileScanStatusResponse
  private lateinit var scanAlreadyRunning: FileScanStatusResponse

  @Autowired private lateinit var videoTokenProvider: VideoTokenProvider

  @Autowired private lateinit var tokenConfig: TokenConfig

  @Before
  override fun setup() {
    super.setup()
    videoFileNoId = VideoFilePayload(fileName = "NoId")
    videoFile1 = VideoFilePayload(1, "FirstFile")
    videoFile2 = VideoFilePayload(2, "SecondFile")
    videoFile3 = VideoFilePayload(3, "ThirdFile")
    videoFileList = listOf(videoFile1, videoFile2, videoFile3)
    videoSearchResults =
      VideoSearchResponse().apply {
        videoList = videoFileList
        totalFiles = 3
        filesPerPage = 3
        currentPage = 0
      }

    scanRunning = createScanRunningStatus()
    scanNotRunning = createScanNotRunningStatus()
    scanAlreadyRunning = createScanAlreadyRunningStatus()
  }

  @Test
  fun testGetAllVideoFiles() {
    mockMvcHandler.token = token
    `when`(videoFileService.getAllVideoFiles(anyInt(), anyString()))
      .thenReturn(videoFileList)
      .thenReturn(listOf())

    var response = mockMvcHandler.doGet("/api/video-files")
    assertOkResponse(response, jacksonVideoFileList.write(videoFileList).json)

    response = mockMvcHandler.doGet("/api/video-files")
    assertNoContentResponse(response)

    response = mockMvcHandler.doGet("/api/video-files?page=0&sortDirection=FooBar")
    assertBadRequest(response)
  }

  @Test
  fun test_getAllVideoFiles_unauthorized() {
    val response = mockMvcHandler.doGet("/api/video-files?page=0&sortDirection=FooBar")
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun testGetVideoFile() {
    mockMvcHandler.token = token
    `when`(videoFileService.getVideoFile(1)).thenReturn(videoFile1)
    `when`(videoFileService.getVideoFile(5)).thenReturn(null)

    var response = mockMvcHandler.doGet("/api/video-files/1")
    assertOkResponse(response, jacksonVideoFile.write(videoFile1).json)

    response = mockMvcHandler.doGet("/api/video-files/5")
    assertNoContentResponse(response)
  }

  @Test
  fun test_getVideoFile_unauthorized() {
    val response = mockMvcHandler.doGet("/api/video-files/1")
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun testAddVideoFile() {
    mockMvcHandler.token = editToken
    val videoFileWithId = videoFileNoId.copy(fileId = 1)
    `when`(videoFileService.addVideoFile(videoFileNoId)).thenReturn(videoFileWithId)

    val response =
      mockMvcHandler.doPost("/api/video-files", jacksonVideoFile.write(videoFileNoId).json)
    assertOkResponse(response, jacksonVideoFile.write(videoFileWithId).json)
  }

  @Test
  fun test_addVideoFile_unauthorized() {
    val response =
      mockMvcHandler.doPost("/api/video-files", jacksonVideoFile.write(videoFileNoId).json)
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun test_addVideoFile_missingRole() {
    mockMvcHandler.token = token

    val response =
      mockMvcHandler.doPost("/api/video-files", jacksonVideoFile.write(videoFileNoId).json)
    assertThat(response, hasProperty("status", equalTo(403)))
  }

  @Test
  fun testUpdateVideoFile() {
    mockMvcHandler.token = editToken
    val updatedVideoFile = videoFile2.copy(fileId = 1)
    `when`(videoFileService.updateVideoFile(1, videoFile2)).thenReturn(updatedVideoFile)
    `when`(videoFileService.updateVideoFile(5, videoFile3)).thenReturn(null)

    var response =
      mockMvcHandler.doPut("/api/video-files/1", jacksonVideoFile.write(videoFile2).json)
    assertOkResponse(response, jacksonVideoFile.write(updatedVideoFile).json)

    response = mockMvcHandler.doPut("/api/video-files/5", jacksonVideoFile.write(videoFile3).json)
    assertNoContentResponse(response)
  }

  @Test
  fun test_updateVideoFile_unauthorized() {
    val response =
      mockMvcHandler.doPut("/api/video-files/1", jacksonVideoFile.write(videoFile2).json)
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun test_updateVideoFile_missingRole() {
    mockMvcHandler.token = token

    val response =
      mockMvcHandler.doPut("/api/video-files/1", jacksonVideoFile.write(videoFile2).json)
    assertThat(response, hasProperty("status", equalTo(403)))
  }

  @Test
  fun testDeleteVideoFile() {
    mockMvcHandler.token = editToken
    `when`(videoFileService.deleteVideoFile(1)).thenReturn(videoFile1).thenReturn(null)

    var response = mockMvcHandler.doDelete("/api/video-files/1")
    assertOkResponse(response, jacksonVideoFile.write(videoFile1).json)

    response = mockMvcHandler.doDelete("/api/video-files/5")
    assertNoContentResponse(response)
  }

  @Test
  fun test_deleteVideoFile_unauthorized() {
    val response = mockMvcHandler.doDelete("/api/video-files/1")
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun test_deleteVideoFile_missingRole() {
    mockMvcHandler.token = token

    val response = mockMvcHandler.doDelete("/api/video-files/1")
    assertThat(response, hasProperty("status", equalTo(403)))
  }

  @Test
  fun test_StartVideoScan() {
    mockMvcHandler.token = scanToken
    `when`(videoFileService.startVideoFileScan())
      .thenReturn(scanRunning)
      .thenReturn(scanAlreadyRunning)

    var response = mockMvcHandler.doPost("/api/video-files/scanner")
    assertOkResponse(response, jacksonStatus.write(scanRunning).json)

    response = mockMvcHandler.doPost("/api/video-files/scanner")
    assertBadRequest(response, jacksonStatus.write(scanAlreadyRunning).json)
  }

  @Test
  fun test_startVideoScan_missingRole() {
    mockMvcHandler.token = token
    `when`(videoFileService.startVideoFileScan())
      .thenReturn(scanRunning)
      .thenReturn(scanAlreadyRunning)

    val response = mockMvcHandler.doPost("/api/video-files/scanner")
    assertThat(response, hasProperty("status", equalTo(403)))
  }

  @Test
  fun test_startVideoScan_unauthorized() {
    val response = mockMvcHandler.doPost("/api/video-files/scanner")
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun testIsVideoScanRunning() {
    mockMvcHandler.token = token
    `when`(videoFileService.isVideoFileScanRunning())
      .thenReturn(scanNotRunning)
      .thenReturn(scanRunning)

    var response = mockMvcHandler.doGet("/api/video-files/scanner")
    assertOkResponse(response, jacksonStatus.write(scanNotRunning).json)

    response = mockMvcHandler.doGet("/api/video-files/scanner")
    assertOkResponse(response, jacksonStatus.write(scanRunning).json)
  }

  @Test
  fun test_isVideoScanRunning_unauthorized() {
    val response = mockMvcHandler.doGet("/api/video-files/scanner")
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun testSearchForVideos() {
    mockMvcHandler.token = token
    `when`(videoFileService.searchForVideos(isA(VideoSearchRequest::class.java)))
      .thenReturn(videoSearchResults)
      .thenReturn(VideoSearchResponse())

    val search = VideoSearchRequest("HelloWorld")

    var response =
      mockMvcHandler.doPost("/api/video-files/search", jacksonSearch.write(search).json)
    assertOkResponse(response, jacksonVideoSearchResults.write(videoSearchResults).json)

    response = mockMvcHandler.doPost("/api/video-files/search", jacksonSearch.write(search).json)
    assertNoContentResponse(response)
  }

  @Test
  fun test_searchForVideos_unauthorized() {
    val search = VideoSearchRequest("HelloWorld")
    val response =
      mockMvcHandler.doPost("/api/video-files/search", jacksonSearch.write(search).json)
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun test_playVideo() {
    mockMvcHandler.token = token
    val params =
      mapOf(
        TokenConstants.PARAM_VIDEO_ID to "1",
        TokenConstants.PARAM_FILE_PATH to "/foo/bar",
        TokenConstants.PARAM_USER_ID to "1")
    val token = videoTokenProvider.createToken("user", params)
    val file = File(".")
    `when`(videoFileService.playVideo(1L)).thenReturn(UrlResource(file.toURI()))
    val response =
      mockMvcHandler.doGet(
        "/api/video-files/play/1?${TokenConstants.QUERY_PARAM_VIDEO_TOKEN}=$token")
    assertEquals(206, response.status)
    assertTrue(response.contentAsByteArray.isNotEmpty())
  }

  @Test
  fun test_playVideo_noJwt_withVideoToken() {
    val params =
      mapOf(
        TokenConstants.PARAM_VIDEO_ID to "1",
        TokenConstants.PARAM_FILE_PATH to "/foo/bar",
        TokenConstants.PARAM_USER_ID to "1")
    val token = videoTokenProvider.createToken("user", params)
    val file = File(".")
    `when`(videoFileService.playVideo(1L)).thenReturn(UrlResource(file.toURI()))
    val response =
      mockMvcHandler.doGet(
        "/api/video-files/play/1?${TokenConstants.QUERY_PARAM_VIDEO_TOKEN}=$token")
    assertEquals(403, response.status)
  }

  @Test
  fun test_playVideo_expiredJwt_validVideoToken() {
    val expiredToken =
      JwtUtils.createJwt(-10).let { JwtUtils.signAndSerializeJwt(it, keyPair.private) }
    mockMvcHandler.token = expiredToken

    val params =
      mapOf(
        TokenConstants.PARAM_VIDEO_ID to "1",
        TokenConstants.PARAM_FILE_PATH to "/foo/bar",
        TokenConstants.PARAM_USER_ID to "1")
    val token = videoTokenProvider.createToken("user", params)
    val file = File(".")
    `when`(videoFileService.playVideo(1L)).thenReturn(UrlResource(file.toURI()))
    val response =
      mockMvcHandler.doGet(
        "/api/video-files/play/1?${TokenConstants.QUERY_PARAM_VIDEO_TOKEN}=$token")
    assertEquals(206, response.status)
    assertTrue(response.contentAsByteArray.isNotEmpty())
  }

  @Test
  fun test_playVideo_unauthorized() {
    val response = mockMvcHandler.doGet("/api/video-files/play/1")
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun test_playVideo_wrongUser() {
    mockMvcHandler.token = token
    val params =
      mapOf(
        TokenConstants.PARAM_VIDEO_ID to "1",
        TokenConstants.PARAM_FILE_PATH to "/foo/bar",
        TokenConstants.PARAM_USER_ID to "2")
    val token = videoTokenProvider.createToken("user", params)
    val file = File(".")
    `when`(videoFileService.playVideo(1L)).thenReturn(UrlResource(file.toURI()))
    val response =
      mockMvcHandler.doGet(
        "/api/video-files/play/1?${TokenConstants.QUERY_PARAM_VIDEO_TOKEN}=$token")
    assertEquals(403, response.status)
  }

  @Test
  fun test_recordNewVideoPlay() {
    mockMvcHandler.token = token
    val response = mockMvcHandler.doGet("/api/video-files/record-play/1")
    assertEquals(200, response.status)

    verify(videoFileService, times(1)).recordNewVideoPlay(1L)
  }

  @Test
  fun test_recordNewVideoPlay_unauthorized() {
    val response = mockMvcHandler.doGet("/api/video-files/record-play/1")
    assertThat(response, hasProperty("status", equalTo(401)))
  }
}
