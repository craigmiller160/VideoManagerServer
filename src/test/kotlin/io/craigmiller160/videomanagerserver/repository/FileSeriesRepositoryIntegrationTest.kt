package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Series
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.test_util.DbTestUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import kotlin.test.assertEquals

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
class FileSeriesRepositoryIntegrationTest {

    companion object {
        private const val SERIES_NAME = "seriesName"
        private const val FILE_NAME = "fileName"
    }

    @Autowired
    private lateinit var seriesRepo: SeriesRepository
    @Autowired
    private lateinit var fileSeriesRepo: FileSeriesRepository
    @Autowired
    private lateinit var videoFileRepo: VideoFileRepository
    @Autowired
    private lateinit var dbTestUtils: DbTestUtils

    private var fileId = 0L
    private var seriesId = 0L

    @Before
    fun setup() {
        val series = Series(seriesName = SERIES_NAME)
        val file = VideoFile(fileName = FILE_NAME)
        file.series.add(series)
        videoFileRepo.save(file)

        fileId = file.fileId
        seriesId = series.seriesId
    }

    @After
    fun clean() {
        dbTestUtils.cleanDb()
    }

    private fun validateRecordsExist() {
        assertEquals(1, videoFileRepo.count())
        assertEquals(1, seriesRepo.count())
        assertEquals(1, fileSeriesRepo.count())
    }

    @Test
    fun test_deleteAllByStarId() {
        validateRecordsExist()
        fileSeriesRepo.deleteAllBySeriesId(seriesId)
        assertEquals(0, fileSeriesRepo.count())
    }

    @Test
    fun test_deleteAllByFileId() {
        validateRecordsExist()
        fileSeriesRepo.deleteAllByFileId(fileId)
        assertEquals(0, fileSeriesRepo.count())
    }

}