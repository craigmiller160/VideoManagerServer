package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Category
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import io.craigmiller160.videomanagerserver.service.CategoryService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.Optional

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class CategoryControllerTest {

    // TODO add tests for unauthorized access for all methods

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @Mock
    private lateinit var categoryService: CategoryService

    @Autowired
    private lateinit var categoryController: CategoryController

    private lateinit var jacksonCategoryList: JacksonTester<List<Category>>
    private lateinit var jacksonCategory: JacksonTester<Category>

    private lateinit var categoryNoId: Category
    private lateinit var category1: Category
    private lateinit var category2: Category
    private lateinit var category3: Category
    private lateinit var categoryList: List<Category>

    @Autowired
    private lateinit var webAppContext: WebApplicationContext

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Before
    fun setup() {
        categoryNoId = Category(categoryName = "NoId")
        category1 = Category(1, "FirstCategory")
        category2 = Category(2, "SecondCategory")
        category3 = Category(3, "ThirdCategory")
        categoryList = listOf(category1, category2, category3)

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
                .build()
        mockMvcHandler = MockMvcHandler(mockMvc)
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))

        JacksonTester.initFields(this, ObjectMapper())
        MockitoAnnotations.initMocks(this)

        ReflectionTestUtils.setField(categoryController, "categoryService", categoryService)
    }

    @Test
    fun testGetAllCategories() {
        `when`(categoryService.getAllCategories())
                .thenReturn(categoryList)
                .thenReturn(listOf())

        var response = mockMvcHandler.doGet("/categories")
        assertOkResponse(response, jacksonCategoryList.write(categoryList).json)

        response = mockMvcHandler.doGet("/categories")
        assertNoContentResponse(response)
    }

    @Test
    fun testGetCategory() {
        `when`(categoryService.getCategory(1))
                .thenReturn(Optional.of(category1))
        `when`(categoryService.getCategory(5))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doGet("/categories/1")
        assertOkResponse(response, jacksonCategory.write(category1).json)

        response = mockMvcHandler.doGet("/categories/5")
        assertNoContentResponse(response)
    }

    @Test
    fun testAddCategory() {
        val categoryWithId = categoryNoId.copy(categoryId = 1)
        `when`(categoryService.addCategory(categoryNoId))
                .thenReturn(categoryWithId)

        val response = mockMvcHandler.doPost("/categories", jacksonCategory.write(categoryNoId).json)
        assertOkResponse(response, jacksonCategory.write(categoryWithId).json)
    }

    @Test
    fun testUpdateCategory() {
        val updatedCategory = category2.copy(categoryId = 1)
        `when`(categoryService.updateCategory(1, category2))
                .thenReturn(Optional.of(updatedCategory))
        `when`(categoryService.updateCategory(5, category3))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doPut("/categories/1", jacksonCategory.write(category2).json)
        assertOkResponse(response, jacksonCategory.write(updatedCategory).json)

        response = mockMvcHandler.doPut("/categories/5", jacksonCategory.write(category3).json)
        assertNoContentResponse(response)
    }

    @Test
    fun testDeleteCategory() {
        `when`(categoryService.deleteCategory(1))
                .thenReturn(Optional.of(category1))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doDelete("/categories/1")
        assertOkResponse(response, jacksonCategory.write(category1).json)

        response = mockMvcHandler.doDelete("/categories/5")
        assertNoContentResponse(response)
    }

}