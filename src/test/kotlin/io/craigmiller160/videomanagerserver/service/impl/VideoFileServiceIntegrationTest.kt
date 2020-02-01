package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.dto.Category
import io.craigmiller160.videomanagerserver.dto.Series
import io.craigmiller160.videomanagerserver.dto.Star
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.dto.VideoSearch
import io.craigmiller160.videomanagerserver.repository.CategoryRepository
import io.craigmiller160.videomanagerserver.repository.FileCategoryRepository
import io.craigmiller160.videomanagerserver.repository.FileSeriesRepository
import io.craigmiller160.videomanagerserver.repository.FileStarRepository
import io.craigmiller160.videomanagerserver.repository.SeriesRepository
import io.craigmiller160.videomanagerserver.repository.StarRepository
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.service.VideoFileService
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.After
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
import javax.sql.DataSource
import javax.transaction.Transactional

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
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var videoFileRepo: VideoFileRepository
    @Autowired
    private lateinit var categoryRepo: CategoryRepository
    @Autowired
    private lateinit var seriesRepo: SeriesRepository
    @Autowired
    private lateinit var starRepo: StarRepository
    @Autowired
    private lateinit var fileCategoryRepo: FileCategoryRepository
    @Autowired
    private lateinit var fileSeriesRepo: FileSeriesRepository
    @Autowired
    private lateinit var fileStarRepo: FileStarRepository

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
        fileCategoryRepo.deleteAll()
        fileSeriesRepo.deleteAll()
        fileStarRepo.deleteAll()

        categoryRepo.deleteAll()
        starRepo.deleteAll()
        seriesRepo.deleteAll()
        videoFileRepo.deleteAll()

        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeUpdate("ALTER TABLE categories ALTER COLUMN category_id RESTART WITH 1")
                stmt.executeUpdate("ALTER TABLE series ALTER COLUMN series_id RESTART WITH 1")
                stmt.executeUpdate("ALTER TABLE stars ALTER COLUMN star_id RESTART WITH 1")
                stmt.executeUpdate("ALTER TABLE video_files ALTER COLUMN file_id RESTART WITH 1")
            }
            conn.commit()
        }
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
