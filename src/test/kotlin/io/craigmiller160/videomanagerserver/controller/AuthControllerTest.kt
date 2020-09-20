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

import io.craigmiller160.videomanagerserver.dto.*
import io.craigmiller160.videomanagerserver.entity.AppUser
import io.craigmiller160.videomanagerserver.service.security.AuthService
import io.craigmiller160.videomanagerserver.test_util.responseBody
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration


@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class AuthControllerTest : AbstractControllerTest() {

    companion object {
        private const val ROLE = "MyRole"
        private const val USER_NAME = "UserName"
        private const val PASSWORD = "password"
    }

    @MockBean
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var authController: AuthController

    private lateinit var jacksonRoles: JacksonTester<List<RolePayload>>
    private lateinit var jacksonVideoToken: JacksonTester<VideoTokenResponse>
    private lateinit var jacksonLoginRequest: JacksonTester<LoginRequest>
    private lateinit var jacksonUserRequest: JacksonTester<AppUserRequest>
    private lateinit var jacksonUserResponse: JacksonTester<AppUserResponse>
    private lateinit var jacksonUserResponseList: JacksonTester<List<AppUserResponse>>

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Test
    fun test_getVideoToken() {
        val user = AppUser().apply {
            userId = 1L
            userName = "userName"
        }
        val videoId = 10L
        val token = VideoTokenResponse("ABCDEFG")

        `when`(authService.getVideoToken(videoId))
                .thenReturn(token)
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doGet("/api/auth/videotoken/10")
        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonVideoToken.write(token).json))
        ))
    }

    @Test
    fun test_getVideoToken_unauthorized() {
        val response = mockMvcHandler.doGet("/api/auth/videotoken/10")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

}
