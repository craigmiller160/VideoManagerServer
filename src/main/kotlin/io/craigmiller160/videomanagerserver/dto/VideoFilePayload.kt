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

package io.craigmiller160.videomanagerserver.dto

import io.craigmiller160.videomanagerserver.util.DEFAULT_TIMESTAMP
import java.time.LocalDateTime

// TODO this does not have all the fields from VideoFile
// TODO lastScanTimestamp and active are not here
data class VideoFilePayload(
  var fileId: Long = 0,
  var fileName: String = "",
  var displayName: String = "",
  var description: String = "",
  var lastModified: LocalDateTime = DEFAULT_TIMESTAMP,
  var fileAdded: LocalDateTime? = null,
  var lastViewed: LocalDateTime? = null,
  var viewCount: Int = 0,
  var categories: MutableSet<CategoryPayload> = HashSet(),
  var series: MutableSet<SeriesPayload> = HashSet(),
  var stars: MutableSet<StarPayload> = HashSet()
)
