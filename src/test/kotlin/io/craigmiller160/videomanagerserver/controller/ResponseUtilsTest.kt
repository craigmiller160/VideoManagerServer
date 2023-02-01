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

import java.util.Optional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ResponseUtilsTest {

  @Test
  fun test_okOrNoContent_optional_ok() {
    val value = "Hello"
    val result = okOrNoContent(Optional.of(value))
    assertNotNull(result)
    assertEquals(200, result.statusCode.value())
    assertEquals(value, result.body)
  }

  @Test
  fun test_okOrNoContent_optional_noContent() {
    val result = okOrNoContent<Nothing>(Optional.empty())
    assertNotNull(result)
    assertEquals(204, result.statusCode.value())
  }

  @Test
  fun test_okOrNoContent_ok() {
    val value = "Hello"
    val result = okOrNoContent(value)
    assertNotNull(result)
    assertEquals(200, result.statusCode.value())
    assertEquals(value, result.body)
  }

  @Test
  fun test_okOrNoContent_noContent() {
    val result = okOrNoContent<String>(null)
    assertNotNull(result)
    assertEquals(204, result.statusCode.value())
  }
}
