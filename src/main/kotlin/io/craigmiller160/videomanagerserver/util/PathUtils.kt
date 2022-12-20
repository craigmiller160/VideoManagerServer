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

fun ensureTrailingSlash(value: String): String {
  if (value.endsWith("/")) return value
  return "$value/"
}

fun parseQueryString(queryString: String): Map<String, String> {
  val queryPairs = HashMap<String, String>()
  val pairs = queryString.split("&")
  pairs
    .filter { pair -> !pair.isBlank() }
    .forEach { pair ->
      val keyValue = pair.split("=")
      queryPairs += keyValue[0] to keyValue[1]
    }
  return queryPairs
}
