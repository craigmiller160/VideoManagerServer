package io.craigmiller160.videomanagerserver.service.videofile

import io.craigmiller160.videomanagerserver.config.MapperConfig
import io.craigmiller160.videomanagerserver.dto.CategoryPayload
import io.craigmiller160.videomanagerserver.entity.Category
import io.craigmiller160.videomanagerserver.repository.CategoryRepository
import io.craigmiller160.videomanagerserver.repository.FileCategoryRepository
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.data.domain.Sort
import java.util.Optional
import kotlin.test.assertNull

@RunWith(MockitoJUnitRunner::class)
class CategoryServiceTest {

    companion object {

        private const val FIRST_NAME = "FirstName"
        private const val SECOND_NAME = "SecondName"
        private const val THIRD_NAME = "ThirdName"

        private val expectedCategories = listOf(
                Category(categoryId = 1, categoryName = FIRST_NAME),
                Category(categoryId = 2, categoryName = SECOND_NAME)
        )

        private val expectedCategoryPayloads = listOf(
                CategoryPayload(categoryId = 1, categoryName = FIRST_NAME),
                CategoryPayload(categoryId = 2, categoryName = SECOND_NAME)
        )

    }

    @InjectMocks
    private lateinit var categoryService: CategoryService

    @Mock
    private lateinit var categoryRepo: CategoryRepository
    @Mock
    private lateinit var fileCategoryRepo: FileCategoryRepository
    @Spy
    private var modelMapper = MapperConfig().modelMapper()

    @Test
    fun testGetAllCategories() {
        Mockito.`when`(categoryRepo.findAll(ArgumentMatchers.isA(Sort::class.java)))
                .thenReturn(expectedCategories)

        val actualCategories = categoryService.getAllCategories()
        assertNotNull(actualCategories)
        assertEquals(expectedCategoryPayloads.size, actualCategories.size)
        assertEquals(expectedCategoryPayloads, actualCategories)
    }

    @Test
    fun testGetCategory() {
        Mockito.`when`(categoryRepo.findById(1))
                .thenReturn(Optional.of(expectedCategories[0]))
        Mockito.`when`(categoryRepo.findById(2))
                .thenReturn(Optional.of(expectedCategories[1]))

        var actualCategory = categoryService.getCategory(1)
        assertNotNull(actualCategory)
        assertEquals(expectedCategoryPayloads[0], actualCategory)

        actualCategory = categoryService.getCategory(2)
        assertNotNull(actualCategory)
        assertEquals(expectedCategoryPayloads[1], actualCategory)

        actualCategory = categoryService.getCategory(3)
        assertNull(actualCategory)
    }

    @Test
    fun testAddCategory() {
        val request = CategoryPayload(categoryName = THIRD_NAME)
        val response = CategoryPayload(categoryId = 3, categoryName = THIRD_NAME)
        val newCategory = Category(categoryName = THIRD_NAME)
        val newCategoryWithId = Category(categoryId = 3, categoryName = THIRD_NAME)

        Mockito.`when`(categoryRepo.save(newCategory))
                .thenReturn(newCategoryWithId)

        val result = categoryService.addCategory(request)
        assertEquals(response, result)
    }

    @Test
    fun testUpdateCategory() {
        val newCategory = Category(categoryName = THIRD_NAME)
        val newCategoryWithId = Category(categoryId = 1, categoryName = THIRD_NAME)

        val request = CategoryPayload(categoryName = THIRD_NAME)
        val response = CategoryPayload(categoryId = 1, categoryName = THIRD_NAME)

        `when`(categoryRepo.save(newCategoryWithId))
                .thenReturn(newCategoryWithId)
        `when`(categoryRepo.findById(1))
                .thenReturn(Optional.of(expectedCategories[0]))

        var actualCategory = categoryService.updateCategory(1, request)
        assertNotNull(actualCategory)
        assertEquals(response, actualCategory)

        actualCategory = categoryService.updateCategory(3, request)
        assertNull(actualCategory)
    }

    @Test
    fun test_deleteCategory() {
        `when`(categoryRepo.findById(1))
                .thenReturn(Optional.of(expectedCategories[0]))
                .thenReturn(Optional.empty())

        var actualCategory = categoryService.deleteCategory(1)
        assertNotNull(actualCategory)
        assertEquals(expectedCategoryPayloads[0], actualCategory)

        actualCategory = categoryService.deleteCategory(1)
        assertNull(actualCategory)

        Mockito.verify(categoryRepo, Mockito.times(2))
                .deleteById(1)
        Mockito.verify(fileCategoryRepo, Mockito.times(2))
                .deleteAllByCategoryId(1)
    }

}
