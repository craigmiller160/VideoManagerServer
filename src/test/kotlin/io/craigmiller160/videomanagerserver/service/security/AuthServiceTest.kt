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

import io.craigmiller160.videomanagerserver.dto.SettingsPayload
import io.craigmiller160.videomanagerserver.dto.VideoFilePayload
import io.craigmiller160.videomanagerserver.dto.VideoTokenResponse
import io.craigmiller160.videomanagerserver.entity.Settings
import io.craigmiller160.videomanagerserver.entity.VideoFile
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import io.craigmiller160.videomanagerserver.service.videofile.VideoFileService
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner.Silent::class)
class AuthServiceTest {

    companion object {
        private const val ROOT_DIR = "/root/dir"
        private const val FILE_PATH = "/file/path"
    }

    @Mock
    private lateinit var videoTokenProvider: VideoTokenProvider
    @Mock
    private lateinit var securityContextService: SecurityContextService
    @Mock
    private lateinit var settingsService: SettingsService
    @Mock
    private lateinit var videoFileService: VideoFileService

    @InjectMocks
    private lateinit var authService: AuthService

    private val settings = SettingsPayload(
            rootDir = ROOT_DIR
    )
    private val videoFile = VideoFilePayload(
            fileName = FILE_PATH
    )

    @Test
    fun test_getVideoToken() {
        val userName = "userName"
        val videoId = 10L
        val token = "ABCDEFG"

        `when`(securityContextService.getUserName())
                .thenReturn(userName)
        `when`(videoTokenProvider.createToken(userName, mapOf(TokenConstants.PARAM_VIDEO_ID to videoId)))
                .thenReturn(token)
        `when`(settingsService.getOrCreateSettings())
                .thenReturn(settings)
        `when`(videoFileService.getVideoFile(videoId))
                .thenReturn(videoFile)

        val result = authService.getVideoToken(videoId)
        assertEquals(VideoTokenResponse(token), result)
    }

    @Test
    fun test_getVideoToken_noFileFound() {
        TODO("Finish this")
    }

}
