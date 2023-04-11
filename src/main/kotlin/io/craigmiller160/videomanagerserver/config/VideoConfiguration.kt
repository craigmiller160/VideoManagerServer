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

package io.craigmiller160.videomanagerserver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Suppress("ConfigurationProperties")
@Configuration
@Validated
@ConfigurationProperties(prefix = "video")
data class VideoConfiguration(
  var vlcCommand: String = "",
  var apiPageSize: Int = 0,
  var fileExts: String = "",
  var converterFileExts: String = ""
) {

  fun splitFileExts(): List<String> {
    return fileExts.split(",").map { ext -> ext.trim() }
  }

  fun splitConverterFileExts(): List<String> {
    return converterFileExts.split(",").map { it.trim() }
  }
}
