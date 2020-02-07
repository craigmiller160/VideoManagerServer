package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.entity.Category
import io.craigmiller160.videomanagerserver.entity.Series
import io.craigmiller160.videomanagerserver.dto.SortBy
import io.craigmiller160.videomanagerserver.dto.Star
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.dto.VideoSearch
import io.craigmiller160.videomanagerserver.service.VideoFileService
import io.craigmiller160.videomanagerserver.test_util.DbTestUtils
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class VideoFileServiceIntegrationTest {

    companion object {
        private const val FILE_NAME = "MyFile"
        private const val FILE_DISPLAY_NAME = "MyDisplayFile"

        private const val FILE_NAME_2 = "MyFile3"
        private const val FILE_DISPLAY_NAME_2 = "MyDisplayFile2"

        private const val FILE_NAME_3 = "MyFile2"

        private const val FILE_NAME_4 = "MyFile4"
    }

    @Autowired
    private lateinit var videoFileService: VideoFileService

    @Autowired
    private lateinit var dbTestUtils: DbTestUtils

    private lateinit var file1: VideoFile
    private lateinit var file2: VideoFile
    private lateinit var file3: VideoFile
    private lateinit var file4: VideoFile

    @Before
    fun setup() {
        val category = Category(categoryName = "MyCategory")
        val series = Series(seriesName = "MySeries")
        val star = Star(starName = "MyStar")

        file1 = VideoFile(fileName = FILE_NAME, displayName = FILE_DISPLAY_NAME).apply {
            categories.add(category)
            this.series.add(series)
            stars.add(star)
            active = true
        }
        file1 = videoFileService.addVideoFile(file1)

        file2 = VideoFile(fileName = FILE_NAME_2, displayName = FILE_DISPLAY_NAME_2, active = true)
        file3 = videoFileService.addVideoFile(file2)

        file3 = VideoFile(fileName = FILE_NAME_3, displayName = FILE_DISPLAY_NAME_2, active = true)
        file3 = videoFileService.addVideoFile(file3)

        file4 = VideoFile(fileName = FILE_NAME_4, active = true)
    }

    @After
    fun clean() {
        dbTestUtils.cleanDb()
    }

    @Test
    fun test_searchForVideos_sortAsc() {
        val search = VideoSearch(
                sortBy = SortBy.NAME,
                sortDir = Sort.Direction.ASC
        )
        val results = videoFileService.searchForVideos(search)
        assertThat(results, allOf(
                hasProperty("totalFiles", equalTo(3L)),
                hasProperty("filesPerPage", equalTo(10)),
                hasProperty("currentPage", equalTo(0)),
                hasProperty("videoList", contains(
                        file1, file3, file2
                ))
        ))
    }

    @Test
    fun test_searchForVideos_sortDesc() {
        val search = VideoSearch(
                sortBy = SortBy.NAME,
                sortDir = Sort.Direction.DESC
        )
        val results = videoFileService.searchForVideos(search)
        assertThat(results, allOf(
                hasProperty("totalFiles", equalTo(3L)),
                hasProperty("filesPerPage", equalTo(10)),
                hasProperty("currentPage", equalTo(0)),
                hasProperty("videoList", contains(
                        file2, file3, file1
                ))
        ))
    }

    @Test
    fun test_updateVideoFile_removeJoin() {
        val file = videoFileService.getVideoFile(1L).get()
        file.categories.clear()
        videoFileService.updateVideoFile(1L, file)

        val result = videoFileService.getVideoFile(1L).get()
        assertEquals(0, result.categories.size)
        assertEquals(1, result.series.size)
        assertEquals(1, result.stars.size)
    }

    @Test
    fun test_updateVideoFile_addJoin() {
        val file = videoFileService.getVideoFile(1L).get()
        file.categories.add(Category(categoryName = "NewCat"))
        videoFileService.updateVideoFile(1L, file)

        val result = videoFileService.getVideoFile(1L).get()
        assertEquals(2, result.categories.size)
        assertEquals(1, result.series.size)
        assertEquals(1, result.stars.size)
    }

    @Test
    fun test_searchForVideos_noCriteria() {
        val search = VideoSearch()
        val result = videoFileService.searchForVideos(search)
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
        val result = videoFileService.searchForVideos(search)
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
    fun test_searchForVideos_onlyText() {
        val search = VideoSearch("File")
        val result = videoFileService.searchForVideos(search)
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
    fun test_searchForVideos_onlyCategory() {
        val search = VideoSearch(categoryId = 1)
        val result = videoFileService.searchForVideos(search)
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
    fun test_searchForVideos_onlySeries() {
        val search = VideoSearch(seriesId = 1)
        val result = videoFileService.searchForVideos(search)
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
    fun test_searchForVideos_onlyStar() {
        val search = VideoSearch(starId = 1)
        val result = videoFileService.searchForVideos(search)
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
    fun test_searchForVideos_caseInsensitive() {
        val search = VideoSearch(searchText = "FILE")
        val result = videoFileService.searchForVideos(search)
        assertThat(result, allOf(
                hasProperty("totalFiles", equalTo(3L)),
                hasProperty("filesPerPage", equalTo(10)),
                hasProperty("currentPage", equalTo(0)),
                hasProperty("videoList", containsInAnyOrder(
                        file1, file2, file3
                ))
        ))
    }

}
