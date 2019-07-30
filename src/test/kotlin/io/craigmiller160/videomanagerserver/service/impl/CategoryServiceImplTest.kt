package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.dto.Category
import io.craigmiller160.videomanagerserver.repository.CategoryRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.isA
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.Sort
import java.util.Optional

class CategoryServiceImplTest {

    companion object {

        private const val FIRST_NAME = "FirstName"
        private const val SECOND_NAME = "SecondName"
        private const val THIRD_NAME = "ThirdName"

        private val expectedCategories = listOf(
                Category(categoryId = 1, categoryName = FIRST_NAME),
                Category(categoryId = 2, categoryName = SECOND_NAME)
        )

    }

    private lateinit var categoryService: CategoryServiceImpl

    @Mock
    private lateinit var categoryRepo: CategoryRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        categoryService = CategoryServiceImpl(categoryRepo)
    }

    @Test
    fun testGetAllCategories() {
        `when`(categoryRepo.findAll(isA(Sort::class.java)))
                .thenReturn(expectedCategories)

        val actualCategories = categoryService.getAllCategories()
        assertNotNull(actualCategories)
        assertEquals(expectedCategories.size, actualCategories.size)
        assertEquals(expectedCategories, actualCategories)
    }

    @Test
    fun testGetCategory() {
        `when`(categoryRepo.findById(1))
                .thenReturn(Optional.of(expectedCategories[0]))
        `when`(categoryRepo.findById(2))
                .thenReturn(Optional.of(expectedCategories[1]))

        var actualCategory = categoryService.getCategory(1)
        assertTrue(actualCategory.isPresent)
        assertEquals(expectedCategories[0], actualCategory.get())

        actualCategory = categoryService.getCategory(2)
        assertTrue(actualCategory.isPresent)
        assertEquals(expectedCategories[1], actualCategory.get())

        actualCategory = categoryService.getCategory(3)
        assertFalse(actualCategory.isPresent)
    }

    @Test
    fun testAddCategory() {
        val newCategory = Category(categoryName = THIRD_NAME)
        val newCategoryWithId = Category(categoryId = 3, categoryName = THIRD_NAME)

        `when`(categoryRepo.save(newCategory))
                .thenReturn(newCategoryWithId)

        val actualCategory = categoryService.addCategory(newCategory)
        assertEquals(newCategoryWithId, actualCategory)
    }

    @Test
    fun testUpdateCategory() {
        val newCategory = Category(categoryName = THIRD_NAME)
        val newCategoryWithId = Category(categoryId = 1, categoryName = THIRD_NAME)

        `when`(categoryRepo.save(newCategoryWithId))
                .thenReturn(newCategoryWithId)
        `when`(categoryRepo.findById(1))
                .thenReturn(Optional.of(expectedCategories[0]))

        var actualCategory = categoryService.updateCategory(1, newCategory)
        assertTrue(actualCategory.isPresent)
        assertEquals(newCategoryWithId, actualCategory.get())

        actualCategory = categoryService.updateCategory(3, newCategory)
        assertFalse(actualCategory.isPresent)
    }

    @Test
    fun testDeleteCategory() {
        `when`(categoryRepo.findById(1))
                .thenReturn(Optional.of(expectedCategories[0]))
                .thenReturn(Optional.empty())

        var actualCategory = categoryService.deleteCategory(1)
        assertTrue(actualCategory.isPresent)
        assertEquals(expectedCategories[0], actualCategory.get())

        actualCategory = categoryService.deleteCategory(1)
        assertFalse(actualCategory.isPresent)
    }

}