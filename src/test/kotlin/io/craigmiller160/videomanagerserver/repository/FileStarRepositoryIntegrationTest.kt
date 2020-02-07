package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.Star
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.test_util.DbTestUtils
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import kotlin.test.assertEquals

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
class FileStarRepositoryIntegrationTest {

    companion object {
        private const val STAR_NAME = "starName"
        private const val STAR_2_NAME = "star2Name"
        private const val FILE_NAME = "fileName"
        private const val FILE_2_NAME = "file2Name"
    }

    @Autowired
    private lateinit var starRepo: StarRepository
    @Autowired
    private lateinit var fileStarRepo: FileStarRepository
    @Autowired
    private lateinit var videoFileRepo: VideoFileRepository
    @Autowired
    private lateinit var dbTestUtils: DbTestUtils

    private var fileId = 0L
    private var starId = 0L

    @Before
    fun setup() {
        val star = Star(starName = STAR_NAME)
        val file = VideoFile(fileName = FILE_NAME)
        file.stars.add(star)
        videoFileRepo.save(file)

        fileId = file.fileId
        starId = star.starId

        val star2 = Star(starName = STAR_2_NAME)
        val file2 = VideoFile(fileName = FILE_2_NAME)
        file2.stars.add(star2)
        videoFileRepo.save(file2)
    }

    private fun validateRecordsExist() {
        assertEquals(2, videoFileRepo.count())
        assertEquals(2, starRepo.count())
        assertEquals(2, fileStarRepo.count())
    }

    @After
    fun clean() {
        dbTestUtils.cleanDb()
    }

    @Test
    fun test_deleteAllByStarId() {
        validateRecordsExist()
        fileStarRepo.deleteAllByStarId(starId)
        val results = fileStarRepo.findAll()
        assertThat(results, allOf(
                hasSize(1),
                contains(hasProperty("starId", not(equalTo(starId))))
        ))
    }

    @Test
    fun test_deleteAllByFileId() {
        validateRecordsExist()
        fileStarRepo.deleteAllByFileId(fileId)
        val results = fileStarRepo.findAll()
        assertThat(results, allOf(
                hasSize(1),
                contains(hasProperty("fileId", not(equalTo(fileId))))
        ))
    }

}