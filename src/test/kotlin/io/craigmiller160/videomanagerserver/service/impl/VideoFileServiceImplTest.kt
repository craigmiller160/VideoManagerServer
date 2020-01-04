package io.craigmiller160.videomanagerserver.service.impl

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.SCAN_STATUS_ALREADY_RUNNING
import io.craigmiller160.videomanagerserver.dto.SCAN_STATUS_ERROR
import io.craigmiller160.videomanagerserver.dto.SCAN_STATUS_NOT_RUNNING
import io.craigmiller160.videomanagerserver.dto.SCAN_STATUS_RUNNING
import io.craigmiller160.videomanagerserver.dto.SETTINGS_ID
import io.craigmiller160.videomanagerserver.dto.Settings
import io.craigmiller160.videomanagerserver.dto.SortBy
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.dto.VideoSearch
import io.craigmiller160.videomanagerserver.exception.InvalidSettingException
import io.craigmiller160.videomanagerserver.file.FileScanner
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import io.craigmiller160.videomanagerserver.test_util.getField
import io.craigmiller160.videomanagerserver.test_util.isA
import io.craigmiller160.videomanagerserver.util.DEFAULT_TIMESTAMP
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.hasProperty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.lang.Exception
import java.util.Optional
import java.util.concurrent.atomic.AtomicBoolean
import javax.persistence.EntityManager
import javax.persistence.Query

class VideoFileServiceImplTest {

    companion object {

        private const val FIRST_NAME = "FirstName"
        private const val SECOND_NAME = "SecondName"
        private const val THIRD_NAME = "ThirdName"
        private const val ROOT_DIR = "rootDir"

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
    @Mock
    private lateinit var entityManager: EntityManager
    @Mock
    private lateinit var settingsService: SettingsService

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        videoFileService = VideoFileServiceImpl(videoFileRepo, videoConfig, fileScanner, entityManager, settingsService)

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
    fun test_startVideoFileScan_scanError() {
        val fileScanRunning = getField(videoFileService, "fileScanRunning", AtomicBoolean::class.java)
        val lastScanSuccess = getField(videoFileService, "lastScanSuccess", AtomicBoolean::class.java)

        `when`(fileScanner.scanForFiles(any()))
                .thenThrow(InvalidSettingException())

        var exception: Exception? = null

        try {
            videoFileService.startVideoFileScan()
        }
        catch (ex: Exception) {
            exception = ex
        }

        assertNotNull(exception)
        assertFalse(fileScanRunning.get())
        assertFalse(lastScanSuccess.get())
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
    fun test_playVideo() {
        val settings = Settings(
                settingsId = SETTINGS_ID,
                rootDir = ROOT_DIR
        )

        `when`(videoFileRepo.findById(1))
                .thenReturn(Optional.of(expectedFiles[0]))
        `when`(settingsService.getOrCreateSettings())
                .thenReturn(settings)

        val video = videoFileService.playVideo(expectedFiles[0].fileId)

        assertThat(video.file.absolutePath, containsString("$ROOT_DIR/${expectedFiles[0].fileName}"))
    }

    @Test(expected = InvalidSettingException::class)
    fun test_playVideo_noRootDir() {
        `when`(videoFileRepo.findById(1))
                .thenReturn(Optional.of(expectedFiles[0]))
        `when`(settingsService.getOrCreateSettings())
                .thenReturn(Settings())

        videoFileService.playVideo(expectedFiles[0].fileId)
    }

    @Test
    fun test_recordNewVideoPlay() {
        `when`(videoFileRepo.findById(1L))
                .thenReturn(Optional.of(expectedFiles[0].copy()))

        videoFileService.recordNewVideoPlay(1L)

        val argumentCaptor = argumentCaptor<VideoFile>().apply {
            verify(videoFileRepo, times(1)).save(capture())
        }

        assertEquals(1, argumentCaptor.allValues.size)
        assertThat(argumentCaptor.firstValue, allOf(
                hasProperty("viewCount", equalTo(1)),
                hasProperty("lastViewed", greaterThan(DEFAULT_TIMESTAMP))
        ))
    }

    @Test
    fun test_buildQueryCriteria_noCriteria() {
        val search = VideoSearch()
        val query = videoFileService.buildQueryCriteria(search, true)
        assertEquals("ORDER BY vf.displayName ASC\n", query)
    }

    @Test
    fun test_buildQueryCriteria_descOrder() {
        val search = VideoSearch(sortDir = Sort.Direction.DESC)
        val query = videoFileService.buildQueryCriteria(search, true)
        assertEquals("ORDER BY vf.displayName DESC\n", query)
    }

    @Test
    fun test_buildQueryCriteria_sortByViewCount() {
        val search = VideoSearch(sortBy = SortBy.VIEW_COUNT)
        val query = videoFileService.buildQueryCriteria(search, true)
        assertEquals("ORDER BY vf.viewCount ASC\n", query)
    }

    @Test
    fun test_buildQueryCriteria_sortByLastViewed() {
        val search = VideoSearch(sortBy = SortBy.LAST_VIEWED)
        val query = videoFileService.buildQueryCriteria(search, true)
        assertEquals("ORDER BY vf.lastViewed ASC\n", query)
    }

    @Test
    fun test_buildQueryCriteria_sortByLastModified() {
        val search = VideoSearch(sortBy = SortBy.LAST_MODIFIED)
        val query = videoFileService.buildQueryCriteria(search, true)
        assertEquals("ORDER BY vf.lastModified ASC\n", query)
    }

    @Test
    fun test_buildQueryCriteria_sortByFileAdded() {
        val search = VideoSearch(sortBy = SortBy.FILE_ADDED)
        val query = videoFileService.buildQueryCriteria(search, true)
        assertEquals("ORDER BY vf.fileAdded ASC\n", query)
    }

    @Test
    fun test_buildQueryCriteria_allCriteria() {
        val expected = "LEFT JOIN vf.categories ca\n" +
                "LEFT JOIN vf.series se\n" +
                "LEFT JOIN vf.stars st\n" +
                "WHERE (LOWER(vf.fileName) LIKE LOWER(:searchText)\n" +
                "OR LOWER(vf.displayName) LIKE LOWER(:searchText))\n" +
                "AND ca.categoryId = :categoryId\n" +
                "AND se.seriesId = :seriesId\n" +
                "AND st.starId = :starId\n" +
                "ORDER BY vf.displayName ASC\n"

        val search = VideoSearch("Hello", 1, 1, 1)
        val query = videoFileService.buildQueryCriteria(search, true)
        assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_onlySearchText() {
        val expected = "WHERE (LOWER(vf.fileName) LIKE LOWER(:searchText)\n" +
                "OR LOWER(vf.displayName) LIKE LOWER(:searchText))\n" +
                "ORDER BY vf.displayName ASC\n"
        val search = VideoSearch("Hello")
        val query = videoFileService.buildQueryCriteria(search, true)
        assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_onlyCategory() {
        val expected = "LEFT JOIN vf.categories ca\n" +
                "WHERE ca.categoryId = :categoryId\n" +
                "ORDER BY vf.displayName ASC\n"
        val search = VideoSearch(categoryId = 1)
        val query = videoFileService.buildQueryCriteria(search, true)
        assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_onlyStar() {
        val expected = "LEFT JOIN vf.stars st\n" +
                "WHERE st.starId = :starId\n" +
                "ORDER BY vf.displayName ASC\n"
        val search = VideoSearch(starId = 1)
        val query = videoFileService.buildQueryCriteria(search, true)
        assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_onlySeries() {
        val expected = "LEFT JOIN vf.series se\n" +
                "WHERE se.seriesId = :seriesId\n" +
                "ORDER BY vf.displayName ASC\n"
        val search = VideoSearch(seriesId = 1)
        val query = videoFileService.buildQueryCriteria(search, true)
        assertEquals(expected, query)
    }

    @Test
    fun test_addParamsToQuery_noParams() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearch()
        videoFileService.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, times(0))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        assertEquals(0, argumentCaptor.allValues.size)
    }

    @Test
    fun test_addParamsToQuery_allParams() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearch("Hello", 1, 1, 1)
        videoFileService.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, times(4))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        assertEquals(4, argumentCaptor.allValues.size)
        assertThat(argumentCaptor.allValues, contains<Any>("%Hello%", 1L, 1L, 1L))
    }

    @Test
    fun test_addParamsToQuery_onlyText() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearch("Hello")
        videoFileService.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, times(1))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        assertEquals(1, argumentCaptor.allValues.size)
        assertThat(argumentCaptor.allValues, contains<Any>("%Hello%"))
    }

    @Test
    fun test_addParamsToQuery_onlyCategory() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearch(categoryId = 1)
        videoFileService.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, times(1))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        assertEquals(1, argumentCaptor.allValues.size)
        assertThat(argumentCaptor.allValues, contains<Any>(1L))
    }

    @Test
    fun test_addParamsToQuery_onlySeries() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearch(seriesId = 1)
        videoFileService.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, times(1))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        assertEquals(1, argumentCaptor.allValues.size)
        assertThat(argumentCaptor.allValues, contains<Any>(1L))
    }

    @Test
    fun test_addParamsToQuery_onlyStar() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearch(starId = 1)
        videoFileService.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, times(1))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        assertEquals(1, argumentCaptor.allValues.size)
        assertThat(argumentCaptor.allValues, contains<Any>(1L))
    }

}