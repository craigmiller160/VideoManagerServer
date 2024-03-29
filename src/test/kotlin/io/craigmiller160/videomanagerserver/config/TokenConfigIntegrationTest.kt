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
import kotlin.test.assertEquals
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
class TokenConfigIntegrationTest {

  companion object {
    private const val EXP_SECS = 300
    private const val VIDEO_EXP_SECS = 10000
    private const val REFRESH_EXP_SECS = 1200
    private const val KEY = "/RoM6KSD6jiYtYUOmd1klD4dtzpKs6vxJbLWT8DjsbM="
  }

  @Autowired private lateinit var tokenConfig: TokenConfig
  @MockBean private lateinit var authConfig: AuthenticationConfig
  @MockBean private lateinit var clientRegRepo: ClientRegistrationRepository
  @MockBean private lateinit var authClientRepo: OAuth2AuthorizedClientRepository
  @MockBean private lateinit var oauth2Props: OAuth2ClientProperties

  @Test
  fun test_expSecs() {
    assertEquals(EXP_SECS, tokenConfig.expSecs)
  }

  @Test
  fun test_videoExpSecs() {
    assertEquals(VIDEO_EXP_SECS, tokenConfig.videoExpSecs)
  }

  @Test
  fun test_refreshExpSecs() {
    assertEquals(REFRESH_EXP_SECS, tokenConfig.refreshExpSecs)
  }

  @Test
  fun test_key() {
    assertEquals(KEY, tokenConfig.key)
  }
}
