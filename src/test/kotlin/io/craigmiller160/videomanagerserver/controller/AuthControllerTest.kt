package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import io.craigmiller160.videomanagerserver.service.security.AuthService
import io.craigmiller160.videomanagerserver.test_util.responseBody
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.isEmptyString
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


@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class AuthControllerTest {

    companion object {
        private const val ROLE = "MyRole"
    }

    @Mock
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var authController: AuthController

    private lateinit var jacksonUser: JacksonTester<AppUser>
    private lateinit var jacksonToken: JacksonTester<Token>
    private lateinit var jacksonRoles: JacksonTester<List<Role>>

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @Autowired
    private lateinit var webAppContext: WebApplicationContext

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Before
    fun setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
                .build()
        mockMvcHandler = MockMvcHandler(mockMvc)

        JacksonTester.initFields(this, ObjectMapper())
        MockitoAnnotations.initMocks(this)
        ReflectionTestUtils.setField(authController, "authService", authService)
    }

    @Test
    fun test_login() {
        val request = AppUser().apply {
            userName = "userName"
            password = "password"
        }
        val token = Token("ABCDEFG")
        `when`(authService.login(request))
                .thenReturn(token)

        val response = mockMvcHandler.doPost("/auth/login", jacksonUser.write(request).json)
        assertOkResponse(response, jacksonToken.write(token).json)
    }

    @Test
    fun test_login_badLogin() {
        val badRequest = AppUser().apply {
            userName = "userName"
            password = "bad_password"
        }
        `when`(authService.login(badRequest))
                .thenThrow(ApiUnauthorizedException("Invalid login"))

        val response = mockMvcHandler.doPost("/auth/login", jacksonUser.write(badRequest).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_getRoles_unauthorized() {
        val roles = listOf(Role(name = ROLE))
        `when`(authService.getRoles())
                .thenReturn(roles)

        val response = mockMvcHandler.doGet("/auth/roles")

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

        val response = mockMvcHandler.doGet("/auth/roles")

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

        val response = mockMvcHandler.doGet("/auth/roles")

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

        val response = mockMvcHandler.doGet("/auth/roles")

        assertThat(response, allOf(
                hasProperty("status", equalTo(204)),
                responseBody(isEmptyString())
        ))
    }

    @Test
    fun test_createUser() {
        TODO("Finish this")
    }

    @Test
    fun test_createUser_unauthorized() {
        TODO("Finish this")
    }

    @Test
    fun test_createUser_lacksRole() {
        TODO("Finish this")
    }

    @Test
    fun test_getAllUsers() {
        TODO("Finish this")
    }

    @Test
    fun test_getAllUsers_noUsers() {
        TODO("Finish this")
    }

    @Test
    fun test_getAllUsers_unauthorized() {
        TODO("Finish this")
    }

    @Test
    fun test_getAllUsers_lacksRole() {
        TODO("Finish this")
    }

    @Test
    fun test_getUser() {
        TODO("Finish this")
    }

    @Test
    fun test_getUser_notFound() {
        TODO("Finish this")
    }

    @Test
    fun test_getUser_unauthorized() {
        TODO("Finish this")
    }

    @Test
    fun test_getUser_lacksRole() {
        TODO("Finish this")
    }

    @Test
    fun test_updateUser() {
        TODO("Finish this")
    }

    @Test
    fun test_updateUser_notFound() {
        TODO("Finish this")
    }

    @Test
    fun test_updateUser_unauthorized() {
        TODO("Finish this")
    }

    @Test
    fun test_updateUser_lacksRole() {
        TODO("Finish this")
    }

}