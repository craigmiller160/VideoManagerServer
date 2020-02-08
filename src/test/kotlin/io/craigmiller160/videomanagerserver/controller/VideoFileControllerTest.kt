package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.FileScanStatusResponse
import io.craigmiller160.videomanagerserver.dto.VideoFilePayload
import io.craigmiller160.videomanagerserver.dto.VideoSearch
import io.craigmiller160.videomanagerserver.dto.VideoSearchResults
import io.craigmiller160.videomanagerserver.dto.createScanAlreadyRunningStatus
import io.craigmiller160.videomanagerserver.dto.createScanNotRunningStatus
import io.craigmiller160.videomanagerserver.dto.createScanRunningStatus
import io.craigmiller160.videomanagerserver.entity.AppUser
import io.craigmiller160.videomanagerserver.entity.Role
import io.craigmiller160.videomanagerserver.security.ROLE_EDIT
import io.craigmiller160.videomanagerserver.security.ROLE_SCAN
import io.craigmiller160.videomanagerserver.security.tokenprovider.JwtTokenProvider
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import io.craigmiller160.videomanagerserver.service.videofile.VideoFileService
import io.craigmiller160.videomanagerserver.test_util.isA
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.UrlResource
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import java.io.File
import java.util.Optional

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class VideoFileControllerTest : AbstractControllerTest() {

    @MockBean
    private lateinit var videoFileService: VideoFileService

    @Autowired
    private lateinit var videoFileController: VideoFileController

    private lateinit var jacksonVideoFileList: JacksonTester<List<VideoFilePayload>>
    private lateinit var jacksonVideoFile: JacksonTester<VideoFilePayload>
    private lateinit var jacksonStatus: JacksonTester<FileScanStatusResponse>
    private lateinit var jacksonSearch: JacksonTester<VideoSearch>
    private lateinit var jacksonVideoSearchResults: JacksonTester<VideoSearchResults>

    private lateinit var videoFileNoId: VideoFilePayload
    private lateinit var videoFile1: VideoFilePayload
    private lateinit var videoFile2: VideoFilePayload
    private lateinit var videoFile3: VideoFilePayload
    private lateinit var videoFileList: List<VideoFilePayload>
    private lateinit var videoSearchResults: VideoSearchResults
    private lateinit var scanRunning: FileScanStatusResponse
    private lateinit var scanNotRunning: FileScanStatusResponse
    private lateinit var scanAlreadyRunning: FileScanStatusResponse

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var videoTokenProvider: VideoTokenProvider

    @Autowired
    private lateinit var tokenConfig: TokenConfig

    @Before
    override fun setup() {
        super.setup()
        videoFileNoId = VideoFilePayload(fileName = "NoId")
        videoFile1 = VideoFilePayload(1, "FirstFile")
        videoFile2 = VideoFilePayload(2, "SecondFile")
        videoFile3 = VideoFilePayload(3, "ThirdFile")
        videoFileList = listOf(videoFile1, videoFile2, videoFile3)
        videoSearchResults = VideoSearchResults().apply {
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
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))
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
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))
        `when`(videoFileService.getVideoFile(1))
                .thenReturn(videoFile1)
        `when`(videoFileService.getVideoFile(5))
                .thenReturn(null)

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
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_EDIT))
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val videoFileWithId = videoFileNoId.copy(fileId = 1)
        `when`(videoFileService.addVideoFile(videoFileNoId))
                .thenReturn(videoFileWithId)

        val response = mockMvcHandler.doPost("/api/video-files", jacksonVideoFile.write(videoFileNoId).json)
        assertOkResponse(response, jacksonVideoFile.write(videoFileWithId).json)
    }

    @Test
    fun test_addVideoFile_unauthorized() {
        val response = mockMvcHandler.doPost("/api/video-files", jacksonVideoFile.write(videoFileNoId).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_addVideoFile_missingRole() {
        val user = AppUser(
                userName = "userName"
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doPost("/api/video-files", jacksonVideoFile.write(videoFileNoId).json)
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun testUpdateVideoFile() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_EDIT))
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val updatedVideoFile = videoFile2.copy(fileId = 1)
        `when`(videoFileService.updateVideoFile(1, videoFile2))
                .thenReturn(updatedVideoFile)
        `when`(videoFileService.updateVideoFile(5, videoFile3))
                .thenReturn(null)

        var response = mockMvcHandler.doPut("/api/video-files/1", jacksonVideoFile.write(videoFile2).json)
        assertOkResponse(response, jacksonVideoFile.write(updatedVideoFile).json)

        response = mockMvcHandler.doPut("/api/video-files/5", jacksonVideoFile.write(videoFile3).json)
        assertNoContentResponse(response)
    }

    @Test
    fun test_updateVideoFile_unauthorized() {
        val response = mockMvcHandler.doPut("/api/video-files/1", jacksonVideoFile.write(videoFile2).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_updateVideoFile_missingRole() {
        val user = AppUser(
                userName = "userName"
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doPut("/api/video-files/1", jacksonVideoFile.write(videoFile2).json)
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun testDeleteVideoFile() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_EDIT))
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        `when`(videoFileService.deleteVideoFile(1))
                .thenReturn(videoFile1)
                .thenReturn(null)

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
        val user = AppUser(
                userName = "userName"
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doDelete("/api/video-files/1")
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun test_StartVideoScan() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_SCAN))
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
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
        val user = AppUser(
                userName = "userName"
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
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
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))
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
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))
        `when`(videoFileService.searchForVideos(isA(VideoSearch::class.java)))
                .thenReturn(videoSearchResults)
                .thenReturn(VideoSearchResults())

        val search = VideoSearch("HelloWorld")

        var response = mockMvcHandler.doPost("/api/video-files/search", jacksonSearch.write(search).json)
        assertOkResponse(response, jacksonVideoSearchResults.write(videoSearchResults).json)

        response = mockMvcHandler.doPost("/api/video-files/search", jacksonSearch.write(search).json)
        assertNoContentResponse(response)
    }

    @Test
    fun test_searchForVideos_unauthorized() {
        val search = VideoSearch("HelloWorld")
        val response = mockMvcHandler.doPost("/api/video-files/search", jacksonSearch.write(search).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_playVideo() {
        val user = AppUser(userName = "userName")
        val params = mapOf(TokenConstants.PARAM_VIDEO_ID to "1")
        val token = videoTokenProvider.createToken(user, params)
        val file = File(".")
        `when`(videoFileService.playVideo(1L))
                .thenReturn(UrlResource(file.toURI()))
        val response = mockMvcHandler.doGet("/api/video-files/play/1?${TokenConstants.QUERY_PARAM_VIDEO_TOKEN}=$token")
        assertEquals(206, response.status)
        assertTrue(response.contentAsByteArray.isNotEmpty())
    }

    @Test
    fun test_playVideo_unauthorized() {
        val response = mockMvcHandler.doGet("/api/video-files/play/1")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_recordNewVideoPlay() {
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))
        val response = mockMvcHandler.doGet("/api/video-files/record-play/1")
        assertEquals(200, response.status)

        verify(videoFileService, times(1))
                .recordNewVideoPlay(1L)
    }

    @Test
    fun test_recordNewVideoPlay_unauthorized() {
        val response = mockMvcHandler.doGet("/api/video-files/record-play/1")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

}
