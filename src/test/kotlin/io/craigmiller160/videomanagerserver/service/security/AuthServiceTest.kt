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
import io.craigmiller160.videomanagerserver.entity.AppUser
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner.Silent::class)
class AuthServiceTest {

    @Mock
    private lateinit var videoTokenProvider: VideoTokenProvider

    @Mock
    private lateinit var securityContextService: SecurityContextService

    @InjectMocks
    private lateinit var authService: AuthService

    @Test
    fun test_getVideoToken() {
        val userName = "userName"
        val user = AppUser(userName = userName)
        val videoId = 10L
        val token = "ABCDEFG"

        `when`(securityContextService.getUserName())
                .thenReturn(userName)
        `when`(videoTokenProvider.createToken(user, mapOf(TokenConstants.PARAM_VIDEO_ID to videoId)))
                .thenReturn(token)

        val result = authService.getVideoToken(videoId)
        assertEquals(VideoTokenResponse(token), result)
    }

}
