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

package io.craigmiller160.videomanagerserver.service.security

import io.craigmiller160.videomanagerserver.dto.VideoTokenResponse
import io.craigmiller160.videomanagerserver.exception.VideoFileNotFoundException
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import io.craigmiller160.videomanagerserver.service.videofile.VideoFileService
import org.springframework.stereotype.Service

@Service
class AuthService (
        private val videoTokenProvider: VideoTokenProvider,
        private val securityContextService: SecurityContextService,
        private val settingsService: SettingsService,
        private val videoFileService: VideoFileService
) {

    fun getVideoToken(videoId: Long): VideoTokenResponse {
        val userName = securityContextService.getUserName()
        val rootDirectory = settingsService.getOrCreateSettings().rootDir
        if (rootDirectory.isEmpty()) {
            throw IllegalStateException("Root directory is not set")
        }
        val videoFile = videoFileService.getVideoFile(videoId)
                ?: throw VideoFileNotFoundException("No video file found for ID: $videoId")
        val filePath = videoFile.fileName
        val fullFilePath = "$rootDirectory/$filePath"
        val params = mapOf(
                TokenConstants.PARAM_VIDEO_ID to videoId,
                TokenConstants.PARAM_FILE_PATH to fullFilePath
        )
        val token = videoTokenProvider.createToken(userName, params)
        return VideoTokenResponse(token)
    }

}
