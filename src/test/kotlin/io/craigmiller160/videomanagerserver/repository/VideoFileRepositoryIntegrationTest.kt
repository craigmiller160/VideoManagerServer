package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Category
import io.craigmiller160.videomanagerserver.dto.Series
import io.craigmiller160.videomanagerserver.dto.Star
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.test_util.getFirst
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
@Transactional
class VideoFileRepositoryIntegrationTest {

    companion object {
        private const val CATEGORY_NAME = "MyCategory"
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

    private lateinit var videoFile: VideoFile
    private lateinit var videoFile2: VideoFile

    @Before
    fun setup() {
        val category = Category(categoryName = CATEGORY_NAME)
        val category2 = Category(categoryName = "${CATEGORY_NAME}2")
        val series = Series(seriesName = SERIES_NAME)
        val star = Star(starName = STAR_NAME)
        videoFile = VideoFile(fileName = FILE_NAME, displayName = FILE_DISPLAY_NAME).apply {
            categories.add(category)
            categories.add(category2)
            this.series.add(series)
            stars.add(star)
            lastModified = DATE_2
        }

        categoryRepo.save(category)
        categoryRepo.save(category2)
        seriesRepo.save(series)
        starRepo.save(star)
        videoFile = videoFileRepo.save(videoFile)
        videoFile2 = VideoFile(fileName = FILE_NAME_2)
        videoFile2 = videoFileRepo.save(videoFile2)
    }

//    @After
//    fun clean() {
//        categoryRepo.deleteAll()
//        seriesRepo.deleteAll()
//        starRepo.deleteAll()
//        videoFileRepo.deleteAll()
//    }

    @Test
    fun testInsertAll() {
        val fileId = videoFile.fileId

        val fileOptional = videoFileRepo.findById(fileId)
        assertTrue(fileOptional.isPresent)

        val file = fileOptional.get()
        assertNotNull(file)
        assertEquals(FILE_NAME, file.fileName)
        assertEquals(FILE_DISPLAY_NAME, file.displayName)

        assertEquals(2, file.categories.size)
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
        val id = videoFileRepo.save(VideoFile(fileName = FILE_NAME_3, lastScanTimestamp = timestamp)).fileId

        TODO("Finish this")
    }

    fun test_getActiveFiles() {
        TODO("Finish this")
    }

}