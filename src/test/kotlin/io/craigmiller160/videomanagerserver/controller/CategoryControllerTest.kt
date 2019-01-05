package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.Category
import io.craigmiller160.videomanagerserver.service.CategoryService
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.json.JacksonTester
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class CategoryControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var categoryService: CategoryService

    private lateinit var categoryController: CategoryController

    private lateinit var jacksonCategoryList: JacksonTester<List<Category>>
    private lateinit var jacksonCategory: JacksonTester<Category>

    private lateinit var category1: Category
    private lateinit var category2: Category
    private lateinit var category3: Category
    private lateinit var categoryList: List<Category>

    @Before
    fun setup() {
        category1 = Category(1, "FirstCategory")
        category2 = Category(2, "SecondCategory")
        category3 = Category(3, "ThirdCategory")
        categoryList = listOf(category1, category2, category3)

        MockitoAnnotations.initMocks(this)
        JacksonTester.initFields(this, ObjectMapper())

        categoryController = CategoryController(categoryService)
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build()
    }

    @Test
    fun testGetAllCategories() {
        `when`(categoryService.getAllCategories())
                .thenReturn(categoryList)
                .thenReturn(listOf())

        var response = mockMvc.perform(
                get("/categories")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertEquals(200, response.status)
        assertEquals(CONTENT_TYPE_JSON, response.contentType)
        assertEquals(response.contentAsString, jacksonCategoryList.write(categoryList).json)

        response = mockMvc.perform(
                get("/categories")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertEquals(204, response.status)
    }

}