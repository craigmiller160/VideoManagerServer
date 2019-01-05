package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.service.VideoFileService
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.springframework.boot.test.json.JacksonTester
import org.springframework.test.web.servlet.MockMvc

class VideoFileControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @Mock
    private lateinit var videoFileService: VideoFileService

    private lateinit var videoFileController: VideoFileController

    private lateinit var jacksonVideoFileList: JacksonTester<List<VideoFile>>
    private lateinit var jacksonVideoFile: JacksonTester<VideoFile>

    private lateinit var videoFileNoId: VideoFile
    private lateinit var videoFile1: VideoFile
    private lateinit var videoFile2: VideoFile
    private lateinit var videoFile3: VideoFile
    private lateinit var videoFileList: List<VideoFile>

    @Before
    fun setup() {
        TODO("Finish this")
    }

    @Test
    fun testGetAllVideoFiles() {
        TODO("Finish this")
    }

    @Test
    fun testGetVideoFile() {
        TODO("Finish this")
    }

    @Test
    fun testAddStar() {
        TODO("Finish this")
    }

    @Test
    fun testUpdateVideoFile() {
        TODO("Finish this")
    }

    @Test
    fun testDeleteVideoFile() {
        TODO("Finish this")
    }

}