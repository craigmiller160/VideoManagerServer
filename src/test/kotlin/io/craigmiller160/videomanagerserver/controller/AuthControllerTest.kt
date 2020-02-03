package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.controller.AuthController.Companion.DEFAULT_MAX_AGE
import io.craigmiller160.videomanagerserver.dto.AppUserRequest
import io.craigmiller160.videomanagerserver.dto.AppUserResponse
import io.craigmiller160.videomanagerserver.dto.LoginRequest
import io.craigmiller160.videomanagerserver.entity.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.dto.VideoToken
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.security.COOKIE_NAME
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.security.tokenprovider.JwtTokenProvider
import io.craigmiller160.videomanagerserver.service.security.AuthService
import io.craigmiller160.videomanagerserver.test_util.header
import io.craigmiller160.videomanagerserver.test_util.responseBody
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.isEmptyString
import org.hamcrest.Matchers.matchesPattern
import org.junit.Before
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
import java.time.Duration


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

    // TODO clean these up
    private lateinit var jacksonUser: JacksonTester<AppUser>
    private lateinit var jacksonRoles: JacksonTester<List<Role>>
    private lateinit var jacksonUserList: JacksonTester<List<AppUser>>
    private lateinit var jacksonVideoToken: JacksonTester<VideoToken>
    private lateinit var jacksonLoginRequest: JacksonTester<LoginRequest>
    private lateinit var jacksonUserRequest: JacksonTester<AppUserRequest>
    private lateinit var jacksonUserResponse: JacksonTester<AppUserResponse>
    private lateinit var jacksonUserResponseList: JacksonTester<List<AppUserResponse>>

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Test
    fun test_login() {
        val request = LoginRequest(
            userName = "userName",
            password = "password"
        )
        val token = "ABCDEFG"
        `when`(authService.login(request))
                .thenReturn(token)

        val response = mockMvcHandler.doPost("/api/auth/login", jacksonLoginRequest.write(request).json)
        assertThat(response, allOf(
                hasProperty("status", equalTo(204)),
                header("Set-Cookie", matchesPattern("vm_token=ABCDEFG; Path=/; Max-Age=1000000; Expires=.+; Secure; HttpOnly; SameSite=strict"))
        ))
    }

    @Test
    fun test_login_badLogin() {
        val badRequest = LoginRequest(
            userName = "userName",
            password = "bad_password"
        )
        `when`(authService.login(badRequest))
                .thenThrow(ApiUnauthorizedException("Invalid login"))

        val response = mockMvcHandler.doPost("/api/auth/login", jacksonLoginRequest.write(badRequest).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_refreshToken() {
        val token1 = "token1"
        val token2 = "token2"

        `when`(authService.refreshToken(token1))
                .thenReturn(token2)

        mockMvcHandler.token = token1

        val response = mockMvcHandler.doGet("/api/auth/refresh")
        assertThat(response, allOf(
                hasProperty("status", equalTo(204)),
                header("Set-Cookie", matchesPattern("vm_token=token2; Path=/; Max-Age=1000000; Expires=.+; Secure; HttpOnly; SameSite=strict"))
        ))
    }

    @Test
    fun test_refreshToken_noToken() {
        val response = mockMvcHandler.doGet("/api/auth/refresh")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_getRoles_unauthorized() {
        val roles = listOf(Role(name = ROLE))
        `when`(authService.getRoles())
                .thenReturn(roles)

        val response = mockMvcHandler.doGet("/api/auth/roles")

        assertThat(response, allOf(
                hasProperty("status", equalTo(401))
        ))
    }

    @Test
    fun test_getRoles_lacksRole() {
        val user = AppUser().apply {
            userName = "userName"
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val roles = listOf(Role(name = ROLE))
        `when`(authService.getRoles())
                .thenReturn(roles)

        val response = mockMvcHandler.doGet("/api/auth/roles")

        assertThat(response, allOf(
                hasProperty("status", equalTo(403))
        ))
    }

    @Test
    fun test_getRoles() {
        val user = AppUser().apply {
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val roles = listOf(Role(name = ROLE))
        `when`(authService.getRoles())
                .thenReturn(roles)

        val response = mockMvcHandler.doGet("/api/auth/roles")

        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonRoles.write(roles).json))
        ))
    }

    @Test
    fun test_getRoles_noRoles() {
        val user = AppUser().apply {
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doGet("/api/auth/roles")

        assertThat(response, allOf(
                hasProperty("status", equalTo(204)),
                responseBody(isEmptyString())
        ))
    }

    @Test
    fun test_createUser() {
        val userRequest = AppUserRequest(
            userName = USER_NAME,
            password = PASSWORD
        )
        val user = AppUser().apply {
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        val userResponse = AppUserResponse(
                userId = 1L,
                userName = USER_NAME
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        `when`(authService.createUser(userRequest))
                .thenReturn(userResponse)

        val response = mockMvcHandler.doPost("/api/auth/users", jacksonUserRequest.write(userRequest).json)

        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonUserResponse.write(userResponse).json))
        ))
    }

    @Test
    fun test_createUser_unauthorized() {
        val userRequest = AppUser().apply {
            userName = USER_NAME
            password = PASSWORD
        }

        val response = mockMvcHandler.doPost("/api/auth/users", jacksonUser.write(userRequest).json)

        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_createUser_lacksRole() {
        val userRequest = AppUser().apply {
            userName = USER_NAME
            password = PASSWORD
        }
        val user = AppUser().apply {
            userName = "userName"
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doPost("/api/auth/users", jacksonUser.write(userRequest).json)

        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun test_getAllUsers() {
        val user = AppUser().apply {
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val userResponse = AppUserResponse(
                userName = "userName",
                roles = listOf(Role(name = ROLE_ADMIN))
        )

        val users = listOf(userResponse)
        `when`(authService.getAllUsers())
                .thenReturn(users)

        val response = mockMvcHandler.doGet("/api/auth/users")

        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonUserResponseList.write(users).json))
        ))
    }

    @Test
    fun test_getAllUsers_noUsers() {
        val user = AppUser().apply {
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        `when`(authService.getAllUsers())
                .thenReturn(listOf())

        val response = mockMvcHandler.doGet("/api/auth/users")

        assertThat(response, allOf(
                hasProperty("status", equalTo(204))
        ))
    }

    @Test
    fun test_getAllUsers_unauthorized() {
        val response = mockMvcHandler.doGet("/api/auth/users")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_getAllUsers_lacksRole() {
        val user = AppUser().apply {
            userName = "userName"
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doGet("/api/auth/users")
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun test_getUser() {
        TODO("Fix this")
//        val userId = 1L
//        val user = AppUser().apply {
//            this.userId = userId
//            userName = "userName"
//            roles = listOf(Role(name = ROLE_ADMIN))
//        }
//        mockMvcHandler.token = jwtTokenProvider.createToken(user)
//
//        `when`(authService.getUser(userId))
//                .thenReturn(user)
//
//        val response = mockMvcHandler.doGet("/api/auth/users/admin/$userId")
//
//        assertThat(response, allOf(
//                hasProperty("status", equalTo(200)),
//                responseBody(equalTo(jacksonUser.write(user).json))
//        ))
    }

    @Test
    fun test_getUser_notFound() {
        val user = AppUser().apply {
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doGet("/api/auth/users/admin/1")
        assertThat(response, hasProperty("status", equalTo(204)))
    }

    @Test
    fun test_getUser_unauthorized() {
        val response = mockMvcHandler.doGet("/api/auth/users/admin/1")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_getUser_lacksRole() {
        val user = AppUser().apply {
            userName = "userName"
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doGet("/api/auth/users/admin/1")
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun test_updateUserAdmin() {
        TODO("Fix this")
//        val userId = 1L
//        val user = AppUser().apply {
//            userName = "userName"
//            roles = listOf(Role(name = ROLE_ADMIN))
//        }
//        mockMvcHandler.token = jwtTokenProvider.createToken(user)
//
//        val userResponse = user.copy(userId = userId)
//
//        `when`(authService.updateUserAdmin(userId, user))
//                .thenReturn(userResponse)
//
//        val response = mockMvcHandler.doPut("/api/auth/users/admin/$userId", jacksonUser.write(user).json)
//
//        assertThat(response, allOf(
//                hasProperty("status", equalTo(200)),
//                responseBody(equalTo(jacksonUser.write(userResponse).json))
//        ))
    }

    @Test
    fun test_updateUserAdmin_notFound() {
        val userId = 1L
        val user = AppUser().apply {
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doPut("/api/auth/users/admin/$userId", jacksonUser.write(user).json)

        assertThat(response, allOf(
                hasProperty("status", equalTo(204))
        ))
    }

    @Test
    fun test_updateUserAdmin_unauthorized() {
        val userId = 1L
        val user = AppUser().apply {
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        val response = mockMvcHandler.doPut("/api/auth/users/admin/$userId", jacksonUser.write(user).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_updateUserAdmin_lacksRole() {
        val userId = 1L
        val user = AppUser().apply {
            userName = "userName"
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val response = mockMvcHandler.doPut("/api/auth/users/admin/$userId", jacksonUser.write(user).json)
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun test_updateUserSelf() {
        TODO("Fix this")
//        val user = AppUser(userName = "userName")
//        mockMvcHandler.token = jwtTokenProvider.createToken(user)
//
//        `when`(authService.updateUserSelf(user))
//                .thenReturn(user)
//
//        val response = mockMvcHandler.doPut("/api/auth/users/self", jacksonUser.write(user).json)
//
//        assertThat(response, allOf(
//                hasProperty("status", equalTo(200)),
//                responseBody(equalTo(jacksonUser.write(user).json))
//        ))
    }

    @Test
    fun test_updateUserSelf_unauthorized() {
        TODO("Fix this")
//        val user = AppUser(userName = "userName")
//
//        `when`(authService.updateUserSelf(user))
//                .thenReturn(user)
//
//        val response = mockMvcHandler.doPut("/api/auth/users/self", jacksonUser.write(user).json)
//
//        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_updateUserSelf_notFound() {
        val user = AppUser(userName = "userName")
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doPut("/api/auth/users/self", jacksonUser.write(user).json)

        assertThat(response, hasProperty("status", equalTo(204)))
    }

    @Test
    fun test_deleteUser() {
        TODO("Fix this")
//        val userId = 1L
//        val user = AppUser().apply {
//            this.userId = userId
//            userName = "userName"
//            roles = listOf(Role(name = ROLE_ADMIN))
//        }
//        mockMvcHandler.token = jwtTokenProvider.createToken(user)
//
//        `when`(authService.deleteUser(userId))
//                .thenReturn(user)
//
//        val response = mockMvcHandler.doDelete("/api/auth/users/$userId")
//
//        assertThat(response, allOf(
//                hasProperty("status", equalTo(200)),
//                responseBody(equalTo(jacksonUser.write(user).json))
//        ))
    }

    @Test
    fun test_deleteUser_notFound() {
        val userId = 1L
        val user = AppUser().apply {
            this.userId = userId
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doDelete("/api/auth/users/$userId")
        assertThat(response, hasProperty("status", equalTo(204)))
    }

    @Test
    fun test_deleteUser_unauthorized() {
        val userId = 1L
        val response = mockMvcHandler.doDelete("/api/auth/users/$userId")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_deleteUser_lacksRole() {
        val userId = 1L
        val user = AppUser().apply {
            this.userId = userId
            userName = "userName"
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doDelete("/api/auth/users/$userId")
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun test_revokeAccess() {
        TODO("Fix this")
//        val user = AppUser().apply {
//            userId = 1L
//            userName = "userName"
//            roles = listOf(Role(name = ROLE_ADMIN))
//        }
//        mockMvcHandler.token = jwtTokenProvider.createToken(user)
//
//        `when`(authService.revokeAccess(1L))
//                .thenReturn(user)
//
//        val response = mockMvcHandler.doPost("/api/auth/users/revoke/1", jacksonUser.write(user).json)
//        assertThat(response, allOf(
//                hasProperty("status", equalTo(200)),
//                responseBody(equalTo(jacksonUser.write(user).json))
//        ))
    }

    @Test
    fun test_revokeAccess_unauthorized() {
        val user = AppUser().apply {
            userId = 1L
            userName = "userName"
        }
        val response = mockMvcHandler.doPost("/api/auth/users/revoke/1", jacksonUser.write(user).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_revokeAccess_lacksRole() {
        val user = AppUser().apply {
            userId = 1L
            userName = "userName"
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val response = mockMvcHandler.doPost("/api/auth/users/revoke/1", jacksonUser.write(user).json)
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun test_checkAuth() {
        TODO("Fix this")
//        val user = AppUser().apply {
//            userId = 1L
//            userName = "userName"
//        }
//        mockMvcHandler.token = jwtTokenProvider.createToken(user)
//
//        `when`(authService.checkAuth())
//                .thenReturn(user)
//
//        val response = mockMvcHandler.doGet("/api/auth/check")
//        assertThat(response, allOf(
//                hasProperty("status", equalTo(200)),
//                responseBody(equalTo(jacksonUser.write(user).json))
//        ))
    }

    @Test
    fun test_checkAuth_unauthorized() {
        val response = mockMvcHandler.doGet("/api/auth/check")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_getVideoToken() {
        val user = AppUser().apply {
            userId = 1L
            userName = "userName"
        }
        val videoId = 10L
        val token = VideoToken("ABCDEFG")

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

    @Test
    fun test_logout() {
        val response = mockMvcHandler.doGet("/api/auth/logout")
        assertThat(response, allOf(
                hasProperty("status", equalTo(204)),
                header("Set-Cookie", equalTo("vm_token=; Path=/; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT; Secure; HttpOnly; SameSite=strict"))
        ))
    }

    @Test
    fun test_createCookie() {
        val token = "ABCDEFG"
        val cookie = authController.createCookie(token, DEFAULT_MAX_AGE)
        assertThat(cookie, allOf(
                hasProperty("name", equalTo(COOKIE_NAME)),
                hasProperty("value", equalTo(token)),
                hasProperty("secure", equalTo(true)),
                hasProperty("httpOnly", equalTo(true)),
                hasProperty("maxAge", equalTo(Duration.ofSeconds(DEFAULT_MAX_AGE))),
                hasProperty("sameSite", equalTo("strict"))
        ))
    }

}
