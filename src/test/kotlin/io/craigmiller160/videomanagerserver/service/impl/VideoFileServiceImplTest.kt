package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.argThat
import org.mockito.Mockito.isA
import org.mockito.Mockito.isNull
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.Optional

class VideoFileServiceImplTest {

    companion object {

        private const val FIRST_NAME = "ZFirstName"
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
    @Mock
    private lateinit var videoConfig: VideoConfiguration

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        videoFileService = VideoFileServiceImpl(videoFileRepo, videoConfig)
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

}