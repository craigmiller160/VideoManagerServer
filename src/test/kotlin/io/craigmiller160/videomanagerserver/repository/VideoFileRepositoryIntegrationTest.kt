package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Category
import io.craigmiller160.videomanagerserver.dto.Series
import io.craigmiller160.videomanagerserver.dto.Star
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.util.getFirst
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.hasProperty
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
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
class VideoFileRepositoryIntegrationTest {

    companion object {
        private const val CATEGORY_NAME = "MyCategory"
        private const val SERIES_NAME = "MySeries"
        private const val STAR_NAME = "MyStar"
        private const val FILE_NAME = "MyFile"
        private const val FILE_DISPLAY_NAME = "MyDisplayFile"
        private const val FILE_NAME_2 = "MyFile2"
    }

    @Autowired
    private lateinit var videoFileRepo: VideoFileRepository

    @Autowired
    private lateinit var seriesRepo: SeriesRepository

    @Autowired
    private lateinit var categoryRepo: CategoryRepository

    @Autowired
    private lateinit var starRepo: StarRepository

    private lateinit var videoFile: VideoFile

    @Before
    fun setup() {
        val category = Category(categoryName = CATEGORY_NAME)
        val series = Series(seriesName = SERIES_NAME)
        val star = Star(starName = STAR_NAME)
        videoFile = VideoFile(fileName = FILE_NAME, displayName = FILE_DISPLAY_NAME).apply {
            categories += category
            this.series += series
            stars += star
        }

        categoryRepo.save(category)
        seriesRepo.save(series)
        starRepo.save(star)
        videoFile = videoFileRepo.save(videoFile)
    }

    @Test
    fun testInsertAll() {
        val fileId = videoFile.fileId

        val fileOptional = videoFileRepo.findById(fileId)
        assertTrue(fileOptional.isPresent)

        val file = fileOptional.get()
        assertNotNull(file)
        assertEquals(FILE_NAME, file.fileName)
        assertEquals(FILE_DISPLAY_NAME, file.displayName)

        assertEquals(1, file.categories.size)
        assertEquals(CATEGORY_NAME, getFirst(file.categories).categoryName)

        assertEquals(1, file.series.size)
        assertEquals(SERIES_NAME, getFirst(file.series).seriesName)

        assertEquals(1, file.stars.size)
        assertEquals(STAR_NAME, getFirst(file.stars).starName)
    }

    @Test
    fun testDeleteOnlyVideoFile() {
        val fileId = videoFile.fileId
        videoFileRepo.deleteById(fileId)

        val fileOptional = videoFileRepo.findById(fileId)
        assertFalse(fileOptional.isPresent)

        val categoryCount = categoryRepo.count()
        assertEquals(1, categoryCount)

        val seriesCount = seriesRepo.count()
        assertEquals(1, seriesCount)

        val starsCount = starRepo.count()
        assertEquals(1, starsCount)
    }

    @Test
    fun testMergeVideoFilesByName() {
        videoFileRepo.mergeVideoFilesByName(FILE_NAME)
        videoFileRepo.mergeVideoFilesByName(FILE_NAME_2)

        val videos = videoFileRepo.findAll()
        assertEquals(2, videos.toList().size)
        assertThat(videos.toList(), hasItems(
                hasProperty("fileName", `is`(FILE_NAME)),
                hasProperty("fileName", `is`(FILE_NAME_2))
        ))
    }

}