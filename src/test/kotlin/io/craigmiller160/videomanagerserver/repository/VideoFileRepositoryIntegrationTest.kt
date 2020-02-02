package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Category
import io.craigmiller160.videomanagerserver.dto.Series
import io.craigmiller160.videomanagerserver.dto.Star
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.test_util.DbTestUtils
import io.craigmiller160.videomanagerserver.test_util.getFirst
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
class VideoFileRepositoryIntegrationTest {

    companion object {
        private const val CATEGORY_NAME = "MyCategory"
        private const val CATEGORY_2_NAME = "MyCategory2"
        private const val SERIES_NAME = "MySeries"
        private const val STAR_NAME = "MyStar"
        private const val FILE_NAME = "MyFile"
        private const val FILE_DISPLAY_NAME = "MyDisplayFile"
        private const val FILE_NAME_2 = "MyFile2"
        private const val FILE_NAME_3 = "MyFile3"
        private val DATE = LocalDateTime.of(2018, 1, 1, 1, 1)
        private val DATE_2 = LocalDateTime.of(2018, 2, 2, 2, 2)
    }

    @Autowired
    private lateinit var videoFileRepo: VideoFileRepository

    @Autowired
    private lateinit var seriesRepo: SeriesRepository

    @Autowired
    private lateinit var categoryRepo: CategoryRepository

    @Autowired
    private lateinit var starRepo: StarRepository

    @Autowired
    private lateinit var dbTestUtils: DbTestUtils

    private lateinit var videoFile: VideoFile
    private lateinit var videoFile2: VideoFile

    @Before
    fun setup() {
        var category = Category(categoryName = CATEGORY_NAME)
        var category2 = Category(categoryName = CATEGORY_2_NAME)
        var series = Series(seriesName = SERIES_NAME)
        var star = Star(starName = STAR_NAME)

        category = categoryRepo.save(category)
        category2 = categoryRepo.save(category2)
        series = seriesRepo.save(series)
        star = starRepo.save(star)

        videoFile = VideoFile(fileName = FILE_NAME, displayName = FILE_DISPLAY_NAME, active = true).apply {
            lastModified = DATE_2
        }


        videoFile = videoFileRepo.save(videoFile)
        videoFile.apply {
            categories.add(category)
            categories.add(category2)
            this.series.add(series)
            stars.add(star)
        }
        videoFile = videoFileRepo.save(videoFile)
        videoFile2 = VideoFile(fileName = FILE_NAME_2, active = true)
        videoFile2 = videoFileRepo.save(videoFile2)
    }

    @After
    fun clean() {
        dbTestUtils.cleanDb()
    }

    @Test
    fun testInsertAll() {
        val fileId = videoFile.fileId

        val fileOptional = videoFileRepo.findById(fileId)
        assertTrue(fileOptional.isPresent)

        val file = fileOptional.get()
        assertThat(file, allOf(
                hasProperty("fileName", equalTo(FILE_NAME)),
                hasProperty("displayName", equalTo(FILE_DISPLAY_NAME)),
                hasProperty("categories", allOf<List<Category>>(
                        hasSize(2),
                        containsInAnyOrder(
                                hasProperty("categoryName", equalTo(CATEGORY_NAME)),
                                hasProperty("categoryName", equalTo(CATEGORY_2_NAME))
                        )
                )),
                hasProperty("series", allOf<List<Series>>(
                        hasSize(1),
                        containsInAnyOrder(
                                hasProperty("seriesName", equalTo(SERIES_NAME))
                        )
                )),
                hasProperty("stars", allOf<List<Star>>(
                        hasSize(1),
                        containsInAnyOrder(
                                hasProperty("starName", equalTo(STAR_NAME))
                        )
                ))
        ))
    }

    @Test
    fun testDeleteOnlyVideoFile() {
        val fileId = videoFile.fileId
        videoFileRepo.deleteById(fileId)

        val fileOptional = videoFileRepo.findById(fileId)
        assertFalse(fileOptional.isPresent)

        val categoryCount = categoryRepo.count()
        assertEquals(2, categoryCount)

        val seriesCount = seriesRepo.count()
        assertEquals(1, seriesCount)

        val starsCount = starRepo.count()
        assertEquals(1, starsCount)
    }

    @Test
    fun testDeleteOldFiles() {
        val timestamp = LocalDateTime.now()
        val id = videoFileRepo.save(VideoFile(fileName = FILE_NAME_3, lastScanTimestamp = timestamp)).fileId

        var count = videoFileRepo.count()
        assertEquals(3, count)

        videoFileRepo.deleteOldFiles(timestamp)
        count = videoFileRepo.count()
        assertEquals(1, count)

        val file = videoFileRepo.findById(id)
        assertTrue(file.isPresent)
        assertEquals(FILE_NAME_3, file.get().fileName)
    }

    @Test
    fun test_setOldFilesInactive() {
        val timestamp = LocalDateTime.now()
        videoFileRepo.save(VideoFile(fileName = FILE_NAME_3, lastScanTimestamp = timestamp, active = true)).fileId

        val result = videoFileRepo.setOldFilesInactive(timestamp)
        assertEquals(2, result)

        val fileMap = videoFileRepo.findAll()
                .groupBy { file -> file.active }

        assertEquals(1, fileMap[true]?.size)
        assertEquals(2, fileMap[false]?.size)
    }

}
