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

import io.craigmiller160.videomanagerserver.test_util.AuthenticationConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class VideoConfigurationIntegrationTest {

  companion object {
    private const val FILE_PATH_ROOT = "FILE_PATH_ROOT"
    private const val PAGE_SIZE = 10
    private const val FILE_EXTS = "FILE_EXTS"
  }

  @Autowired private lateinit var videoConfig: VideoConfiguration
  @MockBean private lateinit var authConfig: AuthenticationConfig
  @MockBean private lateinit var clientRegRepo: ClientRegistrationRepository
  @MockBean private lateinit var authClientRepo: OAuth2AuthorizedClientRepository
  @MockBean private lateinit var oauth2Props: OAuth2ClientProperties

  @Test
  fun testApiPageSize() {
    assertEquals(PAGE_SIZE, videoConfig.apiPageSize)
  }

  @Test
  fun testFileExts() {
    assertEquals(FILE_EXTS, videoConfig.fileExts)
  }
}
