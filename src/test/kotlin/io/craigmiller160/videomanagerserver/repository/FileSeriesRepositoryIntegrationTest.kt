package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.Series
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.test_util.DbTestUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.not
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
        private const val SERIES_2_NAME = "series2Name"
        private const val FILE_NAME = "fileName"
        private const val FILE_2_NAME = "file2Name"
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

        val series2 = Series(seriesName = SERIES_2_NAME)
        val file2 = VideoFile(fileName = FILE_2_NAME)
        file2.series.add(series2)
        videoFileRepo.save(file2)
    }

    @After
    fun clean() {
        dbTestUtils.cleanDb()
    }

    private fun validateRecordsExist() {
        assertEquals(2, videoFileRepo.count())
        assertEquals(2, seriesRepo.count())
        assertEquals(2, fileSeriesRepo.count())
    }

    @Test
    fun test_deleteAllByStarId() {
        validateRecordsExist()
        fileSeriesRepo.deleteAllBySeriesId(seriesId)
        val results = fileSeriesRepo.findAll()
        MatcherAssert.assertThat(results, Matchers.allOf(
                hasSize(1),
                contains(hasProperty("seriesId", not(equalTo(seriesId))))
        ))
    }

    @Test
    fun test_deleteAllByFileId() {
        validateRecordsExist()
        fileSeriesRepo.deleteAllByFileId(fileId)
        val results = fileSeriesRepo.findAll()
        MatcherAssert.assertThat(results, Matchers.allOf(
                hasSize(1),
                contains(hasProperty("fileId", not(equalTo(fileId))))
        ))
    }

}