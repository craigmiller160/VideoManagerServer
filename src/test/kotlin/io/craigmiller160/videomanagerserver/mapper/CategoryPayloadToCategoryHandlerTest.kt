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

package io.craigmiller160.videomanagerserver.mapper

import io.craigmiller160.videomanagerserver.dto.CategoryPayload
import io.craigmiller160.videomanagerserver.entity.Category
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.jupiter.api.Test

class CategoryPayloadToCategoryHandlerTest {

  private val handler = CategoryPayloadToCategoryHandler()

  @Test
  fun test_handleExisting() {
    val source = CategoryPayload()
    val existing = Category(hidden = true)
    val destination = Category(categoryName = "Category")
    handler.handleExisting(source, existing, destination)
    assertThat(
      destination,
      allOf(
        hasProperty("categoryName", equalTo(destination.categoryName)),
        hasProperty("hidden", equalTo(existing.hidden))))
  }
}
