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

package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.oauth2.config.OAuth2Config
import io.craigmiller160.videomanagerserver.entity.Category
import io.craigmiller160.videomanagerserver.entity.VideoFile
import io.craigmiller160.videomanagerserver.test_util.DbTestUtils
import kotlin.test.assertEquals
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
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
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class FileCategoryRepositoryIntegrationTest {

  companion object {
    private const val CATEGORY_NAME = "categoryName"
    private const val CATEGORY_2_NAME = "category2Name"
    private const val FILE_NAME = "fileName"
    private const val FILE_2_NAME = "file2Name"
  }

  @Autowired private lateinit var categoryRepository: CategoryRepository
  @Autowired private lateinit var fileCategoryRepository: FileCategoryRepository
  @Autowired private lateinit var videoFileRepository: VideoFileRepository
  @Autowired private lateinit var dbTestUtils: DbTestUtils

  @MockBean private lateinit var oauthConfig: OAuth2Config

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

    val category2 = Category(categoryName = CATEGORY_2_NAME)
    val file2 = VideoFile(fileName = FILE_2_NAME)
    file2.categories.add(category2)
    videoFileRepository.save(file2)
  }

  @After
  fun clean() {
    dbTestUtils.cleanDb()
  }

  private fun validateRecordsExist() {
    assertEquals(2, videoFileRepository.count())
    assertEquals(2, categoryRepository.count())
    assertEquals(2, fileCategoryRepository.count())
  }

  @Test
  fun test_deleteAllByCategoryId() {
    validateRecordsExist()
    fileCategoryRepository.deleteAllByCategoryId(categoryId)
    val results = fileCategoryRepository.findAll()
    assertThat(
      results, allOf(hasSize(1), contains(hasProperty("categoryId", not(equalTo(categoryId))))))
  }

  @Test
  fun test_deleteAllByFileId() {
    validateRecordsExist()
    fileCategoryRepository.deleteAllByFileId(fileId)
    val results = fileCategoryRepository.findAll()
    assertThat(results, allOf(hasSize(1), contains(hasProperty("fileId", not(equalTo(fileId))))))
  }
}
