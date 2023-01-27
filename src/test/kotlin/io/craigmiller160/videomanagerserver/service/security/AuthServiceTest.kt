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

import com.nhaarman.mockito_kotlin.whenever
import io.craigmiller160.videomanagerserver.dto.SettingsPayload
import io.craigmiller160.videomanagerserver.dto.VideoFilePayload
import io.craigmiller160.videomanagerserver.dto.VideoTokenResponse
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import io.craigmiller160.videomanagerserver.service.videofile.VideoFileService
import java.util.UUID
import kotlin.test.assertFails
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

  companion object {
    private const val ROOT_DIR = "/root/dir"
    private const val FILE_PATH = "/file/path"
  }

  @Mock private lateinit var videoTokenProvider: VideoTokenProvider
  @Mock private lateinit var securityContextService: SecurityContextService
  @Mock private lateinit var settingsService: SettingsService
  @Mock private lateinit var videoFileService: VideoFileService

  @InjectMocks private lateinit var authService: AuthService

  private val settings = SettingsPayload(rootDir = ROOT_DIR)
  private val videoFile = VideoFilePayload(fileName = FILE_PATH)

  @Test
  fun test_getVideoToken() {
    val userName = "bobsaget"
    val videoId = 10L
    val token = "ABCDEFG"
    val userId = UUID.randomUUID()
    whenever(securityContextService.getUserId()).thenReturn(userId)

    whenever(
        videoTokenProvider.createToken(
          "dummyUserName",
          mapOf(
            TokenConstants.PARAM_VIDEO_ID to videoId,
            TokenConstants.PARAM_FILE_PATH to "$ROOT_DIR/$FILE_PATH",
            TokenConstants.PARAM_USER_ID to 1L)))
      .thenReturn(token)
    `when`(settingsService.getOrCreateSettings()).thenReturn(settings)
    `when`(videoFileService.getVideoFile(videoId)).thenReturn(videoFile)

    val result = authService.getVideoToken(videoId)
    assertEquals(VideoTokenResponse(token), result)
  }

  @Test
  fun test_getVideoToken_noFileFound() {
    val userName = "userName"
    val videoId = 10L
    val token = "ABCDEFG"
    `when`(settingsService.getOrCreateSettings()).thenReturn(settings)

    assertFails("No video file found for ID: $videoId") { authService.getVideoToken(videoId) }
  }
}
