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

package io.craigmiller160.videomanagerserver.util

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LegacyDateConverterTest {

  private val legacyDateConverter = LegacyDateConverter()

  @Test
  fun test_convertLocalDateTimeToDate() {
    val localDateTime = LocalDateTime.of(2019, 1, 1, 1, 1)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val date = legacyDateConverter.convertLocalDateTimeToDate(localDateTime)
    assertEquals("2019-01-01 01:01", format.format(date))
  }

  @Test
  fun test_convertDateToLocalDateTime() {
    val date = Date(119, 0, 1, 1, 1)
    val localDateTime = legacyDateConverter.convertDateToLocalDateTime(date)
    val expected = LocalDateTime.of(2019, 1, 1, 1, 1)
    assertEquals(expected, localDateTime)
  }
}
