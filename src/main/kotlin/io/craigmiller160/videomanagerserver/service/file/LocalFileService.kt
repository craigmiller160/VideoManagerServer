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

package io.craigmiller160.videomanagerserver.service.file

import io.craigmiller160.videomanagerserver.dto.LocalFileListResponse
import io.craigmiller160.videomanagerserver.dto.LocalFileResponse
import java.io.File
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class LocalFileService(@Value("\${localfile.homeDir}") private val userHome: String) {

  fun getFilesFromDirectory(path: String?, onlyDirectories: Boolean): LocalFileListResponse {
    val dirPath = path ?: userHome
    val files =
      File(dirPath)
        .listFiles { file ->
          if (onlyDirectories) {
            return@listFiles !file.isHidden && file.isDirectory
          }
          !file.isHidden
        }
        ?.map { file -> LocalFileResponse(file) }
        ?.sortedBy { localFile -> localFile.fileName }
        ?: listOf()
    val parentPath = File(dirPath).parentFile?.absolutePath ?: ""
    return LocalFileListResponse(rootPath = dirPath, parentPath = parentPath, files = files)
  }
}
