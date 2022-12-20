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

import io.craigmiller160.oauth2.config.OAuth2Config
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class VideoConfigurationIntegrationTest {

  companion object {
    private const val FILE_PATH_ROOT = "FILE_PATH_ROOT"
    private const val PAGE_SIZE = 10
    private const val FILE_EXTS = "FILE_EXTS"
  }

  @MockBean private lateinit var oauthConfig: OAuth2Config

  @Autowired private lateinit var videoConfig: VideoConfiguration

  @Test
  fun testApiPageSize() {
    assertEquals(PAGE_SIZE, videoConfig.apiPageSize)
  }

  @Test
  fun testFileExts() {
    assertEquals(FILE_EXTS, videoConfig.fileExts)
  }
}
