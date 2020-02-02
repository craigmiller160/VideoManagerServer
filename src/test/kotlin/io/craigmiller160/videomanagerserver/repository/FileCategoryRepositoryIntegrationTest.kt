package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Category
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.test_util.DbTestUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.assertEquals

@RunWith(SpringRunner::class)
@SpringBootTest
class FileCategoryRepositoryIntegrationTest {

    companion object {
        private const val CATEGORY_NAME = "categoryName";
        private const val FILE_NAME = "fileName"
    }

    @Autowired
    private lateinit var categoryRepository: CategoryRepository
    @Autowired
    private lateinit var fileCategoryRepository: FileCategoryRepository
    @Autowired
    private lateinit var videoFileRepository: VideoFileRepository
    @Autowired
    private lateinit var dbTestUtils: DbTestUtils

    private var fileId = 0L
    private var categoryId = 0L

    @Before
    fun setup() {
        val category = Category(categoryName = CATEGORY_NAME)
        val file = VideoFile(fileName = FILE_NAME)
        file.categories.add(category)
        videoFileRepository.save(file)

        fileId = file.fileId
        categoryId = category.categoryId
    }

    @After
    fun clean() {
        dbTestUtils.cleanDb()
    }

    private fun validateRecordsExist() {
        assertEquals(1, videoFileRepository.count())
        assertEquals(1, categoryRepository.count())
        assertEquals(1, fileCategoryRepository.count())
    }

    @Test
    fun test_deleteAllByCategoryId() {
        validateRecordsExist()
        fileCategoryRepository.deleteAllByCategoryId(categoryId)
        assertEquals(0, fileCategoryRepository.count())
    }

    @Test
    fun test_deleteAllByFileId() {
        validateRecordsExist()
        fileCategoryRepository.deleteAllByFileId(fileId)
        assertEquals(0, fileCategoryRepository.count())
    }

}