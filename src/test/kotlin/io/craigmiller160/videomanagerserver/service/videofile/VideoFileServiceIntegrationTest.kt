package io.craigmiller160.videomanagerserver.service.videofile

import io.craigmiller160.videomanagerserver.dto.CategoryPayload
import io.craigmiller160.videomanagerserver.dto.SeriesPayload
import io.craigmiller160.videomanagerserver.entity.sort.VideoFileSortBy
import io.craigmiller160.videomanagerserver.dto.StarPayload
import io.craigmiller160.videomanagerserver.dto.VideoFilePayload
import io.craigmiller160.videomanagerserver.dto.VideoSearch
import io.craigmiller160.videomanagerserver.test_util.DbTestUtils
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.After
import org.junit.Assert
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
    private lateinit var dbTestUtils: DbTestUtils
    @Autowired
    private lateinit var videoFileService: VideoFileService

    private lateinit var file1: VideoFilePayload
    private lateinit var file2: VideoFilePayload
    private lateinit var file3: VideoFilePayload
    private lateinit var file4: VideoFilePayload

    @Before
    fun setup() {
        val category = CategoryPayload(categoryName = "MyCategory")
        val series = SeriesPayload(seriesName = "MySeries")
        val star = StarPayload(starName = "MyStar")

        file1 = VideoFilePayload(fileName = FILE_NAME, displayName = FILE_DISPLAY_NAME).apply {
            categories.add(category)
            this.series.add(series)
            stars.add(star)
        }
        file1 = videoFileService.addVideoFile(file1)

        file2 = VideoFilePayload(fileName = FILE_NAME_2, displayName = FILE_DISPLAY_NAME_2)
        file2 = videoFileService.addVideoFile(file2)

        file3 = VideoFilePayload(fileName = FILE_NAME_3, displayName = FILE_DISPLAY_NAME_2)
        file3 = videoFileService.addVideoFile(file3)

        file4 = VideoFilePayload(fileName = FILE_NAME_4)
    }

    @After
    fun clean() {
        dbTestUtils.cleanDb()
    }

    @Test
    fun test_searchForVideos_sortAsc() {
        val search = VideoSearch(
                sortBy = VideoFileSortBy.NAME,
                sortDir = Sort.Direction.ASC
        )
        val results = videoFileService.searchForVideos(search)
        Assert.assertThat(results, Matchers.allOf(
                Matchers.hasProperty("totalFiles", Matchers.equalTo(3L)),
                Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
                Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
                Matchers.hasProperty("videoList", Matchers.contains(
                        file1, file3, file2
                ))
        ))
    }

    @Test
    fun test_searchForVideos_sortDesc() {
        val search = VideoSearch(
                sortBy = VideoFileSortBy.NAME,
                sortDir = Sort.Direction.DESC
        )
        val results = videoFileService.searchForVideos(search)
        Assert.assertThat(results, Matchers.allOf(
                Matchers.hasProperty("totalFiles", Matchers.equalTo(3L)),
                Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
                Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
                Matchers.hasProperty("videoList", Matchers.contains(
                        file2, file3, file1
                ))
        ))
    }

    @Test
    fun test_updateVideoFile_removeJoin() {
        val file = videoFileService.getVideoFile(1L)
        file?.categories?.clear()
        videoFileService.updateVideoFile(1L, file!!)

        val result = videoFileService.getVideoFile(1L)
        Assert.assertEquals(0, result?.categories?.size)
        Assert.assertEquals(1, result?.series?.size)
        Assert.assertEquals(1, result?.stars?.size)
    }

    @Test
    fun test_updateVideoFile_addJoin() {
        val file = videoFileService.getVideoFile(1L)
        file?.categories?.add(CategoryPayload(categoryName = "NewCat"))
        videoFileService.updateVideoFile(1L, file!!)

        val result = videoFileService.getVideoFile(1L)
        Assert.assertEquals(2, result?.categories?.size)
        Assert.assertEquals(1, result?.series?.size)
        Assert.assertEquals(1, result?.stars?.size)
    }

    @Test
    fun test_searchForVideos_noCriteria() {
        val search = VideoSearch()
        val result = videoFileService.searchForVideos(search)
        Assert.assertThat(result, Matchers.allOf(
                Matchers.hasProperty("totalFiles", Matchers.equalTo(3L)),
                Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
                Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
                Matchers.hasProperty("videoList", containsInAnyOrder(
                        file1, file2, file3
                ))
        ))
    }

    @Test
    fun test_searchForVideos_allCriteria() {
        val search = VideoSearch("File", 1, 1, 1)
        val result = videoFileService.searchForVideos(search)
        Assert.assertThat(result, Matchers.allOf(
                Matchers.hasProperty("totalFiles", Matchers.equalTo(1L)),
                Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
                Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
                Matchers.hasProperty("videoList", containsInAnyOrder(
                        file1
                ))
        ))
    }

    @Test
    fun test_searchForVideos_onlyText() {
        val search = VideoSearch("File")
        val result = videoFileService.searchForVideos(search)
        Assert.assertThat(result, Matchers.allOf(
                Matchers.hasProperty("totalFiles", Matchers.equalTo(3L)),
                Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
                Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
                Matchers.hasProperty("videoList", Matchers.containsInAnyOrder(
                        file1, file2, file3
                ))
        ))
    }

    @Test
    fun test_searchForVideos_onlyCategory() {
        val search = VideoSearch(categoryId = 1)
        val result = videoFileService.searchForVideos(search)
        Assert.assertThat(result, Matchers.allOf(
                Matchers.hasProperty("totalFiles", Matchers.equalTo(1L)),
                Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
                Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
                Matchers.hasProperty("videoList", containsInAnyOrder(
                        file1
                ))
        ))
    }

    @Test
    fun test_searchForVideos_onlySeries() {
        val search = VideoSearch(seriesId = 1)
        val result = videoFileService.searchForVideos(search)
        Assert.assertThat(result, Matchers.allOf(
                Matchers.hasProperty("totalFiles", Matchers.equalTo(1L)),
                Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
                Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
                Matchers.hasProperty("videoList", containsInAnyOrder(
                        file1
                ))
        ))
    }

    @Test
    fun test_searchForVideos_onlyStar() {
        val search = VideoSearch(starId = 1)
        val result = videoFileService.searchForVideos(search)
        Assert.assertThat(result, Matchers.allOf(
                Matchers.hasProperty("totalFiles", Matchers.equalTo(1L)),
                Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
                Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
                Matchers.hasProperty("videoList", containsInAnyOrder(
                        file1
                ))
        ))
    }

    @Test
    fun test_searchForVideos_caseInsensitive() {
        val search = VideoSearch(searchText = "FILE")
        val result = videoFileService.searchForVideos(search)
        Assert.assertThat(result, Matchers.allOf(
                Matchers.hasProperty("totalFiles", Matchers.equalTo(3L)),
                Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
                Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
                Matchers.hasProperty("videoList", Matchers.containsInAnyOrder(
                        file1, file2, file3
                ))
        ))
    }

}
