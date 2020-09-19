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

import io.craigmiller160.videomanagerserver.dto.LocalFileListResponse
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.service.file.LocalFileService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/localfiles")
class LocalFileController(private val localFileService: LocalFileService) {

    @Secured(ROLE_ADMIN)
    @GetMapping("/directory")
    fun getFilesFromDirectory(
            @RequestParam(required = false) path: String?,
            @RequestParam(required = false, defaultValue = "false") onlyDirectories: Boolean
    ): ResponseEntity<LocalFileListResponse> {
        val actualPath = if (path == null || path.isEmpty()) null else path
        val fileList = localFileService.getFilesFromDirectory(actualPath, onlyDirectories)
        return ResponseEntity.ok(fileList)
    }

}
