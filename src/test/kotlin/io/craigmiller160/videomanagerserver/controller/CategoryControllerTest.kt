/*
 *     video-manager-server
 *     Copyright (C) 2020 Craig Miller
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.CategoryPayload
import io.craigmiller160.videomanagerserver.service.videofile.CategoryService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration

@ExtendWith(SpringExtension::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class CategoryControllerTest : AbstractControllerTest() {

  @MockBean private lateinit var categoryService: CategoryService

  @Autowired private lateinit var categoryController: CategoryController

  private lateinit var jacksonCategoryList: JacksonTester<List<CategoryPayload>>
  private lateinit var jacksonCategory: JacksonTester<CategoryPayload>

  private lateinit var categoryNoId: CategoryPayload
  private lateinit var category1: CategoryPayload
  private lateinit var category2: CategoryPayload
  private lateinit var category3: CategoryPayload
  private lateinit var categoryList: List<CategoryPayload>

  @BeforeEach
  override fun setup() {
    super.setup()
    categoryNoId = CategoryPayload(categoryName = "NoId")
    category1 = CategoryPayload(1, "FirstCategory")
    category2 = CategoryPayload(2, "SecondCategory")
    category3 = CategoryPayload(3, "ThirdCategory")
    categoryList = listOf(category1, category2, category3)
  }

  @Test
  fun testGetAllCategories() {
    mockMvcHandler.token = token
    `when`(categoryService.getAllCategories()).thenReturn(categoryList).thenReturn(listOf())

    var response = mockMvcHandler.doGet("/api/categories")
    assertOkResponse(response, jacksonCategoryList.write(categoryList).json)

    response = mockMvcHandler.doGet("/api/categories")
    assertNoContentResponse(response)
  }

  @Test
  fun test_getAllCategories_unauthorized() {
    val response = mockMvcHandler.doGet("/api/categories")
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun testGetCategory() {
    mockMvcHandler.token = token
    `when`(categoryService.getCategory(1)).thenReturn(category1)
    `when`(categoryService.getCategory(5)).thenReturn(null)

    var response = mockMvcHandler.doGet("/api/categories/1")
    assertOkResponse(response, jacksonCategory.write(category1).json)

    response = mockMvcHandler.doGet("/api/categories/5")
    assertNoContentResponse(response)
  }

  @Test
  fun test_getCategory_unauthorized() {
    val response = mockMvcHandler.doGet("/api/categories/1")
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun testAddCategory() {
    mockMvcHandler.token = editToken
    val categoryWithId = categoryNoId.copy(categoryId = 1)
    `when`(categoryService.addCategory(categoryNoId)).thenReturn(categoryWithId)

    val response =
      mockMvcHandler.doPost("/api/categories", jacksonCategory.write(categoryNoId).json)
    assertOkResponse(response, jacksonCategory.write(categoryWithId).json)
  }

  @Test
  fun test_addCategory_unauthorized() {
    val response =
      mockMvcHandler.doPost("/api/categories", jacksonCategory.write(categoryNoId).json)
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun test_addCategory_missingRole() {
    mockMvcHandler.token = token

    val response =
      mockMvcHandler.doPost("/api/categories", jacksonCategory.write(categoryNoId).json)
    assertThat(response, hasProperty("status", equalTo(403)))
  }

  @Test
  fun testUpdateCategory() {
    mockMvcHandler.token = editToken
    val updatedCategory = category2.copy(categoryId = 1)
    `when`(categoryService.updateCategory(1, category2)).thenReturn(updatedCategory)
    `when`(categoryService.updateCategory(5, category3)).thenReturn(null)

    var response = mockMvcHandler.doPut("/api/categories/1", jacksonCategory.write(category2).json)
    assertOkResponse(response, jacksonCategory.write(updatedCategory).json)

    response = mockMvcHandler.doPut("/api/categories/5", jacksonCategory.write(category3).json)
    assertNoContentResponse(response)
  }

  @Test
  fun test_updateCategory_unauthorized() {
    val response =
      mockMvcHandler.doPut("/api/categories/1", jacksonCategory.write(categoryNoId).json)
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun test_updateCategory_missingRole() {
    mockMvcHandler.token = token

    val response =
      mockMvcHandler.doPut("/api/categories/1", jacksonCategory.write(categoryNoId).json)
    assertThat(response, hasProperty("status", equalTo(403)))
  }

  @Test
  fun testDeleteCategory() {
    mockMvcHandler.token = editToken
    `when`(categoryService.deleteCategory(1)).thenReturn(category1).thenReturn(null)

    var response = mockMvcHandler.doDelete("/api/categories/1")
    assertOkResponse(response, jacksonCategory.write(category1).json)

    response = mockMvcHandler.doDelete("/api/categories/5")
    assertNoContentResponse(response)
  }

  @Test
  fun test_deleteCategory_unauthorized() {
    val response = mockMvcHandler.doDelete("/api/categories/1")
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun test_deleteCategory_missingRole() {
    mockMvcHandler.token = token

    val response = mockMvcHandler.doDelete("/api/categories/1")
    assertThat(response, hasProperty("status", equalTo(403)))
  }
}
