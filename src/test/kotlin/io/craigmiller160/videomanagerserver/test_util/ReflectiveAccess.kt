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

package io.craigmiller160.videomanagerserver.test_util

import org.springframework.util.ReflectionUtils

fun <T> getField(obj: Any, fieldName: String, clazz: Class<T>): T {
  val field = ReflectionUtils.findField(obj.javaClass, fieldName)
  return field?.let { f ->
    ReflectionUtils.makeAccessible(f)
    clazz.cast(f.get(obj))
  }
    ?: throw ReflectiveOperationException("Unable to access the field $fieldName")
}
