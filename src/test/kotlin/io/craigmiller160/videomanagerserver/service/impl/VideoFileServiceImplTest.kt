package io.craigmiller160.videomanagerserver.service.impl

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.*
import io.craigmiller160.videomanagerserver.file.FileScanner
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.test_util.getField
import io.craigmiller160.videomanagerserver.test_util.isA
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class VideoFileServiceImplTest {

    companion object {

        private const val FIRST_NAME = "FirstName"
        private const val SECOND_NAME = "SecondName"
        private const val THIRD_NAME = "ThirdName"

        private val expectedFiles = listOf(
                VideoFile(fileId = 1, fileName = FIRST_NAME),
                VideoFile(fileId = 2, fileName = SECOND_NAME)
        )

        private val expectedFilesPage = PageImpl(expectedFiles)

    }

    private lateinit var videoFileService: VideoFileServiceImpl

    @Mock
    private lateinit var videoFileRepo: VideoFileRepository
    @Spy
    private lateinit var videoConfig: VideoConfiguration
    @Mock
    private lateinit var fileScanner: FileScanner

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        videoConfig.filePathRoot = "/home/craig/Videos"
        videoFileService = VideoFileServiceImpl(videoFileRepo, videoConfig, fileScanner)

        val fileScanRunning = getField(videoFileService, "fileScanRunning", AtomicBoolean::class.java)
        fileScanRunning.set(false)
        val lastScanSuccess = getField(videoFileService, "lastScanSuccess", AtomicBoolean::class.java)
        lastScanSuccess.set(true)
    }

    @Test
    fun testGetAllVideoFiles() {
        `when`(videoFileRepo.findAll(isA(Pageable::class.java)))
                .thenReturn(expectedFilesPage)

        `when`(videoConfig.apiPageSize)
                .thenReturn(20)

        val actualFiles = videoFileService.getAllVideoFiles(1, Sort.Direction.DESC.toString())
        assertNotNull(actualFiles)
        assertEquals(expectedFiles.size, actualFiles.size)
        assertEquals(expectedFiles, actualFiles)
    }

    @Test
    fun testGetVideoFile() {
        `when`(videoFileRepo.findById(1))
                .thenReturn(Optional.of(expectedFiles[0]))
        `when`(videoFileRepo.findById(2))
                .thenReturn(Optional.of(expectedFiles[1]))

        var actualFile = videoFileService.getVideoFile(1)
        assertTrue(actualFile.isPresent)
        assertEquals(expectedFiles[0], actualFile.get())

        actualFile = videoFileService.getVideoFile(2)
        assertTrue(actualFile.isPresent)
        assertEquals(expectedFiles[1], actualFile.get())

        actualFile = videoFileService.getVideoFile(3)
        assertFalse(actualFile.isPresent)
    }

    @Test
    fun testAddVideoFile() {
        val newCategory = VideoFile(fileName = THIRD_NAME)
        val newCategoryWithId = VideoFile(fileId = 3, fileName = THIRD_NAME)

        `when`(videoFileRepo.save(newCategory))
                .thenReturn(newCategoryWithId)

        val actualFile = videoFileService.addVideoFile(newCategory)
        assertEquals(newCategoryWithId, actualFile)
    }

    @Test
    fun testUpdateVideoFile() {
        val newCategory = VideoFile(fileName = THIRD_NAME)
        val newCategoryWithId = VideoFile(fileId = 1, fileName = THIRD_NAME)

        `when`(videoFileRepo.save(newCategoryWithId))
                .thenReturn(newCategoryWithId)
        `when`(videoFileRepo.findById(1))
                .thenReturn(Optional.of(expectedFiles[0]))

        var actualFile = videoFileService.updateVideoFile(1, newCategory)
        assertTrue(actualFile.isPresent)
        assertEquals(newCategoryWithId, actualFile.get())

        actualFile = videoFileService.updateVideoFile(3, newCategory)
        assertFalse(actualFile.isPresent)
    }

    @Test
    fun testDeleteVideoFile() {
        `when`(videoFileRepo.findById(1))
                .thenReturn(Optional.of(expectedFiles[0]))
                .thenReturn(Optional.empty())

        var actualFile = videoFileService.deleteVideoFile(1)
        assertTrue(actualFile.isPresent)
        assertEquals(expectedFiles[0], actualFile.get())

        actualFile = videoFileService.deleteVideoFile(1)
        assertFalse(actualFile.isPresent)
    }

    @Test
    fun testStartVideoFileScan() {
        val fileScanRunning = getField(videoFileService, "fileScanRunning", AtomicBoolean::class.java)
        val lastScanSuccess = getField(videoFileService, "lastScanSuccess", AtomicBoolean::class.java)
        lastScanSuccess.set(false)

        var status = videoFileService.startVideoFileScan()
        assertThat(status, allOf(
                hasProperty("inProgress", equalTo(true)),
                hasProperty("alreadyRunning", equalTo(false)),
                hasProperty("message", equalTo(SCAN_STATUS_RUNNING)),
                hasProperty("scanError", equalTo(false))
        ))

        fileScanRunning.set(true)
        status = videoFileService.startVideoFileScan()
        assertThat(status, allOf(
                hasProperty("inProgress", equalTo(true)),
                hasProperty("alreadyRunning", equalTo(true)),
                hasProperty("message", equalTo(SCAN_STATUS_ALREADY_RUNNING)),
                hasProperty("scanError", equalTo(false))
        ))

        verify(fileScanner, times(1)).scanForFiles(any())
    }

    @Test
    fun testIsVideoFileScanRunning() {
        val fileScanRunning = getField(videoFileService, "fileScanRunning", AtomicBoolean::class.java)
        val lastScanSuccess = getField(videoFileService, "lastScanSuccess", AtomicBoolean::class.java)
        var status = videoFileService.isVideoFileScanRunning()
        assertThat(status, allOf(
                hasProperty("inProgress", equalTo(false)),
                hasProperty("alreadyRunning", equalTo(false)),
                hasProperty("message", equalTo(SCAN_STATUS_NOT_RUNNING)),
                hasProperty("scanError", equalTo(false))
        ))

        fileScanRunning.set(true)

        status = videoFileService.isVideoFileScanRunning()
        assertThat(status, allOf(
                hasProperty("inProgress", equalTo(true)),
                hasProperty("alreadyRunning", equalTo(false)),
                hasProperty("message", equalTo(SCAN_STATUS_RUNNING)),
                hasProperty("scanError", equalTo(false))
        ))

        fileScanRunning.set(false)
        lastScanSuccess.set(false)

        status = videoFileService.isVideoFileScanRunning()
        assertThat(status, allOf(
                hasProperty("inProgress", equalTo(false)),
                hasProperty("alreadyRunning", equalTo(false)),
                hasProperty("message", equalTo(SCAN_STATUS_ERROR)),
                hasProperty("scanError", equalTo(true))
        ))
    }

    @Test
    fun testPlayVideo() {
        `when`(videoFileRepo.findById(1))
                .thenReturn(Optional.of(expectedFiles[0]))

        val video = videoFileService.playVideo(expectedFiles[0])

        val argumentCaptor = argumentCaptor<VideoFile>().apply {
            verify(videoFileRepo, times(1)).save(capture())
        }

        val allValues = argumentCaptor.allValues
        assertEquals(1, allValues.size)
        assertEquals(expectedFiles[0], allValues[0])

        assertEquals("${videoConfig.filePathRoot}/${expectedFiles[0].fileName}", video.file.absolutePath)
    }

    @Test
    fun testSearchForVideos() {
        val pageSize = 10
        val searchText = "Hello"
        val seriesId = 1L
        val categoryId = 1L
        val starId = 1L
        val page = 0
        val totalValues = 100L
        `when`(videoConfig.apiPageSize)
                .thenReturn(pageSize)
        `when`(videoFileRepo.searchByValues(eq("%$searchText%"), eq(seriesId), eq(starId), eq(categoryId), isA(Pageable::class.java)))
                .thenReturn(expectedFiles)
        `when`(videoFileRepo.countByValues(eq("%$searchText%"), eq(seriesId), eq(starId), eq(categoryId)))
                .thenReturn(totalValues)

        val expectedResults = VideoSearchResults().apply {
            totalFiles = totalValues
            filesPerPage = pageSize
            currentPage = page
            videoList = expectedFiles
        }

        val videoSearch = VideoSearch(searchText, seriesId, starId, categoryId)

        val result = videoFileService.searchForVideos(videoSearch, page, Sort.Direction.ASC.toString())
        assertEquals(expectedResults, result)
    }

}