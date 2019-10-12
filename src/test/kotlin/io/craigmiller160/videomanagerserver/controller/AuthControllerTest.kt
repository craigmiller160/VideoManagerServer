package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.controller.AuthController.Companion.DEFAULT_MAX_AGE
import io.craigmiller160.videomanagerserver.dto.AppUser
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
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.Duration


@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class AuthControllerTest {

    companion object {
        private const val ROLE = "MyRole"
        private const val USER_NAME = "UserName"
        private const val PASSWORD = "password"
    }

    @Mock
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var authController: AuthController

    private lateinit var jacksonUser: JacksonTester<AppUser>
    private lateinit var jacksonRoles: JacksonTester<List<Role>>
    private lateinit var jacksonUserList: JacksonTester<List<AppUser>>
    private lateinit var jacksonVideoToken: JacksonTester<VideoToken>

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @Autowired
    private lateinit var webAppContext: WebApplicationContext

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Before
    fun setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
                .build()
        mockMvcHandler = MockMvcHandler(mockMvc)

        JacksonTester.initFields(this, objectMapper)
        MockitoAnnotations.initMocks(this)
        ReflectionTestUtils.setField(authController, "authService", authService)
    }

    @Test
    fun test_login() {
        val request = AppUser().apply {
            userName = "userName"
            password = "password"
        }
        val token = "ABCDEFG"
        `when`(authService.login(request))
                .thenReturn(token)

        val response = mockMvcHandler.doPost("/api/auth/login", jacksonUser.write(request).json)
        assertThat(response, allOf(
                hasProperty("status", equalTo(204)),
                header("Set-Cookie", matchesPattern("vm_token=ABCDEFG; Path=/; Max-Age=1000000; Expires=.+; Secure; HttpOnly; SameSite=strict"))
        ))
    }

    @Test
    fun test_login_badLogin() {
        val badRequest = AppUser().apply {
            userName = "userName"
            password = "bad_password"
        }
        `when`(authService.login(badRequest))
                .thenThrow(ApiUnauthorizedException("Invalid login"))

        val response = mockMvcHandler.doPost("/api/auth/login", jacksonUser.write(badRequest).json)
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
        val userRequest = AppUser().apply {
            userName = USER_NAME
            password = PASSWORD
        }
        val user = AppUser().apply {
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        val userResponse = userRequest.copy(userId = 1L)
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        `when`(authService.createUser(userRequest))
                .thenReturn(userResponse)

        val response = mockMvcHandler.doPost("/api/auth/users", jacksonUser.write(userRequest).json)

        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonUser.write(userResponse).json))
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

        val users = listOf(user)
        `when`(authService.getAllUsers())
                .thenReturn(users)

        val response = mockMvcHandler.doGet("/api/auth/users")

        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonUserList.write(users).json))
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
        val userId = 1L
        val user = AppUser().apply {
            this.userId = userId
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        `when`(authService.getUser(userId))
                .thenReturn(user)

        val response = mockMvcHandler.doGet("/api/auth/users/$userId")

        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonUser.write(user).json))
        ))
    }

    @Test
    fun test_getUser_notFound() {
        val user = AppUser().apply {
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doGet("/api/auth/users")
        assertThat(response, hasProperty("status", equalTo(204)))
    }

    @Test
    fun test_getUser_unauthorized() {
        val response = mockMvcHandler.doGet("/api/auth/users")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_getUser_lacksRole() {
        val user = AppUser().apply {
            userName = "userName"
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doGet("/api/auth/users")
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun test_updateUserAdmin() {
        val userId = 1L
        val user = AppUser().apply {
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val userResponse = user.copy(userId = userId)

        `when`(authService.updateUserAdmin(userId, user))
                .thenReturn(userResponse)

        val response = mockMvcHandler.doPut("/api/auth/users/admin/$userId", jacksonUser.write(user).json)

        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonUser.write(userResponse).json))
        ))
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
        val user = AppUser(userName = "userName")
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        `when`(authService.updateUserSelf(user))
                .thenReturn(user)

        val response = mockMvcHandler.doPut("/api/auth/users/self", jacksonUser.write(user).json)

        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonUser.write(user).json))
        ))
    }

    @Test
    fun test_updateUserSelf_unauthorized() {
        val user = AppUser(userName = "userName")

        `when`(authService.updateUserSelf(user))
                .thenReturn(user)

        val response = mockMvcHandler.doPut("/api/auth/users/self", jacksonUser.write(user).json)

        assertThat(response, hasProperty("status", equalTo(401)))
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
        val userId = 1L
        val user = AppUser().apply {
            this.userId = userId
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        `when`(authService.deleteUser(userId))
                .thenReturn(user)

        val response = mockMvcHandler.doDelete("/api/auth/users/$userId")

        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonUser.write(user).json))
        ))
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
        val user = AppUser().apply {
            userId = 1L
            userName = "userName"
            roles = listOf(Role(name = ROLE_ADMIN))
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        `when`(authService.revokeAccess(user))
                .thenReturn(user)

        val response = mockMvcHandler.doPost("/api/auth/users/revoke", jacksonUser.write(user).json)
        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonUser.write(user).json))
        ))
    }

    @Test
    fun test_revokeAccess_unauthorized() {
        val user = AppUser().apply {
            userId = 1L
            userName = "userName"
        }
        val response = mockMvcHandler.doPost("/api/auth/users/revoke", jacksonUser.write(user).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_revokeAccess_lacksRole() {
        val user = AppUser().apply {
            userId = 1L
            userName = "userName"
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val response = mockMvcHandler.doPost("/api/auth/users/revoke", jacksonUser.write(user).json)
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun test_checkAuth() {
        val user = AppUser().apply {
            userId = 1L
            userName = "userName"
        }
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        `when`(authService.checkAuth())
                .thenReturn(user)

        val response = mockMvcHandler.doGet("/api/auth/check")
        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonUser.write(user).json))
        ))
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
