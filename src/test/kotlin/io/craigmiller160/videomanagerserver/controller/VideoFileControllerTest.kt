package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.craigmiller160.videomanagerserver.dto.*
import io.craigmiller160.videomanagerserver.service.VideoFileService
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

@SpringBootTest
class VideoFileControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @Mock
    private lateinit var videoFileService: VideoFileService

    private lateinit var videoFileController: VideoFileController
    private lateinit var videoManagerControllerAdvice: VideoManagerControllerAdvice

    private lateinit var jacksonVideoFileList: JacksonTester<List<VideoFile>>
    private lateinit var jacksonVideoFile: JacksonTester<VideoFile>
    private lateinit var jacksonStatus: JacksonTester<FileScanStatus>
    private lateinit var jacksonSearch: JacksonTester<VideoSearch>
    private lateinit var jacksonVideoFileCount: JacksonTester<VideoFileCount>

    private lateinit var videoFileNoId: VideoFile
    private lateinit var videoFile1: VideoFile
    private lateinit var videoFile2: VideoFile
    private lateinit var videoFile3: VideoFile
    private lateinit var videoFileList: List<VideoFile>
    private lateinit var scanRunning: FileScanStatus
    private lateinit var scanNotRunning: FileScanStatus
    private lateinit var scanAlreadyRunning: FileScanStatus

    @Before
    fun setup() {
        videoFileNoId = VideoFile(fileName = "NoId")
        videoFile1 = VideoFile(1, "FirstFile")
        videoFile2 = VideoFile(2, "SecondFile")
        videoFile3 = VideoFile(3, "ThirdFile")
        videoFileList = listOf(videoFile1, videoFile2, videoFile3)

        scanRunning = createScanRunningStatus()
        scanNotRunning = createScanNotRunningStatus()
        scanAlreadyRunning = createScanAlreadyRunningStatus()

        MockitoAnnotations.initMocks(this)
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        JacksonTester.initFields(this, objectMapper)

        videoFileController = VideoFileController(videoFileService)
        videoManagerControllerAdvice = VideoManagerControllerAdvice()
        mockMvc = MockMvcBuilders
                .standaloneSetup(videoFileController)
                .setControllerAdvice(videoManagerControllerAdvice)
                .build()
        mockMvcHandler = MockMvcHandler(mockMvc)
    }

    @Test
    fun testGetAllVideoFiles() {
        `when`(videoFileService.getAllVideoFiles(anyInt(), anyString()))
                .thenReturn(videoFileList)
                .thenReturn(listOf())

        var response = mockMvcHandler.doGet("/video-files")
        assertOkResponse(response, jacksonVideoFileList.write(videoFileList).json)

        response = mockMvcHandler.doGet("/video-files")
        assertNoContentResponse(response)

        response = mockMvcHandler.doGet("/video-files?page=0&sortDirection=FooBar")
        assertBadRequest(response)
    }

    @Test
    fun testGetVideoFile() {
        `when`(videoFileService.getVideoFile(1))
                .thenReturn(Optional.of(videoFile1))
        `when`(videoFileService.getVideoFile(5))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doGet("/video-files/1")
        assertOkResponse(response, jacksonVideoFile.write(videoFile1).json)

        response = mockMvcHandler.doGet("/video-files/5")
        assertNoContentResponse(response)
    }

    @Test
    fun testAddStar() {
        val videoFileWithId = videoFileNoId.copy(fileId = 1)
        `when`(videoFileService.addVideoFile(videoFileNoId))
                .thenReturn(videoFileWithId)

        val response = mockMvcHandler.doPost("/video-files", jacksonVideoFile.write(videoFileNoId).json)
        assertOkResponse(response, jacksonVideoFile.write(videoFileWithId).json)
    }

    @Test
    fun testUpdateVideoFile() {
        val updatedVideoFile = videoFile2.copy(fileId = 1)
        `when`(videoFileService.updateVideoFile(1, videoFile2))
                .thenReturn(Optional.of(updatedVideoFile))
        `when`(videoFileService.updateVideoFile(5, videoFile3))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doPut("/video-files/1", jacksonVideoFile.write(videoFile2).json)
        assertOkResponse(response, jacksonVideoFile.write(updatedVideoFile).json)

        response = mockMvcHandler.doPut("/video-files/5", jacksonVideoFile.write(videoFile3).json)
        assertNoContentResponse(response)
    }

    @Test
    fun testDeleteVideoFile() {
        `when`(videoFileService.deleteVideoFile(1))
                .thenReturn(Optional.of(videoFile1))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doDelete("/video-files/1")
        assertOkResponse(response, jacksonVideoFile.write(videoFile1).json)

        response = mockMvcHandler.doDelete("/video-files/5")
        assertNoContentResponse(response)
    }

    @Test
    fun testStartVideoScan() {
        `when`(videoFileService.startVideoFileScan())
                .thenReturn(scanRunning)
                .thenReturn(scanAlreadyRunning)

        var response = mockMvcHandler.doPost("/video-files/scanner")
        assertOkResponse(response, jacksonStatus.write(scanRunning).json)

        response = mockMvcHandler.doPost("/video-files/scanner")
        assertBadRequest(response, jacksonStatus.write(scanAlreadyRunning).json)
    }

    @Test
    fun testIsVideoScanRunning() {
        `when`(videoFileService.isVideoFileScanRunning())
                .thenReturn(scanNotRunning)
                .thenReturn(scanRunning)

        var response = mockMvcHandler.doGet("/video-files/scanner")
        assertOkResponse(response, jacksonStatus.write(scanNotRunning).json)

        response = mockMvcHandler.doGet("/video-files/scanner")
        assertOkResponse(response, jacksonStatus.write(scanRunning).json)
    }

    @Test
    fun testSearchForVideos() {
        `when`(videoFileService.searchForVideos(isA(VideoSearch::class.java), anyInt(), anyString()))
                .thenReturn(videoFileList)
                .thenReturn(listOf())

        val search = VideoSearch("HelloWorld")

        var response = mockMvcHandler.doPost("/video-files/search", jacksonSearch.write(search).json)
        assertOkResponse(response, jacksonVideoFileList.write(videoFileList).json)

        response = mockMvcHandler.doPost("/video-files/search", jacksonSearch.write(search).json)
        assertNoContentResponse(response)
    }

    @Test
    fun testGetVideoFileCount() {
        val expectedCount = VideoFileCount(5, 3)
        `when`(videoFileService.getVideoFileCount())
                .thenReturn(expectedCount)

        val response = mockMvcHandler.doGet("/video-files/count")
        assertOkResponse(response, jacksonVideoFileCount.write(expectedCount).json)
    }

    fun <T> isA(clazz: Class<T>): T = Mockito.isA(clazz)

}