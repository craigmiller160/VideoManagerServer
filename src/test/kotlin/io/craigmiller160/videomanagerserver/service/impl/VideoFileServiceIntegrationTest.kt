package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.dto.Category
import io.craigmiller160.videomanagerserver.dto.Series
import io.craigmiller160.videomanagerserver.dto.Star
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.dto.VideoSearch
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.service.VideoFileService
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort
import org.springframework.test.context.junit4.SpringRunner
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
class VideoFileServiceIntegrationTest {

    companion object {
        private const val FILE_NAME = "MyFile"
        private const val FILE_DISPLAY_NAME = "MyDisplayFile"

        private const val FILE_NAME_2 = "MyFile3"
        private const val FILE_DISPLAY_NAME_2 = "MyDisplayFile2"

        private const val FILE_NAME_3 = "MyFile2"
    }

    @Autowired
    private lateinit var videoFileService: VideoFileService

    private lateinit var file1: VideoFile
    private lateinit var file2: VideoFile
    private lateinit var file3: VideoFile

    @Before
    fun setup() {
        val category = Category(categoryName = "MyCategory")
        val series = Series(seriesName = "MySeries")
        val star = Star(starName = "MyStar")

        file1 = VideoFile(fileName = FILE_NAME, displayName = FILE_DISPLAY_NAME).apply {
            categories += category
            this.series += series
            stars += star
        }
        file1 = videoFileService.addVideoFile(file1)

        file2 = VideoFile(fileName = FILE_NAME_2, displayName = FILE_DISPLAY_NAME_2)
        file3 = videoFileService.addVideoFile(file2)

        file3 = VideoFile(fileName = FILE_NAME_3, displayName = FILE_DISPLAY_NAME_2)
        file3 = videoFileService.addVideoFile(file3)
    }

    @Test
    fun testSortOrderAsc() {
        val files = videoFileService.getAllVideoFiles(0, Sort.Direction.ASC.toString())
        assertNotNull(files)
        assertEquals(3, files.size)
        assertEquals(file1, files[0])
        assertEquals(file3, files[1])
        assertEquals(file2, files[2])
    }

    @Test
    fun testSortOrderDesc() {
        val files = videoFileService.getAllVideoFiles(0, Sort.Direction.DESC.toString())
        assertNotNull(files)
        assertEquals(3, files.size)
        assertEquals(file2, files[0])
        assertEquals(file3, files[1])
        assertEquals(file1, files[2])
    }

    @Test
    fun test_searchForVideos_noCriteria() {
        val search = VideoSearch()
        val result = videoFileService.searchForVideos(search, 0, "ASC")
        assertThat(result, allOf(
                hasProperty("totalFiles", equalTo(3L)),
                hasProperty("filesPerPage", equalTo(10)),
                hasProperty("currentPage", equalTo(0)),
                hasProperty("videoList", containsInAnyOrder(
                        file1, file2, file3
                ))
        ))
    }

    @Test
    fun test_searchForVideos_allCriteria() {
        val search = VideoSearch("File", 1, 1, 1)
        val result = videoFileService.searchForVideos(search, 0, "ASC")
        assertThat(result, allOf(
                hasProperty("totalFiles", equalTo(1L)),
                hasProperty("filesPerPage", equalTo(10)),
                hasProperty("currentPage", equalTo(0)),
                hasProperty("videoList", containsInAnyOrder(
                        file1
                ))
        ))
    }

    @Test
    fun test_searchForVideos_onlyTest() {
        TODO("Finish this")
    }

    @Test
    fun test_searchForVideos_onlyCategory() {
        TODO("Finish this")
    }

    @Test
    fun test_searchForVideos_onlySeries() {
        TODO("Finish this")
    }

    @Test
    fun test_searchForVideos_onlyStar() {
        TODO("Finish this")
    }

}